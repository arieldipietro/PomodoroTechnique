package com.example.pomodorotechnique.canvasdrawings

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.pomodorotechnique.R

class AnimationRed @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ovalSpace = RectF()
    private val bitmapOvalSpace = RectF()
    private var currentPercentage = 0
    private val percentageToFill = getCurrentPercentageToFill()
    //instance of ValueAnimator
    private val valueAnimator = ValueAnimator()

    //filling the animated red circle
    val paintAnimationRed = Paint().apply {
        setColor(getResources().getColor(R.color.redForAnimation))
        isAntiAlias = true // ensure the drawing has smooth edges
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 40f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setSpace()

        //drawing the animation
        drawLoadingAnimationRed(canvas!!)
    }

    private fun drawLoadingAnimationRed(canvas: Canvas){
        val percentageToFill = getCurrentPercentageToFill()
        //animating the filled button
        canvas.drawArc(ovalSpace, 270f, percentageToFill, false, paintAnimationRed)
        invalidate()
    }

    private fun setSpace() {
        val horizontalCenter = (width.div(2)).toFloat()
        val verticalCenter = (height.div(2)).toFloat()
        val ovalSize = ((width*0.6)/2).toFloat()
        val bitmapOvalSize = ((width*0.9)/2).toFloat()
        //setting space for the orange circle
        ovalSpace.set(
            horizontalCenter - ovalSize,
            verticalCenter - ovalSize,
            horizontalCenter + ovalSize,
            verticalCenter + ovalSize
        )
    }

    private fun getCurrentPercentageToFill() = (360f * (currentPercentage / PERCENTAGE_DIVIDER)).toFloat()

    //function to be called to start the animation
    fun animateProgress(setDuration: Long) {
        Log.i("MainActivity", "AnimateProgress called")
        //holds animation values from 0 to 100
        val valuesHolder = PropertyValuesHolder.ofFloat(PERCENTAGE_VALUE_HOLDER, 0f, 100f)

        valueAnimator.apply {
            setValues(valuesHolder)

            //setting the duration of the animation to the seconds remaining in the timer
            duration = setDuration

            addUpdateListener {
                val percentage = it.getAnimatedValue(PERCENTAGE_VALUE_HOLDER) as Float
                currentPercentage = percentage.toInt()
                invalidate()
            }
        }
        valueAnimator.start()
    }

    fun pauseAnimation(){
        valueAnimator.pause()
    }
    fun cancelAnimation(){
        valueAnimator.cancel()
    }
    fun resumeAnimation(){
        valueAnimator.resume()
    }

    companion object {
        const val PERCENTAGE_DIVIDER = 100.0
        const val PERCENTAGE_VALUE_HOLDER = "percentage"
    }
}