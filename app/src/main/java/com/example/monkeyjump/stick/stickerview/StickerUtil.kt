package com.example.monkeyjump.stick.stickerview

import android.view.MotionEvent

/**
 * Author: ghc
 * Date : 17/8/6
 */
object StickerUtil {
    fun calculateRotation(event: MotionEvent?): Float {
        return if (event == null || event.pointerCount < 2) {
            0f
        } else calculateRotation(
            event.getX(0),
            event.getY(0),
            event.getX(1),
            event.getY(1)
        )
    }

    fun calculateRotation(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = (x1 - x2).toDouble()
        val y = (y1 - y2).toDouble()
        val radians = Math.atan2(y, x)
        return Math.toDegrees(radians).toFloat()
    }

    fun calculateDistance(event: MotionEvent?): Float {
        return if (event == null || event.pointerCount < 2) {
            0f
        } else calculateDistance(
            event.getX(0),
            event.getY(0),
            event.getX(1),
            event.getY(1)
        )
    }

    fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = (x1 - x2).toDouble()
        val y = (y1 - y2).toDouble()
        return Math.sqrt(x * x + y * y).toFloat()
    }
}