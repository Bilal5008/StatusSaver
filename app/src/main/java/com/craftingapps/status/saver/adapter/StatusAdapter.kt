package com.craftingapps.status.saver.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.craftingapps.status.saver.R
import com.craftingapps.status.saver.models.StatusModel
import com.craftingapps.status.saver.views.activity.StatusDetailsActivity

import java.util.ArrayList


class StatusAdapter(internal var context: Context, internal var statusList: ArrayList<StatusModel>) : RecyclerView.Adapter<StatusAdapter.MyViewHolder>() {


    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the custom layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_status_item, parent, false)

        return MyViewHolder(itemView)
    }


    //********** Called by RecyclerView to display the Data at the specified Position *********//

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.ivPlayBtn.visibility = if (statusList[position].isVideo) View.VISIBLE else View.GONE

        Glide.with(context)
                .load(statusList[position].uri)
                .into(holder.ivStatusImg)
    }


    //********** Returns the total number of items in the data set *********//

    override fun getItemCount(): Int {
        return statusList.size
    }


    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item  */

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var ivPlayBtn: ImageView
        internal var ivStatusImg: ImageView


        init {
            ivPlayBtn = itemView.findViewById<View>(R.id.iv_play_btn) as ImageView
            ivStatusImg = itemView.findViewById<View>(R.id.iv_status_cover) as ImageView

            ivPlayBtn.setOnClickListener(this)
            ivStatusImg.setOnClickListener(this)
        }


        // Handle Click Listener on Category item
        override fun onClick(view: View) {
            // Navigate to StatusDetailsActivity Activity
            val intent = Intent(context, StatusDetailsActivity::class.java)
            intent.putExtra("statusInfo", statusList[adapterPosition])
            context.startActivity(intent)
        }
    }

}
