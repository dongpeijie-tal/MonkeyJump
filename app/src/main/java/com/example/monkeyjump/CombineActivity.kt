package com.example.monkeyjump

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monkeyjump.stick.stickerview.StickerView

class CombineActivity : AppCompatActivity() {

    private val imageList = mutableListOf(R.drawable.body_img,R.drawable.yougebo,R.drawable.zuogebo,
        R.drawable.zuoer, R.drawable.youerduo,
        R.drawable.youtui,R.drawable.zuotui,R.drawable.weiba)
    private val colorsList = mutableListOf(R.color.black,R.color.white,R.color.c1,
        R.color.c2,R.color.c3,R.color.c4,R.color.c5,R.color.c6,
        R.color.c7,R.color.c8,R.color.c9,R.color.c10,R.color.c11,
        R.color.c12,R.color.c13,R.color.c14,R.color.c15,R.color.c16,
        R.color.c17,R.color.c18,R.color.c19)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combine)
        // 拼装容器
        val container = findViewById<StickerView>(R.id.imContainer)
        // 组合件列表
        findViewById<RecyclerView>(R.id.combine_entry).apply{
            layoutManager = LinearLayoutManager(this@CombineActivity)
            adapter = ComponentAdapter(imageList).apply{
                setOnItemClickListener { adapter, view, position ->
                    container.addSticker(imageList[position])
                }
            }
        }
        // 颜色列表
        findViewById<RecyclerView>(R.id.color_entry).apply{
            layoutManager = LinearLayoutManager(this@CombineActivity)
            adapter = ColorAdapter(colorsList).apply{
                setOnItemClickListener { adapter, view, position ->
                    container.setSelectTint(colorsList[position])
                }
            }
        }

    }
}