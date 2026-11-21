#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

if [[ ! -f .env.production ]]; then
  echo ".env.production is required" >&2
  exit 1
fi

docker compose --env-file .env.production -f compose.prod.yml config --quiet
docker compose --env-file .env.production -f compose.prod.yml up -d --build --remove-orphans

for _ in $(seq 1 36); do
  container_id="$(docker compose --env-file .env.production -f compose.prod.yml ps -q springboot)"
  status="$(docker inspect --format='{{.State.Health.Status}}' "$container_id" 2>/dev/null || true)"
  if [[ "$status" == "healthy" ]]; then
    curl --fail --silent http://127.0.0.1:8080/actuator/health
    echo
    exit 0
  fi
  if [[ "$status" == "unhealthy" ]]; then
    docker compose --env-file .env.production -f compose.prod.yml logs --tail=200 springboot
    exit 1
  fi
  sleep 5
done

docker compose --env-file .env.production -f compose.prod.yml ps
docker compose --env-file .env.production -f compose.prod.yml logs --tail=200 springboot
echo "Application health check timed out" >&2
exit 1
