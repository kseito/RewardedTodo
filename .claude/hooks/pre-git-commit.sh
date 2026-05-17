#!/usr/bin/env bash
# PreToolUse hook for Bash. Intercepts `git commit` and runs spotlessCheck
# on the project before allowing the commit to proceed.
#
# Behavior:
#   - exit 0: allow the tool call (no Kotlin files staged, or check passed)
#   - exit 2: block the tool call and surface stderr to Claude
set -uo pipefail

INPUT=$(cat)
COMMAND=$(printf '%s' "$INPUT" | jq -r '.tool_input.command // ""')

# Only intercept commands that invoke `git commit`
if ! printf '%s' "$COMMAND" | grep -qE '(^|[[:space:]]|;|&&|\|\|)git[[:space:]]+commit'; then
  exit 0
fi

REPO_ROOT=$(git rev-parse --show-toplevel 2>/dev/null) || exit 0
cd "$REPO_ROOT"

STAGED_KT=$(git diff --cached --name-only --diff-filter=ACM | grep '\.kt$' || true)
if [ -z "$STAGED_KT" ]; then
  exit 0
fi

if OUTPUT=$(./gradlew --quiet spotlessCheck 2>&1); then
  exit 0
fi

{
  echo "❌ spotlessCheck failed before commit."
  echo
  printf '%s\n' "$OUTPUT" | tail -40
  echo
  echo "Run /format (or ./gradlew spotlessKotlinApply) to fix, then retry the commit."
} >&2
exit 2
