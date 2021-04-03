package com.example.mccassignment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.mccassignment.adapter.ItemsAdapter
import com.example.mccassignment.model.Items
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    var db: FirebaseFirestore? = null
    var items:MutableList<Items> = mutableListOf()
    var itemOne:Map<String, Any>? = null
    var categoryId:Int? =  null
    var now : Long? = null
    var end : Long? = null
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        db = FirebaseFirestore.getInstance()

        categoryId = intent.getIntExtra("catID", 0)
        sharedPreferences = this.getSharedPreferences("mcc-g-analytics", Context.MODE_PRIVATE)

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            intent.putExtra("catID", categoryId)
            startActivity(intent)
        }

        db!!.collection("items")
            .get()
            .addOnCompleteListener {result->
                if(result.isSuccessful){
                    for( document in result.result!!){
                        itemOne = document.data
                        if(categoryId == itemOne!!["catID"].toString().toInt()){
                            items.add(Items(itemOne!!["name"].toString(),itemOne!!["price"].toString().toInt(),itemOne!!["description"].toString(),itemOne!!["image"].toString()))
                        }
                    }
                    val contactAdapter = ItemsAdapter(this, items)
                    rv.adapter = contactAdapter
                }

            }
    }

    override fun onResume() {
        super.onResume()
        trackScreenViews("All Products","MainActivity2")
        now = System.currentTimeMillis()
        val screen = sharedPreferences!!.getInt("screen3",0)
        if( screen != 0 ){
            val time = sharedPreferences!!.getInt("time",0)
            val pageName = sharedPreferences!!.getString("pageName","")
            val timer:HashMap<String, Any> = HashMap<String,Any>()
            timer["screen3"] = screen
            timer["time"] = time
            timer["pageName"] = pageName!!
            db!!.collection("timer").add(timer)
        }
    }
    override fun onPause() {
        super.onPause()
        end = System.currentTimeMillis()
        val timeSecond = (end!! - now!!)/1000

        val editor: SharedPreferences.Editor =  sharedPreferences!!.edit()
        editor.putLong("time",timeSecond)
        editor.putInt("screen3",2)
        editor.putString("pageName","Product")
        editor.apply()
        editor.commit()


    }

    private fun trackScreenViews(screenName: String, screenClass: String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS,screenClass)
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }



}