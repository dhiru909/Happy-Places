package com.justdevelopers.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.justdevelopers.happyplaces.R
import com.justdevelopers.happyplaces.activities.AddHappyPlaceActivity
import com.justdevelopers.happyplaces.activities.MainActivity
import com.justdevelopers.happyplaces.database.DatabaseHandler
import com.justdevelopers.happyplaces.databinding.ItemHappyPlaceBinding
import com.justdevelopers.happyplaces.models.HappyPlaceModel

// TODO (Step 6: Creating an adapter class for binding it to the recyclerview in the new package which is adapters.)
// START
open class ItemAdapter(private val context: Context, private val items:ArrayList<HappyPlaceModel>):RecyclerView.Adapter<ItemAdapter.MainHolder>() {
    private var onClickListener:OnClickListener? = null


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
        holder.itemView.setOnClickListener{
            if(onClickListener != null){
                onClickListener!!.onClick(position,item)
            }
        }

    }
    interface OnClickListener{
        fun onClick(position: Int, model:HappyPlaceModel){

        }
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    override fun getItemCount(): Int {
        return items.size
    }
    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, items[position])
        activity.startActivityForResult(
            intent,
            requestCode
        ) // Activity is started with requestCode

        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }

    fun notifyDeleteItem(activity: Activity, position: Int) {
        val db = DatabaseHandler(activity)
        (activity as MainActivity).mDeletedPlace = items[position]
        val result = db.deleteHappyPlace(items[position])
        if(result>0){
            Toast.makeText(activity,"Item deleted successfully",Toast.LENGTH_LONG).show()
        }
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}