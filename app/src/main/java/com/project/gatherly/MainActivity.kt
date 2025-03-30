package com.project.gatherly

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.project.gatherly.Model.Repo.StallsData

class MainActivity : AppCompatActivity() {

    private var url: ArrayList<String> = arrayListOf()


    private lateinit var propertyData: StallsData
    private var propertyCount: Int = 0
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val sendAds = hashMapOf(
            "date" to "24-Jun 25,2024",
            "name" to "Decor  Delight",
            "company" to "Decor Construction",
            "files" to "23 Files",
            "imageUrl" to ""
        )

        // Write data to Firestore
        db.collection("StallAds").document()
            .set(sendAds)
            .addOnSuccessListener {
                Log.d("main screen testing", "DocumentSnapshot successfully written!")
                Toast.makeText(this@MainActivity, "Stall added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("main screen testing", "Error writing document", e)
            }

    }



    }