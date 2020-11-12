package com.alcherainc.gitapitest

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url


interface GithubService {

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("repos/jeromeryu/GitApiTest/releases/latest")
    fun getRepos() : Call<Release>

    @Headers("Accept: application/vnd.github.v3+json")
    @GET
    fun download(@Url url:String) : Call<ResponseBody>

}

data class Release(
    @SerializedName("url") val url: String,
    @SerializedName("id") val id: Int,
    @SerializedName("assets") val assets : List<Assets>,
    @SerializedName("name") val name : String
)

data class Assets(
    @SerializedName("url") val url: String,
    @SerializedName("browser_download_url") val browser_download_url: String
)

data class result(
    @SerializedName("code") val code : Int
)
