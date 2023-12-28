package com.example.tp

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup

class DraggableImageViewGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ViewGroup(context, attrs, defStyleAttr) {

    private var lastX = 0f
    private var lastY = 0f

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            // 简单地布局每个孩子在视图组中心位置.
            val widthCenterDifference = (r - l - child.measuredWidth) / 2
            val heightCenterDifference = (b - t - child.measuredHeight) / 2

            child.layout(
                widthCenterDifference,
                heightCenterDifference,
                widthCenterDifference + child.measuredWidth,
                heightCenterDifference + child.measuredHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec)

        for (i in 0 until childCount){
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec)
        }
    }

    override fun onTouchEvent(event : MotionEvent): Boolean{
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                lastX=event.x
                lastY=event.y
            }

            MotionEvent.ACTION_MOVE ->{
                val dx=(event.x-lastX).toInt()
                val dy=(event.y-lastY).toInt()

                for(i in 0 until this.childCount){
                    this.getChildAt(i).offsetLeftAndRight(dx)
                    this.getChildAt(i).offsetTopAndBottom(dy)
                }
                invalidate()
            }
        }
        return true
    }
}
