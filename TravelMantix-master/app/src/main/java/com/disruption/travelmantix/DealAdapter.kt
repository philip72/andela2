package com.disruption.travelmantix

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.util.*

class DealAdapter : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {
    internal var mDeals: ArrayList<TravelDeal>? = null
    private val mFirebaseDatabase: FirebaseDatabase
    private val mDatabaseReference: DatabaseReference
    private val mChildEventListener: ChildEventListener
    private var mImageDeal: ImageView? = null

    init {
        FirebaseUtil.openFirebaseReference(DealActivity.TRAVEL_DEALS_PATH, null)

        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase
        mDatabaseReference = FirebaseUtil.sDatabaseReference
        mDeals = FirebaseUtil.sDeals
        mChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val travelDeal = dataSnapshot.getValue(TravelDeal::class.java)!!
                travelDeal.id = dataSnapshot.key
                mDeals!!.add(travelDeal)
                notifyItemInserted(mDeals!!.size - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        mDatabaseReference.addChildEventListener(mChildEventListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.deal_item, parent, false)
        return DealViewHolder(view)
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = mDeals!![position]
        holder.bind(deal)
    }

    override fun getItemCount(): Int {
        return if (mDeals != null) mDeals!!.size else 0
    }

    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private var tvPrice: TextView = itemView.findViewById(R.id.tvPrice)

        init {
            mImageDeal = itemView.findViewById(R.id.imageDeal)
            itemView.setOnClickListener(this)
        }

        fun bind(deal: TravelDeal) {
            tvTitle.text = deal.title
            tvDescription.text = deal.description
            try {
                tvPrice.text = String.format("%s: %d", "$", deal.price?.toInt())
            } catch (e: Exception) {
                Toast.makeText(itemView.context, "Not a valid price for ${deal.title}",
                        Toast.LENGTH_SHORT).show()
            }

            showImage(deal.imageUrl)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            val selectedDeal = mDeals!![position]
            val intent = Intent(view.context, DealActivity::class.java)
            intent.putExtra("deal", selectedDeal)
            view.context.startActivity(intent)
        }

        private fun showImage(url: String?) {
            if (!url.isNullOrEmpty()) {
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_error_outline)
                        .into(mImageDeal)
            } else {
                Picasso.get()
                        .load(R.drawable.ic_error_outline)
                        .into(mImageDeal)
            }
        }
    }
}
