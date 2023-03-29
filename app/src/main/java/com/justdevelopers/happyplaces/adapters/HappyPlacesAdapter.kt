package com.justdevelopers.happyplaces.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.justdevelopers.happyplaces.R
import com.justdevelopers.happyplaces.databinding.ItemHappyPlaceBinding
import com.justdevelopers.happyplaces.models.HappyPlaceModel

// TODO (Step 6: Creating an adapter class for binding it to the recyclerview in the new package which is adapters.)
// START
open class ItemAdapter(private val items:ArrayList<HappyPlaceModel>):RecyclerView.Adapter<ItemAdapter.MainHolder>() {
    inner class MainHolder(itemView: ItemHappyPlaceBinding) : RecyclerView.ViewHolder(itemView.root){
        val tvName = itemView.tvTitle
        val tvDescription = itemView.tvDescription
        val ivPlaceImage = itemView.ivPlaceImage


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(ItemHappyPlaceBinding.inflate(LayoutInflater
            .from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]
        holder.tvName.text = item.title
        holder.tvDescription.text = item.description
        holder.ivPlaceImage.setImageURI(Uri.parse(item.image))

    }

    override fun getItemCount(): Int {
        return items.size
    }
}