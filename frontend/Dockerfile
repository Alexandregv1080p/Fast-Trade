# ---------- Stage 1: build ----------
FROM node:20-alpine AS build

WORKDIR /build

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration=production

# ---------- Stage 2: runtime ----------
# Imagem unprivileged: roda como uid 101 e escuta em 8080,
# compativel com runAsNonRoot e readOnlyRootFilesystem no Kubernetes.
FROM nginxinc/nginx-unprivileged:1.27-alpine

# outputPath definido em angular.json (builder `application` gera dist/<app>/browser)
COPY --from=build /build/dist/easy-trocas-web/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
