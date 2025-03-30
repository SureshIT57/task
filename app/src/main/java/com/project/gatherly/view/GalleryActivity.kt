package com.project.gatherly.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.gatherly.Dialog.AuthManager
import com.project.gatherly.Dialog.MediaBottomSheet
import com.project.gatherly.Model.Adapter.GridAdapter
import com.project.gatherly.Model.Adapter.OnItemClickListener
import com.project.gatherly.Model.Repo.ImagesAndVideoData
import com.project.gatherly.Model.Repo.MediaModel
import com.project.gatherly.ViewModel.StallsDetails
import com.project.gatherly.databinding.ActivityGalleryBinding
import kotlinx.coroutines.launch
class GalleryActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var getImageAndVideoViewModel: StallsDetails
    private lateinit var imageAndVideoAdapter: GridAdapter
    private var imageAndVideoListDataRecycler: ArrayList<MediaModel> = arrayListOf()

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Show loader while data is being loaded
        binding.progressBar.visibility = View.VISIBLE



        // 🔹 Check if the user is logged in before proceeding
        if (auth.currentUser == null) {
            loginUser("suresh@gmail.com", "P@ssw0rd")
        } else {
            println("User already logged in: ${auth.currentUser?.uid}")
        }

        // Initialize ViewModel
        getImageAndVideoViewModel = ViewModelProvider(this)[StallsDetails::class.java]

        // Initialize RecyclerView
        setupRecyclerView()

        // Observe LiveData
        observeLiveData()

        // Set up Search
        setupSearchView()

        // Set up Image and Video Picker
        val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { getImageAndVideoViewModel.uploadMedia(it, "image") }
        }

        val videoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { getImageAndVideoViewModel.uploadMedia(it, "video") }
        }

        // Handle Image Add Click
        binding.ivAddImage.setOnClickListener {
            val bottomSheet = MediaBottomSheet(imagePicker, videoPicker)
            bottomSheet.show(supportFragmentManager, "MediaBottomSheet")
        }
    }

    // 🔹 Function to Set Up RecyclerView

    private fun setupRecyclerView() {
        getImageAndVideoViewModel.fetchAllMedia()
        imageAndVideoAdapter = GridAdapter(this)
        binding.rvGrid.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity, 2)
            adapter = imageAndVideoAdapter
        }
    }

    // 🔹 Observe LiveData for Media Updates
    private fun observeLiveData() {
        getImageAndVideoViewModel.mediaList.observe(this, Observer { mediaList ->
            binding.progressBar.visibility = View.GONE // Hide loader when data is ready

            mediaList?.let { list ->
                if (list.isNotEmpty()) {
                    imageAndVideoListDataRecycler.clear()
                    imageAndVideoListDataRecycler.addAll(list)

                    // ✅ Update RecyclerView using LiveData
                    imageAndVideoAdapter.submitList(ArrayList(list))
                    binding.nodata.visibility = View.GONE
                    binding.rvGrid.visibility = View.VISIBLE

                }else{
                    binding.nodata.visibility = View.VISIBLE
                    binding.rvGrid.visibility = View.GONE
                }
            }


        })
    }

    // 🔹 Implement Search Functionality
    private fun setupSearchView() {
        binding.toolbar.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // No action needed on submit
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMedia(newText.orEmpty()) // Call filter function
                return true
            }
        })
    }

    // 🔹 Filter RecyclerView Data Based on Search Query
    private fun filterMedia(query: String) {
        val filteredList = imageAndVideoListDataRecycler.filter { media ->
            media.type.contains(query, ignoreCase = true)
        }
        imageAndVideoAdapter.submitList(ArrayList(filteredList)) // ✅ Update adapter with filtered results
    }

    // 🔹 Login Function (Called Only When Necessary)
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Login successful! User ID: ${auth.currentUser?.uid}")
                    getImageAndVideoViewModel.fetchAllMedia() // Load data after login
                } else {
                    println("Login failed: ${task.exception?.message}")
                }
            }
    }

    override fun onItemClick(media: MediaModel) {
        val intent = Intent(this, GalleryViewActivity::class.java)
        intent.putExtra("KEY_TYPE", media.type)
        intent.putExtra("KEY_URL", media.url)
        startActivity(intent)
    }



    override fun onBackPressed() {
        super.onBackPressed()
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finishAffinity()// Close activity
            }
            .setNegativeButton("No", null)
            .show()
    }
}


