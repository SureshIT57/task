package com.project.gatherly.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.collection.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project.gatherly.R
import com.project.gatherly.databinding.ActivityStallsDetailsViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class StallsDetailsViewActivity : AppCompatActivity() {

    private lateinit var stallsDetailsViewBinding: ActivityStallsDetailsViewBinding

    var REQ_CAMERA = 100
    var imageFilePath: String? = null
    var encodedImage: String? = null
    var timeStamp: String? = null
    var imageName: String? = null
    var fileDirectoty: File? = null
    var imageFilename: File? = null

    lateinit var imageBytes: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        stallsDetailsViewBinding = ActivityStallsDetailsViewBinding.inflate(layoutInflater)
        setContentView(stallsDetailsViewBinding.root)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        stallsDetailsViewBinding.ivBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            if (imageFilePath != null) {
                val imgFile = File(imageFilePath)
                if (imgFile.exists()) {
                    val options: BitmapFactory.Options = BitmapFactory.Options()
                    val bitmap: Bitmap = BitmapFactory.decodeFile(imageFilePath, options)
                    val downloadUrl = uploadImageAndGetDownLoadUrl(
                        getImageUri(this@StallsDetailsViewActivity, bitmap)
                    )

                    Glide.with(this@StallsDetailsViewActivity)
                        .load(downloadUrl)
                        .into(stallsDetailsViewBinding.ivCurrentImage)
                }
            }

            }

        stallsDetailsViewBinding.ivUploadImage.setOnClickListener {
            if (checkPermission()) {
                selectProfilePicture()
            } else {
                Toast.makeText(this@StallsDetailsViewActivity, "Please check your Camera And Video Permission ", Toast.LENGTH_SHORT).show()

            }
        }


    }

    private fun selectProfilePicture() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to delete your account ?")
            .setCancelable(true)
            .setPositiveButton("Camera",
                DialogInterface.OnClickListener { dialog, id ->
                    takeCameraImage()
                    dialog.dismiss()

                })
            .setNegativeButton(
                "Gallery",
                DialogInterface.OnClickListener { dialog, id -> //  Action for 'NO' Button

                    uploadImage()
                    dialog.dismiss()

                })
        val alert: AlertDialog = builder.create()
        alert.setTitle("Choose any Option")
        alert.show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CAMERA && resultCode == Activity.RESULT_OK) {
            convertImage(imageFilePath)
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri = data!!.data!!
            val filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)
            assert(selectedImage != null)

            val cursor: Cursor = getApplicationContext().contentResolver.query(
                selectedImage, filePathColumn,
                null, null, null
            )!!
            cursor.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val mediaPath = cursor.getString(columnIndex)

            cursor.close()
            imageFilePath = mediaPath
            convertImage(mediaPath)
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
      Log.e("final getImageUri", inImage.toString())

        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(

                inContext.contentResolver, inImage,
                "IMG_" + System.currentTimeMillis() + Calendar.getInstance().time,
                null
            )
        return Uri.parse(path)
    }

    // take cameraImage
    @SuppressLint("RestrictedApi")
    private fun takeCameraImage() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            FileProvider.getUriForFile(
                this@StallsDetailsViewActivity,
                BuildConfig.APPLICATION_ID.toString() + ".provider_paths", createImageFile()
            )
        )
        startActivityForResult(intent, REQ_CAMERA)


    }


    // Gallery
    private fun uploadImage() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 2)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        timeStamp = SimpleDateFormat("dd MMMM yyyy HH:mm").format(Date())
        imageName = "JPEG_"
        fileDirectoty =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "")
        imageFilename = File.createTempFile(imageName, ".jpg", fileDirectoty)
        imageFilePath = imageFilename!!.absolutePath
        return imageFilename as File
    }

    @SuppressLint("SetTextI18n")
    private fun convertImage(urlImg: String?) {
        val imgFile = File(urlImg)
        if (imgFile.exists()) {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            val bitmap: Bitmap = BitmapFactory.decodeFile(imageFilePath, options)

            stallsDetailsViewBinding.ivCurrentImage.setImageBitmap(bitmap)

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            imageBytes = baos.toByteArray()
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)



        }
    }

    suspend fun uploadImageAndGetDownLoadUrl(imageUri: Uri): String = withContext(Dispatchers.IO) {

        val uploadImageRef = Firebase.storage.getReference("OccupantProfileImage")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child(imageUri.toString())
//        val imageRef = storageRef.child("images/${UUID.randomUUID()}")
        val uploadTask = uploadImageRef.putFile(imageUri)
        uploadTask.await()
        uploadImageRef.downloadUrl.await().toString()
    }


    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@StallsDetailsViewActivity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }


}