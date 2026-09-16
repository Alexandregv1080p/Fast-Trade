# Roteiro comentado — subindo o Fast Trade no kind

Passo a passo com o que **esperar** em cada etapa e o que fazer quando não acontecer. Se algo divergir do descrito, o bloco "se der errado" logo abaixo do passo tem o diagnóstico.

Tempo total na primeira vez: uns 15 minutos, quase todos em download de imagem.

---

## Passo 0 — Pré-requisitos

```powershell
docker version
kind version
kubectl version --client
helm version
```

**Esperado:** as quatro respondem. Docker Desktop precisa estar rodando (ícone da baleia estável, não girando).

Se faltar algo, no Windows com winget:

```powershell
winget install Kubernetes.kind
winget install Kubernetes.kubectl
winget install Helm.Helm
```

> **Memória.** Docker Desktop → Settings → Resources: reserve pelo menos **6 GB**. A stack sobe backend (JVM), frontend, Postgres e o ingress-controller. Com 4 GB o pod do backend costuma morrer com `OOMKilled` no meio do boot.

---

## Passo 1 — Criar o cluster

O `extraPortMappings` liga a porta 80 do seu Windows na porta 80 do nó — é isso que faz `http://fasttrade.local` funcionar direto no navegador, sem port-forward.

```bash
cat <<'EOF' | kind create cluster --name fasttrade --config -
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
    kubeadmConfigPatches:
      - |
        kind: InitConfiguration
        nodeRegistration:
          kubeletExtraArgs:
            node-labels: "ingress-ready=true"
    extraPortMappings:
      - containerPort: 80
        hostPort: 80
        protocol: TCP
      - containerPort: 443
        hostPort: 443
        protocol: TCP
EOF
```

**Esperado** (~2 min na primeira vez, baixa a imagem do nó):

```
Creating cluster "fasttrade" ...
 ✓ Ensuring node image (kindest/node:v1.31.x)
 ✓ Preparing nodes
 ✓ Writing configuration
 ✓ Starting control-plane
 ✓ Installing CNI
 ✓ Installing StorageClass
Set kubectl context to "kind-fasttrade"
```

Confirme:

```bash
kubectl get nodes
# NAME                      STATUS   ROLES           AGE   VERSION
# fasttrade-control-plane   Ready    control-plane   45s   v1.31.x
```

<details>
<summary><b>Se der errado</b></summary>

- **`failed to create cluster: node(s) already exist`** — já existe um cluster com esse nome. `kind delete cluster --name fasttrade` e repita.
- **`Bind for 0.0.0.0:80 failed: port is already allocated`** — algo já usa a porta 80 no Windows. Comum: IIS, Skype antigo, ou outro container. Descubra com `netstat -ano | findstr :80` e pare o processo — ou troque `hostPort: 80` por `hostPort: 8080` e depois acesse `http://fasttrade.local:8080`.
- **O heredoc `cat <<'EOF'` não funciona no PowerShell.** Rode no Git Bash / WSL, ou salve o YAML como `kind-config.yaml` e use `kind create cluster --name fasttrade --config kind-config.yaml`.
- **Status `NotReady` por mais de 2 min** — quase sempre memória. Veja `docker stats`.

</details>

---

## Passo 2 — Instalar o ingress-nginx

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=180s
```

**Esperado:** vários `created`, depois `pod/ingress-nginx-controller-... condition met`. O `wait` costuma levar de 30 a 90s.

> Use o manifest **`provider/kind`**, não o genérico. Ele já vem com o `nodeSelector: ingress-ready=true` e o `hostPort`, que casam com o config do Passo 1. O manifest genérico cria um Service `LoadBalancer` que fica eternamente em `<pending>` no kind.

<details>
<summary><b>Se der errado</b></summary>

- **`timed out waiting for the condition`** — veja o motivo real: `kubectl -n ingress-nginx get pods` e `kubectl -n ingress-nginx describe pod <nome>`. Se estiver em `Pending` com `didn't match node selector`, o label `ingress-ready=true` não foi aplicado — recrie o cluster com o config do Passo 1.
- **`ImagePullBackOff`** — rede/proxy bloqueando o registry. Confirme com `docker pull registry.k8s.io/ingress-nginx/controller:v1.11.2`.

</details>

---

## Passo 3 — Buildar as imagens

