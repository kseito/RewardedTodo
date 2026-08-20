#!/usr/bin/env bash
# =====================================================================
# お試し版(後で外す): CI上の実動作を可視化するため、1フローだけ実行して録画する。
# 無限ループを避けるため screenrecord は単発起動(最大3分)。
# 本来の全フロー実行版はこのブランチを破棄すれば feature ブランチ側に残る。
# =====================================================================
set -uo pipefail

# --- 画面録画(単発) ---
adb shell 'rm -f /sdcard/rec.mp4' || true
adb shell screenrecord --bit-rate 4000000 --time-limit 180 /sdcard/rec.mp4 &
REC_PID=$!
stop_recording() {
  # 録画中なら SIGINT で正常終了させ(mp4がファイナライズされる)、既に終了済みなら何もしない
  adb shell pkill -SIGINT screenrecord 2>/dev/null || true
  wait "$REC_PID" 2>/dev/null || true
  mkdir -p maestro-recordings
  adb pull /sdcard/rec.mp4 maestro-recordings/ 2>/dev/null || true
}
trap stop_recording EXIT
# --- ここまで ---

# お試しのため1フローのみ実行する
maestro test maestro-tests/single-lottery-flow.yaml
