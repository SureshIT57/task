package com.project.gatherly.view

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.project.gatherly.R
import com.project.gatherly.databinding.ActivityGalleryViewBinding

class GalleryViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGalleryViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val KEY_TYPE = intent.getStringExtra("KEY_TYPE")
        val KEY_URL = intent.getStringExtra("KEY_URL")

        if(KEY_TYPE == "image"){
            binding.imageView.visibility = View.VISIBLE
            Glide.with(binding.imageView.context)
                .load(KEY_URL)
                .into(binding.imageView)

        }else{

            binding.videoView.visibility = View.VISIBLE
            // 🔹 Video URL
            val videoUrl = KEY_URL

            // 🔹 Set Video URI
            val uri = Uri.parse(videoUrl)
            binding.videoView.setVideoURI(uri)

            // 🔹 Add Media Controller for Play/Pause/Seek
            val mediaController = MediaController(this)
            mediaController.setAnchorView( binding.videoView)
            binding.videoView.setMediaController(mediaController)
            binding.videoView.start()

        }

        binding.ivBack.setOnClickListener {
            finish()
        }



    }
}