Na raiz do repositório:

```bash
docker build -t fast-trade-backend:local ./backend
docker build -t fast-trade-frontend:local ./frontend
```

**Esperado:** o backend demora bastante na primeira vez (baixa todo o `.m2`) — de 3 a 8 min é normal. Termina com `naming to docker.io/library/fast-trade-backend:local`. Da segunda vez em diante, com o cache de camadas, cai para segundos se você só mexeu em código Java.

Confira o tamanho — é um bom sinal de que o multi-stage funcionou:

```bash
docker images | grep fast-trade
# fast-trade-backend    local   ...   ~250MB
# fast-trade-frontend   local   ...   ~50MB
```

Se o backend vier com mais de 700 MB, o build colapsou num estágio só.

<details>
<summary><b>Se der errado</b></summary>

- **`COPY --from=build /build/dist/easy-trocas-web/browser: not found`** — o `outputPath` do `angular.json` mudou. Verifique com `docker run --rm -it node:20-alpine sh` ou simplesmente rode `npm run build` local e veja onde o `dist/` caiu.
- **Backend falha em `dependency:go-offline`** — geralmente rede/proxy corporativo. Teste `curl -I https://repo1.maven.org/maven2/`.
- **`no space left on device`** — `docker system prune -a` libera imagens antigas.

</details>

---

## Passo 4 — Carregar as imagens no cluster

O kind roda num container isolado; ele **não enxerga** o seu registry local do Docker. É preciso injetar as imagens:

```bash
kind load docker-image fast-trade-backend:local --name fasttrade
kind load docker-image fast-trade-frontend:local --name fasttrade
```

**Esperado:** `Image: "fast-trade-backend:local" with ID "sha256:..." not yet present on node "fasttrade-control-plane", loading...` e volta ao prompt. Leva ~30s por imagem.

Confirme que chegaram:

```bash
docker exec -it fasttrade-control-plane crictl images | grep fast-trade
```

> **Esse é o passo que mais gente esquece.** Se pular, o pod fica em `ErrImageNeverPull` — porque o `values-local.yaml` usa `pullPolicy: Never` justamente para garantir que o cluster use a sua imagem local e não tente buscar num registry inexistente.

---

## Passo 5 — Instalar o chart

```bash
helm install ft ./k8s/fast-trade \
  -f ./k8s/fast-trade/values-local.yaml \
  --namespace fasttrade --create-namespace
```

**Esperado:** `STATUS: deployed` e o texto do `NOTES.txt` com os componentes e o aviso do JWT padrão.

Agora acompanhe a ordem de subida — vale assistir, porque ela mostra o encadeamento funcionando:

```bash
kubectl -n fasttrade get pods -w
```

A sequência esperada, nessa ordem:

```
ft-fast-trade-postgres-0        0/1   ContainerCreating     ← PVC sendo provisionado
ft-fast-trade-postgres-0        1/1   Running               ← banco pronto
ft-fast-trade-frontend-...      1/1   Running               ← nginx sobe rápido, não depende de nada
ft-fast-trade-backend-...       0/1   Init:0/2              ← wait-for-db
ft-fast-trade-migrate-...       0/1   Init:0/2              ← Job de migration disparou (hook post-install)
ft-fast-trade-migrate-...       0/1   Completed             ← Flyway aplicou V1 e V2
ft-fast-trade-backend-...       0/1   Init:1/2              ← wait-for-migrations liberou
ft-fast-trade-backend-...       0/1   Running               ← JVM subindo, startupProbe segurando
ft-fast-trade-backend-...       1/1   Running               ← readiness passou, recebendo tráfego
```

Do `helm install` até o backend `1/1`: de 1 a 3 minutos. **O backend ficar `0/1 Running` por até 90s é normal** — é a `startupProbe` esperando a JVM. Só se preocupe se passar disso ou se o `RESTARTS` começar a subir.

Confira as migrations:

```bash
kubectl -n fasttrade logs job/ft-fast-trade-migrate
# Successfully applied 2 migrations to schema "public", now at version v2
```

<details>
<summary><b>Se der errado</b></summary>

