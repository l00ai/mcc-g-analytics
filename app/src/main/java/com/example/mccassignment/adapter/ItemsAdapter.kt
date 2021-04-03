package com.example.mccassignment.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mccassignment.DetailsActivity
import com.example.mccassignment.R
import com.example.mccassignment.model.Items
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class ItemsAdapter(var context: Context, var data:MutableList<Items>) : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.img
        val item = itemView
        val name = itemView.txtName

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Picasso.get()
            .load(data[position].image)
            .into(holder.image)
        holder.name.text  = data[position].name
        holder.item.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("data",data[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}