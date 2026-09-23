package jp.kztproject.rewardedtodo.application.todo

interface ClearLocalDataUseCase {

    /**
     * 連携アカウントに紐づくローカルデータを削除する。
     *
     * 別のアカウントで連携し直したときに前のアカウントのTodoやチケット残数が残らないようにする。
     * 報酬一覧はTodoistアカウントと無関係なため削除しない。
     *
     * 削除に失敗しても例外は投げない。認証そのものは成立しており、失敗を理由に連携を
     * 止めると再試行しても同じ場所で弾かれ、アプリに入る手段が無くなるため。
     */
    suspend fun execute()
}
