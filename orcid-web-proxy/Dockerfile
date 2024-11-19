#FROM nginx:1.26.2-alpine
FROM nginx:1.18.0

RUN mkdir -p /etc/nginx/snippets
RUN mkdir -p /etc/ssl/certs
COPY certs/* /etc/ssl/certs/
COPY orcid-web-proxy/nginx/snippets/* /etc/nginx/snippets/
COPY orcid-web-proxy/nginx/nginx.conf /etc/nginx/nginx.conf
COPY orcid-web-proxy/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
