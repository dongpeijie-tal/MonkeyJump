package com.example.monkeyjump.stick.stickerview

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Path

/**
 * Author: ghc
 * Date : 2017/08/30
 */
abstract class Sticker {
    var isInit = false
    lateinit var matrix: Matrix
        private set
    private lateinit var srcPts: FloatArray
    lateinit var dst: FloatArray
        private set
    lateinit var rotateSrcPts: FloatArray
        private set
    lateinit var boundPath: Path
    var minStickerSize = 0f
    fun init(width: Int, height: Int) {
        matrix = Matrix()
        srcPts = floatArrayOf(
            0f, 0f,  // 左上
            width.toFloat(), 0f,  // 右上
            width.toFloat(), height.toFloat(),  // 右下
            0f, height.toFloat()
        )
        /*
         * 原始旋转效果的点  图片中心点和图片的右下角的点
         * 触摸时获取到触摸点以及和中心点形成另一组的点
         * 之后通过matrix.setPolyToPoly(src, 0, dst, 0, 2) 方法来获取变换后的matrix
         */rotateSrcPts = floatArrayOf(
            (
                    width / 2).toFloat(), (height / 2).toFloat(),
            width.toFloat(), height.toFloat()
        )
        dst = FloatArray(8)
        boundPath = Path()
    }

    abstract fun draw(canvas: Canvas?)
    abstract val width: Int
    abstract val height: Int
    val bitmapScale: Float
        get() = width / height.toFloat()

    fun converse() {
        matrix!!.mapPoints(dst, srcPts)
    }

    fun getBoundPath(): Path? {
        boundPath!!.reset()
        boundPath!!.moveTo(dst[0], dst[1])
        boundPath!!.lineTo(dst[2], dst[3])
        boundPath!!.lineTo(dst[4], dst[5])
        boundPath!!.lineTo(dst[6], dst[7])
        boundPath!!.lineTo(dst[0], dst[1])
        return boundPath
    }

    abstract fun setSelectTint(color: Int)
}