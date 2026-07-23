# Stage 1: Build Angular application
FROM node:22 AS build

WORKDIR /app

COPY package*.json .

RUN npm install

COPY . .

RUN npm run build


# Stage 2: Serve static files
FROM nginx:alpine

COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html


COPY nginx.conf /etc/nginx/conf.d/default.conf