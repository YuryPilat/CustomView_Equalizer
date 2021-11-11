package com.example.equalizerview.equalizer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.equalizerview.R

private const val COMMON_MARGIN = 10f
private const val START_HEIGHT = 100

class EqualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle){

    private var equalCanvasPart: Float = 0f
    private var maximumRowHeight = 0f
    private var filledRectsList = arrayListOf(RectF(), RectF(), RectF(), RectF(), RectF())
    private var bordersRectsList = arrayListOf(RectF(), RectF(), RectF(), RectF(), RectF())
    private val fillRectPaint : Paint = Paint().apply {
        this.color = context.getColor(R.color.rectBlue)
        this.style = Paint.Style.FILL
    }
    private val strokeRectPaint = Paint().apply {
        this.style = Paint.Style.STROKE
        this.color = context.getColor(R.color.rectGrey)
        this.strokeWidth = COMMON_MARGIN/(filledRectsList.size-2)
    }
    private val strokeBigRectPaint = Paint().apply {
        this.style = Paint.Style.STROKE
        this.color = context.getColor(R.color.rectBlue)
        this.strokeWidth = COMMON_MARGIN
    }
    private var currentPercentList = arrayListOf(START_HEIGHT, START_HEIGHT, START_HEIGHT, START_HEIGHT, START_HEIGHT)
    private var heightsList = arrayListOf(COMMON_MARGIN, COMMON_MARGIN, COMMON_MARGIN, COMMON_MARGIN, COMMON_MARGIN)
    private val defaultBarWidth = resources.getDimensionPixelSize(R.dimen.volume_bar_default_width)
    private val defaultBarHeight = resources.getDimensionPixelSize(R.dimen.volume_bar_default_height)
    private var onEqualizerDataChangedListener: OnEqualizerDataChanged? = null

    fun setOnEqualizerDataChangedListener(listener: OnEqualizerDataChanged?) {
        this.onEqualizerDataChangedListener = listener
    }

    interface OnEqualizerDataChanged {
        fun shareRowsHeight(percentList: ArrayList<Int>)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val deviceWidth = MeasureSpec.getSize(widthMeasureSpec)
        val deviceHeight = MeasureSpec.getSize(heightMeasureSpec)

        val viewWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> deviceWidth
            MeasureSpec.AT_MOST -> defaultBarWidth
            MeasureSpec.UNSPECIFIED -> defaultBarWidth
            else -> defaultBarWidth
        }

        val viewHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> deviceHeight
            MeasureSpec.AT_MOST -> defaultBarHeight
            MeasureSpec.UNSPECIFIED -> defaultBarHeight
            else -> defaultBarHeight
        }
        setMeasuredDimension(viewWidth/2, viewHeight/2)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        maximumRowHeight = height - COMMON_MARGIN
        equalCanvasPart = (width/(filledRectsList.size*2+1)).toFloat()
        onEqualizerDataChangedListener?.shareRowsHeight(currentPercentList)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            drawMainBorder(canvas)
            drawFilledRects(canvas)
            drawRectStrokes(canvas)
        }
    }

    private fun drawRectStrokes(canvas: Canvas) {
        (bordersRectsList.indices).forEach{ i ->
            bordersRectsList[i].set(COMMON_MARGIN, COMMON_MARGIN, equalCanvasPart, maximumRowHeight)
            bordersRectsList[i].offsetTo(equalCanvasPart*(i + i+1), COMMON_MARGIN)
            canvas.drawRect(bordersRectsList[i], strokeRectPaint)
        }
    }

    private fun drawMainBorder(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), strokeBigRectPaint)
    }

    private fun drawFilledRects(canvas: Canvas) {
        (filledRectsList.indices).forEach{ i ->
            filledRectsList[i].set(COMMON_MARGIN, heightsList[i], equalCanvasPart, maximumRowHeight)
            filledRectsList[i].offsetTo(equalCanvasPart*(i + i+1), heightsList[i])
            currentPercentList[i] = countPercent(filledRectsList[i].height())
            canvas.drawRect(filledRectsList[i], fillRectPaint)
        }
    }

    private fun countPercent(newRectHeight: Float): Int {
        return (((newRectHeight + COMMON_MARGIN)/maximumRowHeight* START_HEIGHT)).toInt()
    }

    private fun setNewHeight(barHeight: Float, i: Int) {
        heightsList[i] = if (barHeight<=0) COMMON_MARGIN else barHeight
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_MOVE) {
            (bordersRectsList.indices).forEach {
                if(bordersRectsList[it].contains(event.x, event.y)) setNewHeight(event.y, it)
            }
            onEqualizerDataChangedListener?.shareRowsHeight(currentPercentList)
        }
        return true
    }

}
