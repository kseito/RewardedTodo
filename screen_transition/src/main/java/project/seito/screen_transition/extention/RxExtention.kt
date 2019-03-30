package project.seito.screen_transition.extention

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable = apply { compositeDisposable.add(this) }