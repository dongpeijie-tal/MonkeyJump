package com.example.monkeyjump

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.SeekBar

/**
 * 可以拖动的进度条
 */
class ScaleControlView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var seekBar: SeekBar

    init {
        orientation = VERTICAL
        seekBar = SeekBar(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        addView(seekBar)
    }

    /***
     * 外部调用更改当前进度位置
     */
    fun notifyProgressChange(size: Int) {
        seekBar.progress = size - 100
    }

    /**
     * 更改进度触发事件
     */
    fun changeRunner(function: (size: Int) -> Unit) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                function(100+progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No-op
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No-op
            }
        })
    }
}
