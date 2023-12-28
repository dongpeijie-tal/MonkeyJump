package com.example.monkeyjump

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder

class ColorAdapter(colors: MutableList<Int>) : BaseQuickAdapter<Int, QuickViewHolder>(colors) {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Int?) {
        holder.getView<View>(R.id.view).setBackgroundColor(ContextCompat.getColor(context,item!!))
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder = QuickViewHolder(R.layout.layout_color_item, parent)

}