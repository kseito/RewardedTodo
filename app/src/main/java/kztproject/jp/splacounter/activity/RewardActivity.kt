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
        private const val ARG_POINT = "point"

        fun createIntent(context: Context, point: Int): Intent {
            val intent = Intent(context, RewardActivity::class.java)
            intent.putExtra(ARG_POINT, point)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reward)
        val point = intent.getIntExtra(ARG_POINT, 0);
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, RewardFragment.newInstance(point))
                .commit()
    }
}
