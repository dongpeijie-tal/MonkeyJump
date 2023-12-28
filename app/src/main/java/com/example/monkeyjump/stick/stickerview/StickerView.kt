package com.example.monkeyjump.stick.stickerview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Region
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.text.format.DateFormat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import androidx.core.widget.ImageViewCompat
import com.example.monkeyjump.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Collections

/**
 * Author: ghc
 * Date : 2017/08/30
 */
class StickerView : AppCompatImageView {
    private var mStickers: ArrayList<Sticker?>? = null
    private var mStickerPaint: Paint? = null
    private var btnDeleteBitmap: Bitmap? = null
    private var btnRotateBitmap: Bitmap? = null
    private var currentSticker: Sticker? = null
    private var lastPoint: PointF? = null
    private var state: TouchState? = null
    private var maxStickerCount = 0
    private var minStickerSizeScale = 0f
    private var imageBeginScale = 0f
    private var closeIcon = 0
    private var rotateIcon = 0
    private var closeSize = 0
    private var rotateSize = 0
    private var outLineWidth = 0
    private var outLineColor = 0
    private var savePath = ""

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setAttributes(context, attrs)
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributes(context, attrs)
        init(context)
    }

    private fun setAttributes(context: Context, attrs: AttributeSet?) {
        try {
            imageBeginScale = 0.5f /*)*/
            maxStickerCount = 20
            minStickerSizeScale = 0.1f
            closeIcon = R.drawable.sticker_closed
            rotateIcon = R.drawable.sticker_rotate
            closeSize = dip2px(context, 20f)
            rotateSize = dip2px(context, 20f)
            outLineWidth = dip2px(context, 1f)
            outLineColor = Color.WHITE
        } finally {
        }
    }

    private fun init(context: Context) {
        mStickerPaint = Paint()
        mStickerPaint!!.isAntiAlias = true
        mStickerPaint!!.style = Paint.Style.STROKE
        mStickerPaint!!.strokeWidth = outLineWidth.toFloat()
        mStickerPaint!!.color = outLineColor
        val mBtnPaint = Paint()
        mBtnPaint.isAntiAlias = true
        mBtnPaint.color = Color.BLACK
        mBtnPaint.style = Paint.Style.FILL
        mStickers = ArrayList()
        btnDeleteBitmap = BitmapFactory.decodeResource(resources, closeIcon)
        btnDeleteBitmap = Bitmap.createScaledBitmap(btnDeleteBitmap!!, closeSize, closeSize, true)
        btnRotateBitmap = BitmapFactory.decodeResource(resources, rotateIcon)
        btnRotateBitmap = Bitmap.createScaledBitmap(btnRotateBitmap!!, rotateSize, rotateSize, true)
        lastPoint = PointF()
    }

    fun addSticker(@DrawableRes res: Int): Boolean {
        if (mStickers!!.size >= maxStickerCount) {
            return false
        }
        val drawable = ContextCompat.getDrawable(context, res)
        return addSticker(drawable)
    }

    fun addSticker(drawable: Drawable?): Boolean {
        val drawableSticker = DrawableSticker(drawable!!)
        mStickers!!.add(drawableSticker)
        currentSticker = drawableSticker
        invalidate()
        return true
    }

    fun clearSticker() {
        mStickers!!.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawStickers(canvas)
    }

    private fun drawStickers(canvas: Canvas) {
        for (sticker in mStickers!!) {
            if (!sticker!!.isInit) {
                val imageWidth = imageBeginScale * measuredWidth
                val imageHeight = imageWidth / sticker.bitmapScale
                val minSize =
                    Math.sqrt((imageWidth * imageWidth + imageHeight * imageHeight).toDouble())
                        .toFloat()
                sticker.minStickerSize = minSize * minStickerSizeScale / 2
                sticker.matrix.postScale(imageWidth / sticker.width, imageWidth / sticker.width)
                sticker.matrix.postTranslate(
                    (measuredWidth - imageWidth) / 2,
                    (measuredHeight - imageHeight) / 2
                )
                sticker.converse()
                sticker.isInit = true
            }
            sticker.draw(canvas)
            if (sticker === currentSticker) {
                //不能使用 drawPath  否则图片过大时会导致 Path too large to be rendered into a texture
                val dst = currentSticker!!.dst
                canvas.drawLine(dst[0], dst[1], dst[2], dst[3], mStickerPaint!!)
                canvas.drawLine(dst[2], dst[3], dst[4], dst[5], mStickerPaint!!)
                canvas.drawLine(dst[4], dst[5], dst[6], dst[7], mStickerPaint!!)
                canvas.drawLine(dst[6], dst[7], dst[0], dst[1], mStickerPaint!!)
                drawBtn(sticker, canvas)
            }
        }
    }

    private fun drawBtn(sticker: Sticker?, canvas: Canvas) {
        canvas.drawBitmap(
            btnDeleteBitmap!!,
            sticker!!.dst[0] - btnDeleteBitmap!!.width / 2,
            sticker.dst[1] - btnDeleteBitmap!!.height / 2,
            null
        )
        canvas.drawBitmap(
            btnRotateBitmap!!,
            sticker.dst[4] - btnRotateBitmap!!.width / 2,
            sticker.dst[5] - btnRotateBitmap!!.height / 2,
            null
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val evX = event.getX(0)
        val evY = event.getY(0)
        val action = MotionEventCompat.getActionMasked(event)
        when (action) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                midPoint = calculateMidPoint(event)
                oldDistance = StickerUtil.calculateDistance(event)
                oldRotation = StickerUtil.calculateRotation(event)
                if (touchInsideSticker(event.getX(0), event.getY(0))
                    && touchInsideSticker(event.getX(1), event.getY(1))
                ) {
                    state = TouchState.DOUBLE_TOUCH
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (touchInsideDeleteButton(evX, evY)) {
                    state = TouchState.PRESS_DELETE
                    return true
                }
                if (touchInsideRotateButton(evX, evY)) {
                    state = TouchState.PRESS_SCALE_AND_ROTATE
                    return true
                }
                state = if (touchInsideSticker(evX, evY)) {
                    TouchState.TOUCHING_INSIDE
                } else {
                    TouchState.TOUCHING_OUTSIDE
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = evX - lastPoint!!.x
                val dy = evY - lastPoint!!.y
                if (state == TouchState.PRESS_SCALE_AND_ROTATE) {
                    rotateAndScale(evX, evY)
                }
                if (state == TouchState.DOUBLE_TOUCH) {
                    rotateAndScaleDoubleTouch(event)
                }
                if (state == TouchState.TOUCHING_INSIDE) {
                    translate(dx, dy)
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (state == TouchState.PRESS_DELETE && touchInsideDeleteButton(evX, evY)) {
                    mStickers!!.remove(currentSticker)
                    currentSticker = null
                    invalidate()
                    return true
                }
                if (state == TouchState.TOUCHING_INSIDE || state == TouchState.PRESS_SCALE_AND_ROTATE) {
                    return true
                }
                if (state == TouchState.TOUCHING_OUTSIDE) {
                    currentSticker = null
                    invalidate()
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                oldDistance = 0f
                oldRotation = 0f
            }
        }
        lastPoint!!.x = evX
        lastPoint!!.y = evY
        return true
    }

    private fun rotateAndScaleDoubleTouch(event: MotionEvent) {
        if (event.pointerCount < 2) {
            return
        }
        //双手旋转时根据映射出的四个点坐标来判断最小值的临界点
        val centerX = (currentSticker!!.dst[0] + currentSticker!!.dst[4]) / 2
        val centerY = (currentSticker!!.dst[1] + currentSticker!!.dst[5]) / 2
        val rightBottomX = currentSticker!!.dst[4]
        val rightBottomY = currentSticker!!.dst[5]
        val pathMeasureLength = getPathMeasureLength(centerX, centerY, rightBottomX, rightBottomY)
        val newDistance = StickerUtil.calculateDistance(event)
        val newRotation = StickerUtil.calculateRotation(event)
        if (oldDistance != 0f) {
            val matrix = currentSticker!!.matrix
            //可以放大  不能缩小
            if (newDistance - oldDistance > 0) {
                matrix.postScale(
                    newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                    midPoint.y
                )
            } else if (pathMeasureLength > currentSticker!!.minStickerSize) {
                matrix.postScale(
                    newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                    midPoint.y
                )
            }
            matrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y)
            currentSticker!!.converse()
        }
        oldDistance = newDistance
        oldRotation = newRotation
    }

    private fun rotateAndScale(evX: Float, evY: Float) {
        var evX = evX
        var evY = evY
        val src = currentSticker!!.rotateSrcPts
        val dst = FloatArray(4)
        val centerX = (currentSticker!!.dst[0] + currentSticker!!.dst[4]) / 2
        val centerY = (currentSticker!!.dst[1] + currentSticker!!.dst[5]) / 2

        //获取到触摸点到中心点距离 计算到中心点的距离比例得到X和Y的比例  通过相似三角形计算出最终结果
        val pathMeasureLength = getPathMeasureLength(centerX, centerY, evX, evY)
        if (pathMeasureLength < currentSticker!!.minStickerSize) {
            evX = currentSticker!!.minStickerSize * (evX - centerX) / pathMeasureLength + centerX
            evY = currentSticker!!.minStickerSize * (evY - centerY) / pathMeasureLength + centerY
        }
        dst[0] = centerX
        dst[1] = centerY
        dst[2] = evX
        dst[3] = evY
        val matrix = currentSticker!!.matrix
        matrix.reset()
        //并不是将图片从一组点变成另一组点  而是获取这两个组的点变换的matrix
        matrix.setPolyToPoly(src, 0, dst, 0, 2)
        currentSticker!!.converse()
    }

    private fun getPathMeasureLength(
        moveX: Float,
        moveY: Float,
        lineX: Float,
        lineY: Float
    ): Float {
        val path = Path()
        path.moveTo(moveX, moveY)
        path.lineTo(lineX, lineY)
        val pathMeasure = PathMeasure(path, false)
        return pathMeasure.length
    }

    private var oldDistance = 0f
    private var oldRotation = 0f
    private var midPoint = PointF()
    protected fun calculateMidPoint(event: MotionEvent?): PointF {
        if (event == null || event.pointerCount < 2) {
            midPoint[0f] = 0f
            return midPoint
        }
        try {
            val x = (event.getX(0) + event.getX(1)) / 2
            val y = (event.getY(0) + event.getY(1)) / 2
            midPoint[x] = y
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return midPoint
    }

    private fun touchInsideRotateButton(evX: Float, evY: Float): Boolean {
        return currentSticker != null && RectF(
            currentSticker!!.dst[4] - btnRotateBitmap!!.width / 2,
            currentSticker!!.dst[5] - btnRotateBitmap!!.height / 2,
            currentSticker!!.dst[4] + btnRotateBitmap!!.width / 2,
            currentSticker!!.dst[5] + btnRotateBitmap!!.height / 2
        ).contains(evX, evY)
    }

    private fun touchInsideDeleteButton(evX: Float, evY: Float): Boolean {
        return currentSticker != null && RectF(
            currentSticker!!.dst[0] - btnDeleteBitmap!!.width / 2,
            currentSticker!!.dst[1] - btnDeleteBitmap!!.height / 2,
            currentSticker!!.dst[0] + btnDeleteBitmap!!.width / 2,
            currentSticker!!.dst[1] + btnDeleteBitmap!!.height / 2
        ).contains(evX, evY)
    }

    private fun translate(dx: Float, dy: Float) {
        if (currentSticker == null) {
            return
        }
        val matrix = currentSticker!!.matrix
        matrix.postTranslate(dx, dy)
        currentSticker!!.converse()
    }

    private fun touchInsideSticker(evX: Float, evY: Float): Boolean {
        for (sticker in mStickers!!) {
            val region = Region()
            region.setPath(sticker!!.boundPath, Region(0, 0, measuredWidth, measuredHeight))
            if (region.contains(evX.toInt(), evY.toInt())) {
                currentSticker = sticker
                val index = mStickers!!.indexOf(currentSticker)
                Collections.swap(mStickers, index, mStickers!!.size - 1)
                return true
            }
        }
        return false
    }

    fun setSelectTint(color: Int) {
        currentSticker?.setSelectTint(ContextCompat.getColor(context, color))
        invalidate()
    }
    private enum class TouchState {
        TOUCHING_INSIDE, TOUCHING_OUTSIDE, PRESS_DELETE, PRESS_SCALE_AND_ROTATE, DOUBLE_TOUCH
    }

    fun setMinStickerSizeScale(minStickerSizeScale: Float) {
        this.minStickerSizeScale = minStickerSizeScale
    }

    fun dip2px(c: Context, dpValue: Float): Int {
        val scale = c.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun saveSticker(): String {
        Thread(StickerSaveTask()).start()
        return savePath
    }

    internal inner class StickerSaveTask : Runnable {
        override fun run() {
            currentSticker = null
            // 采用的是Android截屏实现原理，来实现两张图片的保存
            var bitmap: Bitmap? = Bitmap.createBitmap(
                width, height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap!!)
            draw(canvas)
            val fileSave = "/storage/emulated/0/DCIM/Camera/"
            val picName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance())
            Log.d("StickerView", "[saveSticker] >> picName " + picName + END)
            var file = File(fileSave, picName + END)
            val i = 0
            // 如果该文件存在，则在末尾添加_1、_2....
            while (file.exists()) {
                Log.d("StickerView", "[saveSticker] >> $picName exit.")
                file = File(fileSave, picName + "_" + i + END)
            }
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                out.flush()
                out.close()
                Log.d("StickerView", "save bitmap success.")
                if (bitmap != null && !bitmap.isRecycled) {
                    bitmap.recycle()
                    bitmap = null
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            savePath = file.absolutePath
            scanFile(savePath)
            Log.d("StickerSaveTask", "savePath getAbsolutePath $savePath")
        }
    }

    /**
     * 扫描文件
     *
     * @param path
     */
    private fun scanFile(path: String) {
        MediaScannerConnection.scanFile(
            this.context, arrayOf(path),
            null
        ) { path, uri -> Log.e("TAG", "onScanCompleted") }
    }

    companion object {
        private const val END = ".jpg"
    }
}