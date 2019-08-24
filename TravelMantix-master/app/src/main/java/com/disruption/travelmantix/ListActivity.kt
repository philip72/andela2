package com.disruption.travelmantix

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.list_activity_menu, menu)
        val newDealMenu = menu.findItem(R.id.action_new_deal)

        newDealMenu.isVisible = FirebaseUtil.sIsUserAdmin
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new_deal -> {
                startActivity(Intent(this, DealActivity::class.java))
                return true
            }
            R.id.action_log_out -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener {
                            FirebaseUtil.attachListener()
                            Toast.makeText(this@ListActivity, "Logout Success",
                                    Toast.LENGTH_LONG).show()
                        }
                FirebaseUtil.detachListener()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }

    override fun onResume() {
        super.onResume()
        FirebaseUtil.openFirebaseReference(DealActivity.TRAVEL_DEALS_PATH, this)

        val recyclerView = findViewById<RecyclerView>(R.id.rvDeals)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = DealAdapter()
        FirebaseUtil.attachListener()
    }

    fun showMenu() {
        invalidateOptionsMenu()
    }
}
