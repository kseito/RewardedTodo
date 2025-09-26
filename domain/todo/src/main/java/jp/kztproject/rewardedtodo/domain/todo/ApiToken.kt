package jp.kztproject.rewardedtodo.domain.todo

@JvmInline
value class ApiToken(val value: String) {

    init {
        require(value.isNotBlank()) { "API Token cannot be blank" }
        require(isValidFormat(value)) { "Invalid API Token format" }
    }

    companion object {
        private fun isValidFormat(token: String): Boolean {
            return token.matches(Regex("[0-9a-f]{40}"))
        }
    }
}