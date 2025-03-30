package com.project.gatherly.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.gatherly.Model.Adapter.StallsAdapter
import com.project.gatherly.Model.Repo.StallsData
import com.project.gatherly.R
import com.project.gatherly.ViewModel.StallsDetails
import com.project.gatherly.databinding.ActivityStallsBinding

class StallsActivity : AppCompatActivity() {

    private lateinit var stallsBinding: ActivityStallsBinding
    private lateinit var occupantHomeScreenViewModel: StallsDetails
    private lateinit var stallsAdapter: StallsAdapter
    private var blogListDataRecycler: ArrayList<StallsData> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        stallsBinding = ActivityStallsBinding.inflate(layoutInflater)
        setContentView(stallsBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        occupantHomeScreenViewModel = ViewModelProvider(this)[StallsDetails::class.java]
        occupantHomeScreenViewModel.getBlogsData()

        stallsBinding.rvStallList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        stallsBinding.rvStallList.apply {
            stallsAdapter = StallsAdapter(this@StallsActivity, blogListDataRecycler)
            layoutManager = GridLayoutManager(this@StallsActivity, 1)
            adapter = stallsAdapter

        }
        occupantHomeScreenViewModel.blogsListData.observe(this, Observer {
            blogListDataRecycler = it
            if (it != null) {
                if (it.isNotEmpty()) {

                    stallsBinding.rvStallList.apply {
                        stallsAdapter = StallsAdapter(this@StallsActivity, blogListDataRecycler)
                        layoutManager = GridLayoutManager(this@StallsActivity, 1)
                        adapter = stallsAdapter

                    }
                }
            }

        })

    }
}