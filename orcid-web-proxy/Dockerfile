# debian:buster-20210408-slim
FROM nginx:1.18.0

RUN mkdir -p /etc/nginx/snippets

RUN mkdir -p /etc/ssl/certs

COPY certs/* /etc/ssl/certs/

COPY orcid-web-proxy/nginx/snippets/* /etc/nginx/snippets/

COPY orcid-web-proxy/nginx/templates/* /etc/nginx/templates/

COPY orcid-web-proxy/nginx/nginx.conf /etc/nginx/nginx.conf

COPY orcid-web-proxy/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
