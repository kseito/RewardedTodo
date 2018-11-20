package kztproject.jp.splacounter.exception

class InvalidUrlException : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)
}