#!/usr/bin/env bash
# SessionStart hook. Prints a single line summarising the working state so
# Claude can pick up cold: current branch, divergence from main, dirty file
# count, and the most recent CI run status (when `gh` is available).
set -uo pipefail

REPO_ROOT=$(git rev-parse --show-toplevel 2>/dev/null) || exit 0
cd "$REPO_ROOT"

BRANCH=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "?")
AHEAD=$(git rev-list --count main..HEAD 2>/dev/null || echo "?")
MODIFIED=$(git status --porcelain 2>/dev/null | wc -l | tr -d ' ')

CI_INFO=""
if command -v gh >/dev/null 2>&1; then
  CI_INFO=$(gh run list -L 1 --json status,conclusion \
    --jq '.[0] | "CI: \(.status)/\(.conclusion // "—")"' 2>/dev/null) || CI_INFO=""
fi

LINE="📍 branch=${BRANCH} | ahead-of-main=${AHEAD} | modified=${MODIFIED}"
if [ -n "$CI_INFO" ]; then
  LINE="${LINE} | ${CI_INFO}"
fi
echo "$LINE"
