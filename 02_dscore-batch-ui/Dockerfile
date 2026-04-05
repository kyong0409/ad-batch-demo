FROM node:22-alpine AS build
WORKDIR /app
COPY package.json ./
RUN npm install --registry=https://registry.npmjs.org/
COPY . .
ENV VITE_API_BASE_URL=/api
ENV VITE_ENABLE_MOCK=false
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
