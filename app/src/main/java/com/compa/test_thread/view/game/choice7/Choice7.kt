package com.compa.test_thread.view.game.choice7

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.compa.test_thread.R
import com.compa.test_thread.databinding.AChioce7Binding
import com.compa.test_thread.viewmodel.Choice7VM

class Choice7 : AppCompatActivity(), View.OnClickListener {

    private val binding: AChioce7Binding by lazy {
        DataBindingUtil.setContentView<AChioce7Binding>(
            this, R.layout.a_chioce7
        )
    }
    private val viewModel: Choice7VM by lazy { ViewModelProviders.of(this)[Choice7VM::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.act = this
        binding.vm = viewModel
    }

    /* View.OnClickListener - Start */
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_backActivity -> {
                onBackPressed()
            }
            R.id.btn_action -> {
                viewModel.actionRandom()
            }
        }
    }
    /* View.OnClickListener - End */
}