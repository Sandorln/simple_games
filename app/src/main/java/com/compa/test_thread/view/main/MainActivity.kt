package com.compa.test_thread.view.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.compa.test_thread.R
import com.compa.test_thread.databinding.AMainBinding
import com.compa.test_thread.view.game.random.RandomActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: AMainBinding by lazy { DataBindingUtil.setContentView<AMainBinding>(this, R.layout.a_main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.act = this
    }

    override fun onClick(v: View?) {
        var moveIntent: Intent? = null
        var activityOptions: ActivityOptions? = null

        when (v!!.id) {
            R.id.btn_random -> {
                moveIntent = Intent(this, RandomActivity::class.java)
                activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    findViewById(R.id.btn_random),
                    "btn_title"
                )
            }
        }

        if (moveIntent != null && activityOptions != null)
            startActivity(moveIntent, activityOptions.toBundle())
    }
}