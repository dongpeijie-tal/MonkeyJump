package com.example.monkeyjump

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<DraggableImageViewGroup>(R.id.imContainer).apply{
            addView(ImageView(this@MainActivity).apply{
                layoutParams = ViewGroup.LayoutParams(200,200)
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.zuotui))
            })
            addView(ImageView(this@MainActivity).apply{
                layoutParams = ViewGroup.LayoutParams(200,200)
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.youtui))
            })
            addView(ImageView(this@MainActivity).apply{
                layoutParams = ViewGroup.LayoutParams(200,200)
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.zuoer))
            })
            addView(ImageView(this@MainActivity).apply{
                layoutParams = ViewGroup.LayoutParams(200,200)
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.youerduo))
            })
            addView(ImageView(this@MainActivity).apply{
                layoutParams = ViewGroup.LayoutParams(200,200)
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.zuogebo))
            })
            addView(ImageView(this@MainActivity).apply{
                layoutParams = ViewGroup.LayoutParams(200,200)
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.yougebo))
            })
            addView(ImageView(this@MainActivity).apply{
                layoutParams = ViewGroup.LayoutParams(200,200)
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.weiba))
            })
            addView(ImageView(this@MainActivity).apply{
                layoutParams = ViewGroup.LayoutParams(200,200)
                setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.body_img))
            })
        }
    }
}