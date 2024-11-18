#FROM nginx:1.26.2-alpine
FROM nginx:1.18.0

RUN mkdir -p /etc/nginx/snippets
COPY ./snippets/* /etc/nginx/snippets/
COPY ./default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
