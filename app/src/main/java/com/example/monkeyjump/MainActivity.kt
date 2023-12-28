package com.example.monkeyjump

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.monkeyjump.tts.TtsUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), TtsUtils.ISpeechProgressChanged {

    private lateinit var tvStory: TextView
    private val story =
        "在神秘的丛林深处，一只叫做Bongo的卡通猴子正挺直脊背站在一个古老遗迹的大门前。这扇巨大的石门上刻满了各种动物图像和神秘符号，仿佛是一个等待解密的谜题。突然，丛林深处传来一阵震耳欲聋的野兽咆哮。Bongo是否能破解这个密码，找到隐藏在遗迹中的宝藏并安全逃出丛林呢？让我们拭目以待！"
    private var lastIndex = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvStory = findViewById(R.id.tv_story)
//        findViewById<DraggableImageViewGroup>(R.id.imContainer).apply{
//            addView(ImageView(this@MainActivity).apply{
//                layoutParams = ViewGroup.LayoutParams(200,200)
//                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.zuotui))
//            })
//            addView(ImageView(this@MainActivity).apply{
//                layoutParams = ViewGroup.LayoutParams(200,200)
//                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.youtui))
//            })
//            addView(ImageView(this@MainActivity).apply{
//                layoutParams = ViewGroup.LayoutParams(200,200)
//                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.zuoer))
//            })
//            addView(ImageView(this@MainActivity).apply{
//                layoutParams = ViewGroup.LayoutParams(200,200)
//                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.youerduo))
//            })
//            addView(ImageView(this@MainActivity).apply{
//                layoutParams = ViewGroup.LayoutParams(200,200)
//                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.zuogebo))
//            })
//            addView(ImageView(this@MainActivity).apply{
//                layoutParams = ViewGroup.LayoutParams(200,200)
//                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.yougebo))
//            })
//            addView(ImageView(this@MainActivity).apply{
//                layoutParams = ViewGroup.LayoutParams(200,200)
//                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.weiba))
//            })
//            addView(ImageView(this@MainActivity).apply{
//                layoutParams = ViewGroup.LayoutParams(200,200)
//                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.body_img))
//            })
//        }
        TtsUtils.initTTS(this, this)

    }

    override fun onSpeechProgressChanged(sequence: Int, index: Int) {
        val rawIndex = sequence * TtsUtils.BAG_LENGTH + index
        if(lastIndex == rawIndex) {
            lastIndex = rawIndex
            return
        }
        try {
            // 回调不够准确 使用append会丢字
            val substring = story.substring(0, rawIndex)
            tvStory.setText(substring)
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
        lastIndex = rawIndex
    }

}

