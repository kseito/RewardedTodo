#!/usr/bin/env bash
# E2E用のWireMockをバックグラウンドで起動し、応答を返すまで待つ。
#
# アプリは `10.0.2.2:8080` で、Maestroのスクリプトは `localhost:8080` で同じサーバーに届く。
set -euo pipefail

PORT="${WIREMOCK_PORT:-8080}"
ROOT_DIR="maestro-tests/wiremock"
JAR="build/wiremock/wiremock-standalone.jar"
LOG="build/wiremock/wiremock.log"

./gradlew syncWireMockJar

nohup java -jar "$JAR" --port "$PORT" --root-dir "$ROOT_DIR" --local-response-templating \
  > "$LOG" 2>&1 &
echo $! > build/wiremock/wiremock.pid

for _ in $(seq 1 60); do
  if curl -sf -m 2 "http://localhost:${PORT}/__admin/mappings" > /dev/null; then
    echo "WireMock is ready on port ${PORT}"
    exit 0
  fi
  sleep 1
done

echo "WireMock did not become ready within 60s" >&2
cat "$LOG" >&2
exit 1
