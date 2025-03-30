package com.project.gatherly.ViewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.gatherly.Model.Repo.ImagesAndVideoData
import com.project.gatherly.Model.Repo.MediaModel
import com.project.gatherly.Model.Repo.StallDetailsRepo
import com.project.gatherly.Model.Repo.StallsData
import kotlinx.coroutines.launch

class StallsDetails(application: Application) : AndroidViewModel(application) {
    // Define properties that represent data needed by the view
    private var utilsRepository: StallDetailsRepo = StallDetailsRepo(application)
    val blogsListData: MutableLiveData<ArrayList<StallsData>> = utilsRepository.blogsListdata
    private val _mediaList = MutableLiveData<List<MediaModel>>()
    val mediaList: LiveData<List<MediaModel>> get() = _mediaList

    fun getBlogsData() {
        utilsRepository.getBlogsList()
    }






    fun uploadMedia(uri: Uri, type: String) {
        viewModelScope.launch {
            val downloadUrl = utilsRepository.uploadMedia(uri, type)
            if (downloadUrl != null) {
                println("Upload Success: $downloadUrl")

                // ✅ Directly refresh media list after upload
                fetchAllMedia()
            } else {
                println("Upload Failed")
            }
        }
    }

    // ✅ Renamed to "fetchAllMedia" for clarity
    fun fetchAllMedia() {
        viewModelScope.launch {
            val mediaList = utilsRepository.getAllMedia()
            _mediaList.postValue(mediaList) // ✅ Updates LiveData
        }
    }
}