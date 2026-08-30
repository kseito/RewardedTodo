package jp.kztproject.rewardedtodo.domain.todo

/**
 * 現在時刻の取得を差し替え可能にするための抽象。
 *
 * アクセストークンの有効期限判定と、`expires_in`(相対秒)から絶対時刻への変換を
 * テストから制御できるようにする。application層とdata層の双方が使うため
 * domain層に置いている（`docs/module-dependency.md` の依存方向ルールによる）。
 */
fun interface CurrentTimeProvider {
    fun nowMillis(): Long
}
