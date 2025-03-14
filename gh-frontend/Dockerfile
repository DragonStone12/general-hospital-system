FROM node:20-alpine AS builder

COPY package*.json ./

RUN npm ci --frozen-lockfile && mkdir /app && mv ./node_modules ./app/

WORKDIR /app

COPY . .

RUN npm run build -- --configuration production --optimization

FROM nginx:alpine

COPY nginx.default.conf /etc/nginx/conf.d/default.conf

RUN rm -rf /usr/share/nginx/html/*

COPY --from=builder /app/dist/gh-frontend/browser/ /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]
