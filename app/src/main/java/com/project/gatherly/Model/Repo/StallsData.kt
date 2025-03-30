package com.project.gatherly.Model.Repo

import java.util.ArrayList

data class StallsData(
    var date: String? = null,
    var files: String? = null,
    var imageUrl: String? = null,
    var blogsimageUrlList: ArrayList<String>? = null,
    var name: String? = null,
    var company: String? = null,
) {

}


data class ImagesAndVideoData(
    var date: String? = null,
    var files: String? = null,
    var imageUrl: String? = null,
    var blogsimageUrlList: ArrayList<String>? = null,
    var name: String? = null,
    var video: String? = null,
    var type: String? = null

) {

}


data class MediaModel(
    val id: String = "",
    val url: String = "",
    val type: String = ""
)