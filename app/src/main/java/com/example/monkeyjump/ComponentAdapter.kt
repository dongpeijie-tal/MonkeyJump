package com.example.monkeyjump

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder

class ComponentAdapter(imageList: MutableList<Int>) : BaseQuickAdapter<Int, QuickViewHolder>(imageList) {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Int?) {
        holder.getView<ImageView>(R.id.img).setImageDrawable(ContextCompat.getDrawable(context,item!!))
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder = QuickViewHolder(R.layout.layout_component, parent)

}