- **Pod em `ErrImageNeverPull`** — Passo 4 não foi feito, ou a tag não bate. Compare `docker images` com o que o pod pede: `kubectl -n fasttrade describe pod <nome> | grep Image:`.
- **`postgres-0` preso em `Pending`** — `kubectl -n fasttrade describe pod ft-fast-trade-postgres-0`. Se disser `no persistent volumes available`, a StorageClass default sumiu; confirme com `kubectl get storageclass` (o kind cria uma `standard`).
- **Backend preso em `Init:0/2` por minutos** — o initContainer não consegue falar com o banco. `kubectl -n fasttrade logs <pod> -c wait-for-db`.
- **Backend preso em `Init:1/2`** — o Job de migration falhou. `kubectl -n fasttrade logs job/ft-fast-trade-migrate` e `kubectl -n fasttrade get jobs`.
- **Backend em `CrashLoopBackOff`** — veja o log: `kubectl -n fasttrade logs <pod> --previous`. Se aparecer `Schema-validation: missing table` ou `wrong column type`, as entidades JPA divergem do `V1__init_schema.sql` — é exatamente o erro que o `ddl-auto: validate` existe para dar.
- **`OOMKilled` no backend** — aumente a memória do Docker Desktop, ou `--set backend.resources.limits.memory=1500Mi`.

</details>

---

## Passo 6 — Apontar o host

Adicione ao `C:\Windows\System32\drivers\etc\hosts` (precisa abrir o editor **como administrador**):

```
127.0.0.1  fasttrade.local
```

Teste:

```bash
curl http://fasttrade.local/api/products
```

**Esperado:** um JSON com os 26 produtos do seed (iPhone, AirPods etc.).

E no navegador, `http://fasttrade.local` deve abrir o painel Angular.

<details>
<summary><b>Se der errado</b></summary>

- **`Could not resolve host`** — o hosts não foi salvo (quase sempre falta de permissão de admin) ou tem cache. `ipconfig /flushdns`.
- **404 do nginx** — o Ingress não casou. `kubectl -n fasttrade describe ingress ft-fast-trade` e confira se o `Host` está como `fasttrade.local`.
- **502 Bad Gateway** — o Ingress achou o Service mas nenhum pod está `Ready`. Volte ao `get pods`.
- **`/api/products` funciona mas o painel dá tela branca** — abra o DevTools. Se o Angular estiver chamando `localhost:8080`, a URL base da API no frontend ainda aponta para o dev server; ajuste para caminho relativo (`/api`).

</details>

---

## Ciclo de trabalho depois de subir

Mudou código do backend:

```bash
docker build -t fast-trade-backend:local ./backend
kind load docker-image fast-trade-backend:local --name fasttrade
kubectl -n fasttrade rollout restart deploy/ft-fast-trade-backend
kubectl -n fasttrade rollout status deploy/ft-fast-trade-backend
```

Como `maxUnavailable: 0`, o pod antigo só sai quando o novo está `Ready` — dá para observar o deploy sem downtime acontecendo ao vivo.

Mudou só o chart:

```bash
helm upgrade ft ./k8s/fast-trade -f ./k8s/fast-trade/values-local.yaml -n fasttrade
```

Adicionou uma migration (`V3__...sql`): rebuild + load do backend, depois `helm upgrade`. O hook `pre-upgrade` roda o Flyway antes dos pods novos.

Comandos que valem ter à mão:

```bash
kubectl -n fasttrade get all                        # visão geral
kubectl -n fasttrade logs -f deploy/ft-fast-trade-backend
kubectl -n fasttrade describe pod <nome>            # eventos: é aqui que o motivo real aparece
kubectl -n fasttrade exec -it ft-fast-trade-postgres-0 -- psql -U fasttrade -d fasttrade
helm -n fasttrade history ft                        # releases, para rollback
```

---

## Para o print do README

Com tudo verde, esses dois dão a foto mais convincente:

```bash
kubectl -n fasttrade get pods,svc,ingress,pvc
kubectl -n fasttrade logs job/ft-fast-trade-migrate | tail -5
```

O primeiro mostra a stack inteira de pé; o segundo prova que as migrations rodaram como parte do deploy.

---

## Derrubar tudo

```bash
helm uninstall ft -n fasttrade
kind delete cluster --name fasttrade
```

O `kind delete` apaga o cluster inteiro, PVC incluso. Para zerar só os dados mantendo o cluster: `helm uninstall ft -n fasttrade && kubectl -n fasttrade delete pvc --all`.
