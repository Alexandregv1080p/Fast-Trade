# Fast Trade no Kubernetes

Helm chart que sobe a stack completa — API Spring Boot, painel Angular e PostgreSQL — em qualquer cluster Kubernetes.

```
k8s/fast-trade/
├── Chart.yaml
├── values.yaml            valores padrão (produção-like, imagens no GHCR)
├── values-local.yaml      overrides para kind / k3d / Docker Desktop
└── templates/
    ├── _helpers.tpl        nomes, labels e resolução de imagem
    ├── backend.yaml        Deployment + Service + HPA + PDB
    ├── frontend.yaml       Deployment + Service + HPA + PDB
    ├── postgres.yaml       StatefulSet + Service headless + PVC
    ├── migration-job.yaml  Job Flyway (hook post-install/pre-upgrade)
    ├── configmap.yaml      config não-sensível da API
    ├── secrets.yaml        credenciais do banco e chave JWT
    ├── ingress.yaml        roteamento /api, /ws e /
    ├── serviceaccount.yaml
    └── NOTES.txt
```

As migrations vivem junto do código, não do chart:

```
backend/src/main/resources/db/migration/
├── V1__init_schema.sql          tabelas, FKs e índices
└── V2__seed_reference_data.sql  seed idempotente (admin, catálogo, pedidos)
```

## Arquitetura

```
                    Ingress (fasttrade.local)
                            │
              ┌─────────────┴─────────────┐
              │                           │
         /  (SPA)                  /api  e  /ws
              │                           │
      ┌───────▼───────┐          ┌────────▼────────┐
      │   frontend    │          │     backend     │
      │  nginx :8080  │          │ Spring Boot :8080│
      │  Deployment   │          │   Deployment    │
      └───────────────┘          └────────┬────────┘
                                          │
                                 ┌────────▼────────┐
                                 │    postgres     │
                                 │  StatefulSet    │
                                 │   PVC 5Gi       │
                                 └─────────────────┘
```

O Ingress precisa avaliar `/api` e `/ws` antes de `/` — o template já emite as regras nessa ordem.

## Rodando local com kind

Versão curta abaixo. Para o roteiro comentado — o que esperar em cada passo e como diagnosticar cada erro — veja [RODANDO-LOCAL.md](RODANDO-LOCAL.md).

