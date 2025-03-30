package com.project.gatherly.Model.Repo

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StallDetailsRepo(private val application: Application) {
    private val firebaseAuth: FirebaseAuth
    val db: FirebaseFirestore
    val stallDetails: MutableLiveData<Map<String, Any>?>
    val blogsListdata: MutableLiveData<ArrayList<StallsData>> = MutableLiveData()
    val ImageAndCaptureListdata: MutableLiveData<ArrayList<ImagesAndVideoData>> = MutableLiveData()

    private val storage = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("media")


    init {
        firebaseAuth = FirebaseAuth.getInstance()
        stallDetails = MutableLiveData()
        db = Firebase.firestore
    }

    fun getBlogsList() {

        val blogsList = ArrayList<StallsData>()
        db.collection("StallAds").get().addOnCompleteListener {
            val data = it.result.documents
            for (value in data) {
                val individualValue = value.get("blogLongDescription").toString()
                Log.d("data", individualValue)
                val list = value.toObject(StallsData::class.java)
                if (list != null) {
                    Log.d("list", list.toString())
                    blogsList.add(list)
                }
            }
            blogsListdata.value = blogsList
        }
    }


    fun getImageOrCapture(){
        val galleryList = ArrayList<ImagesAndVideoData>()
        db.collection("gallery").get().addOnCompleteListener {
            val data = it.result.documents
            for (value in data) {
                val list = value.toObject(ImagesAndVideoData::class.java)
                if (list != null) {
                    Log.d("list", list.toString())
                    galleryList.add(list)
                }
            }
            ImageAndCaptureListdata.value = galleryList
        }
    }


    suspend fun uploadMedia(uri: Uri, type: String): String? {
        return try {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser == null) {
                println("Upload failed: User is not authenticated! Please log in.")
                return null
            }

            val userId =  currentUser.uid// Get authenticated user ID
            println("User ID: $userId") // Debugging log

            val fileRef = storage.child("uploads/$userId/${System.currentTimeMillis()}_${uri.lastPathSegment}")

            val uploadTask = fileRef.putFile(uri)

            uploadTask.addOnProgressListener { snapshot ->
                val progress = (100.0 * snapshot.bytesTransferred) / snapshot.totalByteCount
                println("Upload is $progress% done")
            }

            uploadTask.await()

            val downloadUrl = fileRef.downloadUrl.await().toString()
            val media = hashMapOf(
                "url" to downloadUrl,
                "type" to type,
                "userId" to userId, // Corrected user ID
                "timestamp" to System.currentTimeMillis()
            )

            collectionRef.add(media).await()

            println("Upload successful! File URL: $downloadUrl")
            return downloadUrl
        } catch (e: Exception) {
            println("Upload failed: ${e.message}")
            null
        }
    }



    suspend fun getAllMedia(): List<MediaModel> {
        return try {
            collectionRef.get().await().documents.map { doc ->
                doc.toObject(MediaModel::class.java)!!
            }
        } catch (e: Exception) {
            emptyList()
        }
    }






}