#!/usr/bin/env bash
set -euo pipefail
curl --fail http://localhost:8080/actuator/health
