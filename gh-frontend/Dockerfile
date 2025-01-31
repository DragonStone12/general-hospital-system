FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./

RUN npm ci --frozen-lockfile

COPY . .

RUN npm run build -- --configuration production --optimization

FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf

COPY default.conf /etc/nginx/conf.d/default.conf

RUN mkdir -p /usr/share/nginx/html/assets

COPY --from=builder /app/dist/gh-frontend/browser/ /usr/share/nginx/html/

# COPY src/assets/env.template.js /usr/share/nginx/html/assets/

# COPY env.sh /docker-entrypoint.d/

# RUN chmod +x /docker-entrypoint.d/env.sh

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
