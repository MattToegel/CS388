package com.ethereallab.purfectcats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CatImageAdapter(private val catImages: List<String>) :
    RecyclerView.Adapter<CatImageAdapter.CatImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cat_image, parent, false)
        return CatImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatImageViewHolder, position: Int) {
        val imageUrl = catImages[position]
        Glide.with(holder.imageView.context)
            .load(imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = catImages.size

    class CatImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}
