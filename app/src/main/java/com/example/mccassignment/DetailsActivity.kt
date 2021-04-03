package com.example.mccassignment

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.mccassignment.model.Items
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    var now : Long? = null
    var end : Long? = null
    var sharedPreferences: SharedPreferences? = null
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val item: Items = intent.getSerializableExtra("data") as Items

        sharedPreferences = this.getSharedPreferences("mcc-g-analytics", Context.MODE_PRIVATE)
        db = FirebaseFirestore.getInstance()

        Picasso.get()
            .load(item.image)
            .into(img)
        tvName.text = item.name
        tvPrice.text = item.price.toString()
        tvDescription.text = item.description
    }

    override fun onResume() {
        super.onResume()
        trackScreenViews("Product Details","DetailsActivity")
        val screen = sharedPreferences!!.getInt("screen2",0)
        if( screen != 0 ){
            val time = sharedPreferences!!.getInt("time",0)
            val pageName = sharedPreferences!!.getString("pageName","")
            val timer:HashMap<String, Any> = HashMap()
            timer["screen2"] = screen
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
        editor.putInt("screen2",2)
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