package jp.kztproject.rewardedtodo.application.todo

/**
 * 現在時刻の取得を差し替え可能にするための抽象。
 *
 * アクセストークンの有効期限判定をテストから制御できるようにする。
 */
fun interface CurrentTimeProvider {
    fun nowMillis(): Long
}
