package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.TodoistAuthSession
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 認可リクエスト中のstateとcode_verifierをメモリ上に保持する。
 *
 * 認可の開始([StartTodoistAuthUseCase])と完了([CompleteTodoistAuthUseCase])が別々の
 * Interactorに分かれているため、両者が共有できるようSingletonで持つ。
 *
 * 永続化はしない。Auth Tabを開いている間だけの一時的な値で、ユーザーは通常すぐ戻ってくる。
 * バックグラウンド中にプロセスが終了すると失われるが、その場合はstate照合に失敗して
 * 「もう一度お試しください」と表示されるだけで復帰可能なため、永続化する価値は薄いと判断した。
 */
@Singleton
class TodoistAuthSessionStore @Inject constructor() {

    // 保存はViewModelのコルーチン、読み出しはAuth Tabのコールバック経由と
    // 別スレッドになりうるため可視性を保証する
    @Volatile
    private var session: TodoistAuthSession? = null

    fun save(session: TodoistAuthSession) {
        this.session = session
    }

    fun get(): TodoistAuthSession? = session

    fun clear() {
        session = null
    }
}