**Pré-requisitos:** Docker, [kind](https://kind.sigs.k8s.io/), kubectl e Helm 3.

### 1. Criar o cluster com portas expostas

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

### 2. Instalar o ingress-nginx

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=180s
```

### 3. Buildar e carregar as imagens

```bash
docker build -t fast-trade-backend:local ./backend
docker build -t fast-trade-frontend:local ./frontend

kind load docker-image fast-trade-backend:local  --name fasttrade
kind load docker-image fast-trade-frontend:local --name fasttrade
```

### 4. Instalar o chart

```bash
helm install ft ./k8s/fast-trade \
  -f ./k8s/fast-trade/values-local.yaml \
  --namespace fasttrade --create-namespace

kubectl -n fasttrade get pods -w
```

### 5. Apontar o host e acessar

Adicione ao arquivo hosts (`C:\Windows\System32\drivers\etc\hosts` no Windows, precisa de admin):

```
127.0.0.1  fasttrade.local
```

Painel em `http://fasttrade.local`, API em `http://fasttrade.local/api`.

### Atualizar depois de mudar o código

```bash
docker build -t fast-trade-backend:local ./backend
kind load docker-image fast-trade-backend:local --name fasttrade
kubectl -n fasttrade rollout restart deploy/ft-fast-trade-backend
```

`pullPolicy: Never` no `values-local.yaml` garante que o cluster usa a imagem carregada e não tenta buscar num registry.

### Remover tudo

```bash
helm uninstall ft -n fasttrade
kind delete cluster --name fasttrade
```

## O que o chart faz

**Probes.** O backend expõe `/actuator/health/liveness` e `/actuator/health/readiness` (Spring Boot health groups). Uma `startupProbe` dá até 150s para a JVM subir antes da liveness começar a matar o pod — sem ela, uma aplicação Spring lenta entra em CrashLoopBackOff. O frontend usa `/healthz`, servido pelo próprio nginx.

**Rolling update sem downtime.** `maxUnavailable: 0` combinado com readiness probe e `server.shutdown: graceful` no Spring: o pod novo só recebe tráfego depois de pronto, e o antigo drena as conexões antes de morrer.

**Ordem de subida.** Um initContainer roda `pg_isready` em loop até o Postgres aceitar conexões, então o backend nunca sobe contra um banco ausente.

**Recarga de config.** Os Deployments carregam um `checksum/config` derivado do ConfigMap e do Secret. Mudou a config, o hash muda, e o Helm dispara um rollout — sem isso os pods continuariam com os valores antigos em memória.

**Segurança de pod.** Tudo roda como não-root com `readOnlyRootFilesystem`, capabilities dropadas e `seccompProfile: RuntimeDefault`. O frontend usa `nginx-unprivileged` (uid 101, porta 8080) justamente para caber nessas restrições.

**Estado.** O Postgres é StatefulSet com `volumeClaimTemplates`, não Deployment — identidade de rede estável e PVC que sobrevive ao pod ser recriado.

## Migrations

O schema é versionado com Flyway. O Hibernate roda em `ddl-auto: validate` — ele **compara** as entidades com o banco e falha o boot se divergirem, mas nunca altera nada. Quem cria e altera tabela é sempre uma migration.

### Como roda no cluster

O `migration-job.yaml` é um Job com hooks `post-install,pre-upgrade`:

- **`post-install`** — na primeira instalação, roda depois que o Postgres está `Ready`. Um hook `pre-install` não serviria: nessa fase o StatefulSet e o Secret ainda não existem.
- **`pre-upgrade`** — em upgrades, o Helm espera o Job completar **antes** de tocar nos Deployments. O schema novo já está no banco quando os pods novos começam a subir.

O Job tem dois initContainers e um container principal:

1. `wait-for-db` — `pg_isready` em loop.
2. `copy-migrations` — usa a **imagem da própria API** e copia `/migrations/*.sql` para um `emptyDir`. Como os SQLs saem da mesma tag de imagem que o `.jar`, schema e código nunca ficam fora de sincronia.
3. `flyway` — o CLI oficial roda `migrate` contra os arquivos copiados.

O backend ganha um initContainer `wait-for-migrations` que espera aparecer uma linha bem-sucedida em `flyway_schema_history`. Sem isso, na primeira instalação os pods da API subiriam em paralelo com o Job, encontrariam o banco vazio e entrariam em CrashLoopBackOff até o Job terminar — funcionaria por força bruta, mas com ruído.

Quando `migrations.enabled=true`, o chart passa `FLYWAY_ENABLED=false` para a aplicação. **Uma única coisa migra o banco**, e ela roda antes de qualquer pod novo.

### Como roda localmente

Nada muda no seu fluxo: `spring.flyway.enabled` tem default `true`, então `./mvnw spring-boot:run` migra sozinho no boot, como antes.

### Criando uma nova migration

```bash
# backend/src/main/resources/db/migration/V3__add_product_rating.sql
ALTER TABLE products ADD COLUMN rating NUMERIC(2,1);
```

Regras que o Flyway impõe e valem entender:

- **Nunca edite uma migration já aplicada.** O Flyway guarda um checksum de cada arquivo; alterar um já rodado faz o `validate` falhar. Para mudar algo, crie um `V4__`.
- **A numeração é a ordem de execução.** `V10` roda depois de `V9`.
- **Prefira mudanças compatíveis com a versão anterior** (adicionar coluna nullable, criar índice). Como o Job roda antes do rolling update, existe uma janela em que o schema novo convive com pods da versão antiga. Remover ou renomear coluna nessa janela derruba os pods velhos — o padrão para isso é expand/contract: uma release adiciona, outra remove.

### Verificar o estado

```bash
kubectl -n fasttrade logs job/ft-fast-trade-migrate
kubectl -n fasttrade exec -it ft-fast-trade-postgres-0 -- \
  psql -U fasttrade -d fasttrade -c \
  "SELECT version, description, success, installed_on FROM flyway_schema_history ORDER BY installed_rank;"
```

### Bancos que já existem

`baseline-on-migrate: true` com `baseline-version: 0`. Se você já tem um banco criado pelo antigo `ddl-auto: update`, o Flyway o adota em vez de reclamar que o schema não está vazio. Como o `V1` usa `CREATE TABLE IF NOT EXISTS`, ele passa sem quebrar nada.

## Configuração

Valores que costumam mudar por ambiente:

| Valor | Padrão | Descrição |
|-------|--------|-----------|
| `image.registry` | `ghcr.io` | Vazio para imagens locais |
| `image.tag` | appVersion | Tag das imagens |
| `backend.replicaCount` | `2` | Réplicas da API |
| `backend.autoscaling.enabled` | `false` | Liga o HPA (exige metrics-server) |
| `postgresql.enabled` | `true` | `false` usa `externalDatabase.*` |
| `postgresql.persistence.size` | `5Gi` | Tamanho do PVC |
| `ingress.host` | `fasttrade.local` | Hostname público |
| `jwt.secret` | valor de exemplo | **Troque em qualquer ambiente real** |

### Banco gerenciado em vez do StatefulSet

```bash
helm upgrade --install ft ./k8s/fast-trade \
  --set postgresql.enabled=false \
  --set externalDatabase.host=fasttrade.abc123.us-east-1.rds.amazonaws.com \
  --set externalDatabase.existingSecret=rds-credentials
```

### Segredos

O chart gera Secrets a partir de `values.yaml` por conveniência. Para uso real, crie o Secret fora do chart e referencie:

```bash
kubectl create secret generic ft-db \
  --from-literal=username=fasttrade \
  --from-literal=password="$(openssl rand -base64 32)"

helm upgrade --install ft ./k8s/fast-trade \
  --set postgresql.auth.existingSecret=ft-db \
  --set jwt.secret="$(openssl rand -base64 64)"
```

Nunca commite segredos reais no `values.yaml`.

## Validar sem cluster

```bash
helm lint ./k8s/fast-trade
helm template ft ./k8s/fast-trade | kubeconform -strict -summary
```

Ambos rodam no CI a cada push (`.github/workflows/ci.yml`).

## Limitações conscientes

- **Postgres com 1 réplica.** Sem HA nem backup automatizado. Para produção de verdade, use um banco gerenciado (`postgresql.enabled=false`) ou um operator como CloudNativePG.
- **Seed no `V2`.** Dados de demonstração numa migration é ótimo para portfólio e dev, discutível em produção. O caminho usual é separar em `db/seed` e aplicar só em alguns ambientes (`spring.flyway.locations`).
- **Sem rollback automático de migration.** O Flyway Community não tem `undo`. A estratégia correta é escrever migrations compatíveis para frente (expand/contract) em vez de contar com rollback.
- **Sem NetworkPolicy.** Todo pod alcança todo pod. Restringir o acesso ao Postgres apenas ao backend seria o próximo incremento.
- **Sem TLS por padrão.** `ingress.tls.enabled=true` espera um Secret existente; cert-manager resolveria isso de ponta a ponta.
