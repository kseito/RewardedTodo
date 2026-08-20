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

# --- お試し(後で外す): エミュレータ画面を録画してCI上の実動作を可視化する ---
# screenrecordは1ファイル最大3分のため、180秒ごとに分割して連続録画する。
adb shell 'rm -rf /sdcard/rec && mkdir -p /sdcard/rec' || true
( i=0; while adb shell screenrecord --bit-rate 4000000 --time-limit 180 "/sdcard/rec/part_$(printf '%03d' "$i").mp4"; do i=$((i+1)); done ) &
REC_LOOP_PID=$!
stop_recording() {
  # SIGINTで送るとmp4が正常にファイナライズされる(SIGKILLだと壊れる)
  adb shell pkill -SIGINT screenrecord 2>/dev/null || true
  sleep 3
  kill "$REC_LOOP_PID" 2>/dev/null || true
  wait "$REC_LOOP_PID" 2>/dev/null || true
  mkdir -p maestro-recordings
  adb pull /sdcard/rec/. maestro-recordings/ 2>/dev/null || true
}
trap stop_recording EXIT
# --- ここまでお試し ---

if maestro test maestro-tests/; then
  exit 0
fi

echo "::warning::Some Maestro flows failed. Retrying the full suite once..."
maestro test maestro-tests/
