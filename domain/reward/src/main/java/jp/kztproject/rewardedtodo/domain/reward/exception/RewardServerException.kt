package jp.kztproject.rewardedtodo.domain.reward.exception

class RewardServerException(val code: String) : Exception(code)
class PendingLotteryException : Exception("抽選結果を確認してください")
