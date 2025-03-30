package com.project.gatherly.Model.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.gatherly.Model.Repo.StallsData
import com.project.gatherly.R
import com.project.gatherly.view.StallsDetailsViewActivity


internal class StallsAdapter(
    ctx: Context,
    data: ArrayList<StallsData>
) :
    RecyclerView.Adapter<StallsAdapter.MyViewHolder?>() {
    private val inflater: LayoutInflater
    var ctx: Context
    var data = ArrayList<StallsData>()

    init {
        inflater = LayoutInflater.from(ctx)
        this.ctx = ctx
        this.data = data

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            inflater.inflate(R.layout.stall_items, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val propertyData: StallsData = data[position]

        holder.stallsDate.text = propertyData.date
        holder.stallsTitle.text = propertyData.name
        holder.stallsCompany.text = propertyData.company
        holder.stallsFiles.text = propertyData.files


        val imageUrl = propertyData.imageUrl
        Glide.with(ctx)
            .load(imageUrl)
            .into(holder.stallsImage)
holder.addStallImage.setOnClickListener {
    val intent = Intent(ctx, StallsDetailsViewActivity::class.java)
    ctx.startActivity(intent)
}

    }

    override fun getItemCount(): Int {
        return data.size
    }


    internal inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var stallsDate: TextView
        var stallsTitle: TextView
        var stallsCompany: TextView
        var stallsFiles: TextView
        var stallsImage: ImageView
        var addStallImage: ImageView

        init {
            stallsDate = itemView.findViewById<TextView>(R.id.tv_stallDate) as TextView
            stallsTitle = itemView.findViewById<TextView>(R.id.tv_stallTitle) as TextView
            stallsCompany = itemView.findViewById<TextView>(R.id.tv_stallCompanyName) as TextView
            stallsFiles = itemView.findViewById<TextView>(R.id.tv_stallFiles) as TextView
            stallsImage = itemView.findViewById<ImageView>(R.id.iv_stallImage) as ImageView
            addStallImage = itemView.findViewById<ImageView>(R.id.iv_addImage) as ImageView



        }


    }




}