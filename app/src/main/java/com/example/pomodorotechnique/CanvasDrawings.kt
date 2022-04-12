package com.example.pomodorotechnique

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View

class CanvasDrawings @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ovalSpace = RectF()
    private val bitmapOvalSpace = RectF()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setSpace()

        //filling the inner circle
        val paint = Paint()
        paint.setColor(getResources().getColor(R.color.secondaryColor))
        paint.isAntiAlias = true // ensure the drawing has smooth edges

        //drawing the tomato image
        val bitmap : Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tomato)

        canvas?.drawBitmap(bitmap, null, bitmapOvalSpace, null)
        canvas?.drawArc(ovalSpace, 0f, 360f, false, paint)

    }

    private fun setSpace() {
        val horizontalCenter = (width.div(2)).toFloat()
        val verticalCenter = (height.div(2)).toFloat()
        val ovalSize = ((width*0.6)/2).toFloat()
        val bitmapOvalSize = ((width*0.9)/2).toFloat()
        ovalSpace.set(
            horizontalCenter - ovalSize,
            verticalCenter - ovalSize,
            horizontalCenter + ovalSize,
            verticalCenter + ovalSize
        )
        bitmapOvalSpace.set(
            horizontalCenter - bitmapOvalSize,
            verticalCenter - bitmapOvalSize -65f,
            horizontalCenter + bitmapOvalSize,
            verticalCenter + bitmapOvalSize - 65f
        )
    }
}