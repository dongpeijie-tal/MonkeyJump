package com.example.monkeyjump.stick.stickerview

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.ImageViewCompat.setImageTintList

/**
 * Author: ghc
 * Date : 17/8/6
 */
class DrawableSticker(private val drawable: Drawable) : Sticker() {
    private val realBounds: Rect

    init {
        realBounds = Rect(0, 0, width, height)
        init(drawable.intrinsicWidth, drawable.intrinsicHeight)
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.concat(matrix)
        drawable.bounds = realBounds
        drawable.draw(canvas)
        canvas.restore()
    }

    override fun getWidth(): Int {
        return drawable.intrinsicWidth
    }

    override fun getHeight(): Int {
        return drawable.intrinsicHeight
    }

    override fun setSelectTint(color: Int) {
        DrawableCompat.setTintMode(drawable,PorterDuff.Mode.MULTIPLY)
        DrawableCompat.setTint(drawable,color)
    }
}