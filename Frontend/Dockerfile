# ----- Stage 1: Build Angular -----
FROM node:22-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci --prefer-offline

COPY . .
RUN npm run build

# ----- Stage 2: Servir com Nginx -----
FROM nginx:alpine AS runtime

# Remove config default do nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copia o build do Angular para o diretório do nginx
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

# Copia nossa config customizada do nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
