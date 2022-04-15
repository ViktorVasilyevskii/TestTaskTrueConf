package com.vasilyevskii.testtasktrueconf

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.vasilyevskii.testtasktrueconf.databinding.ActivityMainBinding
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var animationText: ValueAnimator

    private var heightDisplayMetrics by Delegates.notNull<Float>()

    companion object{
        const val DELAY_TIME: Long = 5 * 1000
        const val DURATION_ANIMATION_TEXT: Long = 3 * 1000
        const val ZERO_COORDINATE = 0F
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        heightDisplayMetrics = binding.mainContainer.height.toFloat()
    }

    private fun migrationTextByCoordinates(x: Float, y: Float){
        binding.textMain.apply{
            this.x = x
            this.y = (y - this.height * 4f)
            this.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.color_text_main))
            moveTextByY(this.y, heightDisplayMetrics)

            setOnClickListener {
                stopAnimationText()
            }
        }
    }

    private fun moveTextByY(startY: Float, endY: Float){
        animationText = ValueAnimator.ofFloat(startY, endY)

        when(startY){
            ZERO_COORDINATE,
            heightDisplayMetrics -> animationText.startDelay = 1L
            else -> animationText.startDelay = DELAY_TIME
        }

        animationText.addUpdateListener { animation ->
            val coordinatesY = animation.animatedValue as Float
            when(coordinatesY){
                ZERO_COORDINATE -> {
                    animationText.cancel()
                    moveTextByY(ZERO_COORDINATE, heightDisplayMetrics)
                }
                heightDisplayMetrics -> {
                    animationText.cancel()
                    moveTextByY(heightDisplayMetrics, ZERO_COORDINATE)
                }
            }
            binding.textMain.translationY = coordinatesY
        }
        animationText.interpolator = LinearInterpolator()
        animationText.duration = DURATION_ANIMATION_TEXT

        animationText.start()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        stopAnimationText()

        val x = event.rawX
        val y = event.rawY
        migrationTextByCoordinates(x, y)

        return super.onTouchEvent(event)
    }

    private fun stopAnimationText(){
        if(this::animationText.isInitialized){
            animationText.cancel()
        }
    }

    override fun onPause() {
        super.onPause()
        stopAnimationText()
    }

    override fun onStop() {
        super.onStop()
        stopAnimationText()
    }

}