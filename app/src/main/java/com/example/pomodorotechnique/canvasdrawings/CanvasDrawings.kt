package com.example.pomodorotechnique.canvasdrawings

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.pomodorotechnique.R

class   CanvasDrawings @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ovalSpace = RectF()
    private val bitmapOvalSpace = RectF()
    val bitmap : Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tomato)


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setSpace()

        //filling the inner circle
        val paintInnerCircle = Paint()
        paintInnerCircle.color = resources.getColor(R.color.secondaryColor)
        paintInnerCircle.isAntiAlias = true // ensure the drawing has smooth edges

        //drawing the tomato image
        //val bitmap : Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tomato)
        canvas?.drawBitmap(bitmap, null, bitmapOvalSpace, null)
        //drawing the inner circle
        canvas?.drawArc(ovalSpace, 0f, 360f, false, paintInnerCircle)


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
        //setting space for the tomato image
        bitmapOvalSpace.set(
            horizontalCenter - bitmapOvalSize,
            verticalCenter - bitmapOvalSize -65f,
            horizontalCenter + bitmapOvalSize,
            verticalCenter + bitmapOvalSize - 65f
        )
    }

}