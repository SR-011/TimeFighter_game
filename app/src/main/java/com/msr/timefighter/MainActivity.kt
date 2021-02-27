package com.msr.timefighter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.msr.timefighter.BuildConfig.*
import com.msr.timefighter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    internal var score=0
    internal var gameStarted=false

    internal lateinit var countDownTimer: CountDownTimer
    internal var initialCountDown: Long = 10000
    internal var countDownInterval: Long = 1000
    internal var timeLeftOnTimer: Long = 10000
    companion object{
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG,"onCreate called. Score is: "+score)

        binding.buTap.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.button_bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        if(savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        }else {
            resetGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actonAbout) {
            showInfo()
        }
        return true
    }
    @SuppressLint("StringFormatInvalid")
    private fun showInfo(){

        val dialogTitle = getString(R.string.aboutTitle, VERSION_NAME)
        val diaLogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(diaLogMessage)
        builder.create().show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeftOnTimer")
    }
    override fun onDestroy(){
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }
    private fun incrementScore() {
        if(!gameStarted){
            startGame()
        }
        score +=1
        val newScore = getString(R.string.yourScore, score)
        binding.scoreCardTextView.text=newScore
        val blinkAnimation= AnimationUtils.loadAnimation(this, R.anim.blink)
        binding.scoreCardTextView.startAnimation(blinkAnimation)
    }
    private fun resetGame(){
        score=0
        binding.scoreCardTextView.text = getString(R.string.yourScore, score)
        val initialTimeLeft = initialCountDown/1000
        binding.timerTextView.text = getString(R.string.timeLeft, initialTimeLeft)

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished/1000
                binding.timerTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                gameEnd()
            }
        }
        gameStarted = false
    }
    private fun restoreGame() {
        binding.scoreCardTextView.text = getString(R.string.yourScore, score)
        val restoredTime = timeLeftOnTimer/1000
        binding.timerTextView.text = getString(R.string.timeLeft, restoredTime)
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished/1000
                binding.timerTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                gameEnd()
            }
        }
        countDownTimer.start()
        gameStarted = true
    }
    private fun startGame(){
        countDownTimer.start()
        gameStarted=true
    }
    private fun gameEnd(){
        Toast.makeText(this, getString(R.string.gameOverMessage, score), Toast.LENGTH_LONG).show()
        resetGame()
    }
}