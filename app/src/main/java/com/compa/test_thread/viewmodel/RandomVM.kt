package com.compa.test_thread.viewmodel

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

/**
 *
 * Random Game ViewModel
 * _ 무작위로 변하는 숫자 중 7 맞추기 게임
 *
 * _ Start 버튼이 눌렸을 시
 * _ 1) GameDelay (1000 = 1초) 가 지날 시 무작위의 숫자로 변경
 * _ 2) Start 버튼이 Choice 버튼으로 변경
 * _ 3) Choice 버튼을 눌러서 '무작위 숫자' 가 7일 시 점수 획득 / 아닐 시 생명 감소
 * _ 4) '무작위 숫자' 가 7이 아닌 상태로 다음 숫자로 변할 시 점수 획득 / 7인 상태로 다음 숫자로 변할 시 생명 감소
 * _ 5) 점수 획득 마다 0.05초 감소 (최대 0.5초 까지 감소 / 기본 1.5초)
 */
class RandomVM : ViewModel(), TextWatcher {

    private var _randomNum = MutableLiveData<String>().apply { value = "1 ~ 9" }
    val randomNum: LiveData<String> get() = _randomNum

    val selectNum = MutableLiveData<String>().apply { value = "0" }

    private val _isBtnAction = MutableLiveData<Boolean>().apply { value = false }
    val isBtnAction: LiveData<Boolean> get() = _isBtnAction

    /* Thread 시작 / 중지 */
    private val _isStart = MutableLiveData<Boolean>().apply { value = true }
    val isStart: LiveData<Boolean> get() = _isStart

    val gameDelayTime = MutableLiveData<Int>().apply { value = 1500 }
    val nowDelayTime = MutableLiveData<Int>().apply { value = 0 }

    private lateinit var gameThread: Thread

    /**
     * Button Action
     * _ _isStart : True -> Thread 시작
     * _ 시작 시 _ RandomNum : GameDelay 가 지날 시 변경
     */
    fun actionRandom(context: Context) {
        if (_isStart.value!!) {
            _isStart.value = false
            selectNum.value = selectNum.value!!.toInt().toString()
            gameThread = Thread(randomRunnable)
            gameThread.start()
        } else {
            _isStart.value = true

            gameThread.interrupt()
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

            Log.d("randomGame", "현재 값 : ${_randomNum.value} / ${randomNum.value}")

            gameDelayTime.value = when {
                gameDelayTime.value!! > 400 -> gameDelayTime.value!! - 200
                gameDelayTime.value!! > 110 -> gameDelayTime.value!! - 10
                gameDelayTime.value!! > 55 -> -5
                else -> 50
            }

        } else {
            title = "아쉽습니다.."
            body = "안타깝게도..\n맞추지 못하셨네요.."
        }

        createGameResultDialog(title, body, context)
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
        _randomNum.postValue(Random(System.currentTimeMillis()).nextInt(1, 11).toString())
        nowDelayTime.postValue(0)
        var nowTime = 0

        while (!(_isStart.value!!)) {
            try {
                Thread.sleep(10)
                nowTime += 10
                nowDelayTime.postValue(nowTime)

                if (nowTime >= gameDelayTime.value!! && !(_isStart.value!!)) {
                    _randomNum.postValue(Random(System.currentTimeMillis()).nextInt(1, 11).toString())

                    nowTime = 0
                    nowDelayTime.postValue(0)
                }
            } catch (e: InterruptedException) {
                _randomNum.postValue(randomNum.value)
            }
        }
    }

    companion object {
        fun createGameResultDialog(title: String, body: String, context: Context) {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton("확인", null)
                .create()
                .show()
        }
    }
}