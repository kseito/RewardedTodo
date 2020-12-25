package jp.kztproject.rewardedtodo.exception

class InvalidUrlException : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)
}