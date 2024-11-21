#!/usr/bin/env bash


docker compose up postgres -d
docker compose up redis -d

echo "sleeping 20s to ensure postgres and redis are available"
sleep 20

docker compose up web -d
docker compose up frontend -d
docker compose up web_proxy -d

