#!/usr/bin/env bash
# Maestro E2E フローを実行する。
#
# フォルダ一括実行にする理由:
#   アプリのcold start(初回JIT/描画)が重く、個別フロー実行だと各フローが毎回coldになり
#   launchApp直後の tapOn がタイムアウトして失敗する。フォルダ一括実行なら最初の1フローで
#   温まり、以降のフローは安定して通る。
#
# Flaky対策:
#   launchApp直後のタブタップがまれに起動完了前になりFlakyになるため、
#   一括実行が失敗したらスイート全体を1回だけリトライする(2回目はJIT済みで安定しやすい)。
#
# 注: android-emulator-runner の `script` は各行を個別の sh で実行するため、
# 複数行のこの処理はスクリプトに切り出して1行で呼び出している。
set -uo pipefail

if maestro test maestro-tests/; then
  exit 0
fi

echo "::warning::Some Maestro flows failed. Retrying the full suite once..."
maestro test maestro-tests/
