package com.compa.test_thread.view.game.random

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.compa.test_thread.R
import com.compa.test_thread.databinding.ARandomgameBinding
import com.compa.test_thread.viewmodel.RandomVM
import kotlinx.android.synthetic.main.a_randomgame.*

class RandomActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ARandomgameBinding by lazy {
        DataBindingUtil.setContentView<ARandomgameBinding>(
            this, R.layout.a_randomgame
        )
    }
    private val viewModel: RandomVM by lazy { ViewModelProviders.of(this)[RandomVM::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.act = this
        binding.vm = viewModel

        edit_select.addTextChangedListener(viewModel)
    }

    /* View.OnClickListener - Start */
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_backActivity -> {
                onBackPressed()
            }
            R.id.btn_action -> {
                viewModel.actionRandom(this)
            }
        }
    }
    /* View.OnClickListener - End */
}