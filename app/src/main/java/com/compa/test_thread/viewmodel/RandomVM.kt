package com.compa.test_thread.viewmodel

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

/**
 *
 * Random Game ViewModel
 * _ 숫자가 입력되지 않았을 시 Start 버튼 비활성화
 * _ 숫자가 입력되었을 시 Start 버튼 활성화
 *
 * _ Start 버튼이 눌렸을 시
 * _ 1) 0.1초 마다 무작위의 숫자로 변경
 * _ 2) Start 버튼이 Stop 버튼으로 변경
 * _ 3) Stop 버튼을 눌러서 '입력한 숫자'와 '무작위 숫자'와 동일할 시 승리 / 아닐 시 패배
 */
class RandomVM : ViewModel(), TextWatcher {

    private val _randomNum = MutableLiveData<String>().apply { value = "1 ~ 10" }
    val randomNum: LiveData<String> get() = _randomNum

    val selectNum = MutableLiveData<String>().apply { value = "0" }

    private val _isBtnAction = MutableLiveData<Boolean>().apply { value = false }
    val isBtnAction: LiveData<Boolean> get() = _isBtnAction

    /* Thread 시작 / 중지 */
    private val _isStart = MutableLiveData<Boolean>().apply { value = true }
    val isStart: LiveData<Boolean> get() = _isStart

    /**
     * Button Action
     * _ _isStart : True -> Thread 시작
     * _ 시작 시 _ RandomNum : 0.1 초 마다 변경
     * _ _isStart : False -> Thread 중지
     * _ 중지 시 _ RandomNum | SelectNum : 비교
     */
    fun actionRandom(context: Context) {
        if (_isStart.value!!) {
            _isStart.value = false
            object : Thread(randomRunnable) {}.start()
        } else {
            _isStart.value = true
            checkValues(context)
        }
    }

    /**
     * RandomNum | SelectNum 비교 후 사용자에게 알려주기
     * _ AlertDialog 활용
     */
    private fun checkValues(context: Context) {
        val title: String
        val body: String

        if (selectNum.value!!.toInt() == _randomNum.value!!.toInt()) {
            title = "축하드립니다!"
            body = "숫자를 맞추셨습니다!\n잘하셨습니다!"
        } else {
            title = "아쉽습니다.."
            body = "안타깝게도..\n맞추지 못하셨네요.."
        }

        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(body)
            .setPositiveButton("확인", null)
            .create()
            .show()
    }

    /* TextWatcher - Start */
    override fun afterTextChanged(s: Editable?) {
        _isBtnAction.value = if (selectNum.value!!.isNotEmpty())
            selectNum.value!!.toInt() in 1..10 else
            false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
    /* TextWatcher - End */

    /* Thread 처리 */
    private val randomRunnable = Runnable {
        while (!(_isStart.value!!)) {
            Thread.sleep(50)
            if (!(_isStart.value!!))
                _randomNum.postValue(Random(System.currentTimeMillis()).nextInt(1, 11).toString())
        }
    }
}