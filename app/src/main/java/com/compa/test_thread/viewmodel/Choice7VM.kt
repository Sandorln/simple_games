package com.compa.test_thread.viewmodel

import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
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
class Choice7VM : ViewModel() {

    /* Game State Info */
    private val _level = MutableLiveData<Int>().apply { value = 1 }
    val level: LiveData<Int> get() = _level

    private val _score = MutableLiveData<Int>().apply { value = 0 }
    val score: LiveData<Int> get() = _score

    private val _life = MutableLiveData<Int>().apply { value = 0 }
    val life: LiveData<Int> get() = _life


    /* Game Object */
    private val _randomNum = MutableLiveData<String>().apply { value = "1 ~ 9" }
    val randomNum: LiveData<String> get() = _randomNum


    /* Game Time */
    val gameDelayTime = MutableLiveData<Int>().apply { value = 1500 }

    private var nowTime = 0
    val nowDelayTime = MutableLiveData<Int>().apply { value = 0 }

    /* Thread 시작 / 중지 */
    private lateinit var gameThread: Thread

    private val _isReady = MutableLiveData<Boolean>().apply { value = true }
    val isReady: LiveData<Boolean> get() = _isReady

    private var isChoice = false
    private var isGaming = false

    val isResult = MutableLiveData<Boolean>().apply { value = false }

    /**
     * Button Action
     * _ _isReady : True -> Thread 시작
     * _ 시작 시 _ RandomNum : GameDelay 가 지날 시 변경
     */
    fun actionRandom() {
        if (_isReady.value!!) {
            _isReady.value = false
            isGaming = true
            isResult.value = false

            _life.value = 3
            _level.value = 1
            _score.value = 0

            gameThread = Thread(gameRunnable)
            gameThread.start()
        } else {
            userChoiceNumber()
        }
    }

    /**
     * Game Over
     *
     * _ 사용자가 획득한 점수 표시
     */
    private fun endGame() {
        _isReady.postValue(true)
        isResult.postValue(true)
        isGaming = false

        gameThread.interrupt()
    }

    /**
     * RandomNum | 7 비교 하기
     *
     * _ 7과 같을 경우
     * _ 1) Score 획득
     * _ 2) Level 상승 ( 획득 Score 증가, gameDelayTime 감소 )
     *
     * _ 7과 다를 경우
     * _ 1) life 감소 ( life 0 일 시 Game 종료 )
     * _ 2) Level 초기화 ( level 0 으로 변경 )
     *
     */
    private fun userChoiceNumber() {
        isChoice = true
        gameLevelUpDown(7 == _randomNum.value!!.toInt())
    }

    private fun userNOTChoiceNumber() {
        isChoice = false
        gameLevelUpDown(7 != _randomNum.value!!.toInt())
    }

    private fun gameLevelUpDown(isLvUP: Boolean) {
        if (isLvUP) {
            _score.postValue(_score.value!! + _level.value!! * 500)
            _level.postValue(_level.value!! + 1)

            gameDelayTime.postValue(
                when {
                    gameDelayTime.value!! > 400 -> gameDelayTime.value!! - 200
                    gameDelayTime.value!! > 110 -> gameDelayTime.value!! - 10
                    gameDelayTime.value!! > 55 -> -5
                    else -> 50
                }
            )

        } else {
            _level.postValue(1)
            _life.postValue(_life.value!! - 1)
            gameDelayTime.postValue(1500)

            if (_life.value!! < 1)
                endGame()
        }
    }

    /* Thread 처리 */
    private val gameRunnable = Runnable {
        _randomNum.postValue(Random(System.currentTimeMillis()).nextInt(1, 11).toString())
        nowDelayTime.postValue(0)

        while (isGaming) {
            try {
                if (isGaming && isChoice) {
                    /* 현재 게임 중이며 사용자가 숫자를 선택했을 시 */
                    _randomNum.postValue(Random(System.currentTimeMillis()).nextInt(1, 9).toString())
                    nowTime = 0
                    nowDelayTime.postValue(0)
                    isChoice = false

                } else if (isGaming && !isChoice && nowTime >= gameDelayTime.value!!) {
                    /* 현재 게임 중이며 사용자가 숫자를 선택 안했지만 시간을 넘겼을 시 */
                    userNOTChoiceNumber()

                    _randomNum.postValue(Random(System.currentTimeMillis()).nextInt(1, 9).toString())
                    nowTime = 0
                    nowDelayTime.postValue(0)
                } else {
                    Thread.sleep(10)
                    nowTime += 10
                    nowDelayTime.postValue(nowTime)
                }


            } catch (e: InterruptedException) {
                _randomNum.postValue(randomNum.value)
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("endGameResult")
        fun createGameResultDialog(layout: ConstraintLayout, isResult: LiveData<Boolean>) {
            if (isResult.value!!) {
                AlertDialog.Builder(layout.context)
                    .setTitle("Game Over")
                    .setMessage("수고하셨습니다")
                    .setPositiveButton("확인", null)
                    .create()
                    .show()
            }
        }
    }
}