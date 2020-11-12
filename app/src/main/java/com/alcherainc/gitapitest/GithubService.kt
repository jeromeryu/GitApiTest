package com.alcherainc.gitapitest

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {

    @GET("users/AlcheraInc/cpp-service-template")
    fun getRepos() : Call<String>
}

data class GithubRepo(
    @SerializedName("name") val name: String,
    @SerializedName("id") val id: String,
    @SerializedName("created_at") val date: String,
    @SerializedName("html_url") val url: String
)
