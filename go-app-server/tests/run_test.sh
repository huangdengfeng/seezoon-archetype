#!/bin/bash
set -e
cd "$(dirname "$0")/.."

go run ./cmd/main.go -conf conf &
trap 'kill $! 2>/dev/null || true' EXIT

sleep 2
go test ./tests/... "$@"
