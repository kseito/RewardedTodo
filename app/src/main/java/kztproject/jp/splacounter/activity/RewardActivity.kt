package kztproject.jp.splacounter.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.databinding.ActivityRewardBinding
import kztproject.jp.splacounter.view.fragment.RewardFragment

class RewardActivity : AppCompatActivity() {

    lateinit var binding : ActivityRewardBinding

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, RewardActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reward)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, RewardFragment.newInstance(0))
                .commit()
    }
}
