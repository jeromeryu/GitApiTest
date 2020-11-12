package com.alcherainc.gitapitest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alcherainc.gitapitest.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        setSupportActionBar(findViewById(R.id.toolbar))

        initUI(binding)
    }

    private fun initUI(binding: ActivityMainBinding){
        Log.e("code", BuildConfig.VERSION_CODE.toString())
        Log.e("name", BuildConfig.VERSION_NAME)
        var code = "Version Code : ${BuildConfig.VERSION_CODE}"
        binding.tvVersionCode.text = code
        var name = "Version Name :  ${BuildConfig.VERSION_NAME}"
        binding.tvVersionName.text = name


        val iGithub = GithubClient.getApi().create(GithubService::class.java)
        iGithub.getRepos().enqueue(object : Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("fail", "fail")
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if(response.isSuccessful){
                    val data = response.body()
                    Log.e("body", data.toString())
                } else {
                    Log.e("res fail", response.toString())
                }
            }
        })


    }

}