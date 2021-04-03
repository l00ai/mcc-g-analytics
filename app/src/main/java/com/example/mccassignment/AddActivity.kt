package com.example.mccassignment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add.*

class AddActivity : AppCompatActivity() {
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    val PICK_IMAGE_REQUEST = 1
    var filepath: Uri? = null
    var storageReference: StorageReference? = null
    var db: FirebaseFirestore? = null
    var category_id:Int? = null
    var now : Long? = null
    var end : Long? = null
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        category_id = intent.getIntExtra("catID", 0)

        storageReference = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()
        sharedPreferences = this.getSharedPreferences("mcc-g-analytics",
            Context.MODE_PRIVATE)

        btnSave.setOnClickListener {view->
            if(txtName.text.isNotEmpty()){
                if(txtPrice.text.isNotEmpty()){
                    if(txtDescription.text.isNotEmpty()){
                        if(filepath != null) {
                            uploadImage(view)
                        }else{
                            Snackbar.make(view, "Pleas Choose Any Image", Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }else{
                        Snackbar.make(view, "The Description Must Not Be Empty", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }else{
                    Snackbar.make(view, "The Price Must Not Be Empty", Snackbar.LENGTH_LONG)
                        .show()
                }
            }else{
                Snackbar.make(view, "The Name Must Not Be Empty", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        img.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent,PICK_IMAGE_REQUEST)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            filepath = data.data
            img.setImageURI(filepath)
        }
    }

    fun saveImageUri(uri: String){
        val item:HashMap<String, Any> = HashMap<String,Any>()
        item["image"] = uri
        item["catID"] = category_id!!
        item["name"] = txtName.text.toString()
        item["price"] = txtPrice.text.toString().toInt()
        item["description"] = txtDescription.text.toString()
        db!!.collection("items").add(item)
        val intent = Intent(this, ListActivity::class.java)
        intent.putExtra("catID", category_id)
        startActivity(intent)
    }

    private fun uploadImage(view: View){
        val imageReference = storageReference!!.child("images/${filepath!!.pathSegments}")
        imageReference.putFile(filepath!!)
            .addOnSuccessListener { _->
                imageReference.downloadUrl.addOnSuccessListener {
                    saveImageUri(it.toString())
                }
                Snackbar.make(view, "The Image Upload Successfully", Snackbar.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener {
                Snackbar.make(view, "The Image Upload failed", Snackbar.LENGTH_LONG)
                    .show()
            }
    }

    override fun onResume() {
        super.onResume()
        trackScreenViews("Add Product","FormActivity")
        now = System.currentTimeMillis()
        val screen = sharedPreferences!!.getInt("screen4",0)
        if( screen != 0 ){
            val time = sharedPreferences!!.getInt("time",0)
            val pageName = sharedPreferences!!.getString("pageName","")
            val timer:HashMap<String, Any> = HashMap<String,Any>()
            timer["screen4"] = screen
            timer["time"] = time
            timer["pageName"] = pageName!!
            db!!.collection("timer").add(timer)
        }
    }

    override fun onPause() {
        super.onPause()
        end = System.currentTimeMillis()
        val timeSecond = (end!! - now!!)/1000
        var screen = sharedPreferences!!.getInt("screen",0)

        val editor: SharedPreferences.Editor =  sharedPreferences!!.edit()
        editor.putLong("time",timeSecond)
        editor.putInt("screen",2)
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