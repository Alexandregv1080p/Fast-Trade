{{/* Nome base do chart */}}
{{- define "fast-trade.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Nome completo do release */}}
{{- define "fast-trade.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/* Labels comuns a todos os recursos */}}
{{- define "fast-trade.labels" -}}
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" }}
app.kubernetes.io/name: {{ include "fast-trade.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: fast-trade
{{- end -}}

{{/* Selector labels de um componente. Uso: (dict "ctx" $ "component" "backend") */}}
{{- define "fast-trade.selectorLabels" -}}
app.kubernetes.io/name: {{ include "fast-trade.name" .ctx }}
app.kubernetes.io/instance: {{ .ctx.Release.Name }}
app.kubernetes.io/component: {{ .component }}
{{- end -}}

{{/* ServiceAccount */}}
{{- define "fast-trade.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "fast-trade.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- default "default" .Values.serviceAccount.name -}}
{{- end -}}
{{- end -}}

{{/* Nome do Secret do banco */}}
{{- define "fast-trade.dbSecretName" -}}
{{- if .Values.postgresql.enabled -}}
{{- default (printf "%s-db" (include "fast-trade.fullname" .)) .Values.postgresql.auth.existingSecret -}}
{{- else -}}
{{- default (printf "%s-db" (include "fast-trade.fullname" .)) .Values.externalDatabase.existingSecret -}}
{{- end -}}
{{- end -}}

{{/* Nome do Secret do JWT */}}
{{- define "fast-trade.jwtSecretName" -}}
{{- default (printf "%s-jwt" (include "fast-trade.fullname" .)) .Values.jwt.existingSecret -}}
{{- end -}}

{{/* Host do banco: Service do StatefulSet ou banco externo */}}
{{- define "fast-trade.dbHost" -}}
{{- if .Values.postgresql.enabled -}}
{{- printf "%s-%s" (include "fast-trade.fullname" .) .Values.postgresql.name -}}
{{- else -}}
{{- required "externalDatabase.host e obrigatorio quando postgresql.enabled=false" .Values.externalDatabase.host -}}
{{- end -}}
{{- end -}}

{{- define "fast-trade.dbPort" -}}
{{- if .Values.postgresql.enabled -}}
{{- .Values.postgresql.service.port -}}
{{- else -}}
{{- .Values.externalDatabase.port -}}
{{- end -}}
{{- end -}}

{{- define "fast-trade.dbName" -}}
{{- if .Values.postgresql.enabled -}}
{{- .Values.postgresql.auth.database -}}
{{- else -}}
{{- .Values.externalDatabase.database -}}
{{- end -}}
{{- end -}}

{{/* Imagem de um componente. Uso: (dict "ctx" $ "component" .Values.backend) */}}
{{- define "fast-trade.image" -}}
{{- $tag := .component.image.tag | default .ctx.Values.image.tag | default .ctx.Chart.AppVersion -}}
{{- $repo := printf "%s-%s" .ctx.Values.image.repository .component.image.name -}}
{{- if .ctx.Values.image.registry -}}
{{- printf "%s/%s:%s" .ctx.Values.image.registry $repo $tag -}}
{{- else -}}
{{- printf "%s:%s" $repo $tag -}}
{{- end -}}
{{- end -}}
