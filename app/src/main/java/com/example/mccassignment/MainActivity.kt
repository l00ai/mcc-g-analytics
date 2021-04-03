package com.example.mccassignment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    var now : Long? = null
    var end : Long? = null
    var sharedPreferences: SharedPreferences? = null
    var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        db = FirebaseFirestore.getInstance()
        sharedPreferences = this.getSharedPreferences("mcc-g-analytics", Context.MODE_PRIVATE)
        btnFood.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("catID", 1)
            startActivity(intent)
        }


        btnClothes.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("catID", 2)
            startActivity(intent)
        }

        btnElectronic!!.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("catID", 3)
            startActivity(intent)
        }

    }

    override fun onResume() {
        trackScreenViews("All Categories","MainActivity")
        now = System.currentTimeMillis()
        val screen = sharedPreferences!!.getInt("screen1",0)
        if( screen != 0 ){
            val time = sharedPreferences!!.getInt("time",0)
            val pageName = sharedPreferences!!.getString("pageName","")
            val timer:HashMap<String, Any> = HashMap<String,Any>()
            timer["screen1"] = screen
            timer["time"] = time
            timer["pageName"] = pageName!!
            db!!.collection("timer").add(timer)
        }
        super.onResume()
    }

    override fun onPause() {
        end = System.currentTimeMillis()
        val timeSecond = (end!! - now!!)/1000
        val editor: SharedPreferences.Editor =  sharedPreferences!!.edit()
        editor.putLong("time",timeSecond)
        editor.putInt("screen1",1)
        editor.putString("pageName","Categories")
        editor.apply()
        editor.commit()
        super.onPause()

    }

    private fun trackScreenViews(screenName: String, screenClass: String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS,screenClass)
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }



}