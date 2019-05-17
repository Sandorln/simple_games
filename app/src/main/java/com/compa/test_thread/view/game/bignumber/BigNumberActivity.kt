package com.compa.test_thread.view.game.bignumber

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.compa.test_thread.R
import com.compa.test_thread.databinding.ABignumbergameBinding
import com.compa.test_thread.viewmodel.BigNumberVM

class BigNumberActivity : AppCompatActivity() {

    private val binding: ABignumbergameBinding by lazy {
        DataBindingUtil.setContentView<ABignumbergameBinding>(
            this, R.layout.a_bignumbergame
        )
    }

    private val viewModel: BigNumberVM by lazy { ViewModelProviders.of(this)[BigNumberVM::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.act = this
    }
}