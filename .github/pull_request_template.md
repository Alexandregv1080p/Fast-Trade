## O que muda

<!-- 1–3 linhas: o que este PR faz e por quê. Link do card/issue se houver. -->

## Tipo

- [ ] Feature
- [ ] Bugfix
- [ ] Refactor / dívida técnica
- [ ] Infra / CI

## Checklist

- [ ] Build verde localmente (`mvn verify` no backend / `./gradlew testDebugUnitTest assembleDebug` no android)
- [ ] Testes cobrindo o que mudou (obrigatório em caminho de dinheiro/segurança)
- [ ] Formatação aplicada (`mvn spotless:apply` / `./gradlew lintDebug`)
- [ ] Mudou schema? Tem migration Flyway versionada (nunca editei uma migration já aplicada)
- [ ] Mudou contrato de API? DTO + `@Valid` + reflete no Swagger
- [ ] Sem segredo commitado (env/Secret, não valor no repo)

## Como testei

<!-- Passos ou evidência (print, log, resposta de request). -->
