package com.alcherainc.gitapitest

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.alcherainc.gitapitest.databinding.ActivityMainBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.URI
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    var latestUrl = ""
    var savePath = ""
    var latestVersion = "" //앞에 v붙여둠

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initUI(binding)


    }

    private fun initUI(binding: ActivityMainBinding){
        Log.e("code", BuildConfig.VERSION_CODE.toString())
        Log.e("name", BuildConfig.VERSION_NAME)
        var code = "Version Code : ${BuildConfig.VERSION_CODE}"
        binding.tvVersionCode.text = code
        var name = "Version Name :  ${BuildConfig.VERSION_NAME}"
        binding.tvVersionName.text = name

        binding.btnDownload.setOnClickListener{
            if(checkVersion()){
                Toast.makeText(applicationContext, "다운로드를 진행합니다.", Toast.LENGTH_SHORT).show()
                download()
            } else {
                Toast.makeText(applicationContext, "이미 최신 버전입니다", Toast.LENGTH_SHORT).show()
            }
        }

        val iGithub = GithubClient.getApi().create(GithubService::class.java)
        iGithub.getRepos().enqueue(object : Callback<Release> {
            override fun onFailure(call: Call<Release>, t: Throwable) {
                Log.e("fail", t.message.toString())

            }

            override fun onResponse(
                call: Call<Release>,
                response: Response<Release>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.e("body", data.toString())
                    latestUrl = data!!.assets[0].browser_download_url
                    latestVersion = data!!.name
                } else {
                    Log.e("res fail", response.toString())
                }
            }
        })
    }

    fun download(){
        Log.e("a", latestUrl)
        val iDownload = GithubClient.getDownloadApi().create(GithubService::class.java)
        iDownload.download(latestUrl).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("fail", t.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val fileName=latestUrl.substring(latestUrl.lastIndexOf("/")+1)
                    savePath = application.filesDir.absolutePath+"/"+fileName
                    Log.e("path", savePath)
                    saveFile(response.body(), savePath)
                    install()
                }
            }
        })
    }

    fun saveFile(body: ResponseBody?, savePath: String):String{
        if (body==null)
            return ""
        var input: InputStream? = null
        try {
            input = body.byteStream()
            val file = File(savePath)
            if(file.exists()){
                file.delete()
            }

            val fos = FileOutputStream(savePath)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }


            return savePath
        }catch (e:Exception){
            Log.e("saveFile",e.toString())
        }
        finally {
            input?.close()
            Log.e("save", "done")
        }
        return ""
    }

    fun install(){
        val file = File(savePath)
        Log.e("tag", file.exists().toString())
        if(!file.exists()){
            Log.e("tag", "file does not exist")
        }
        val uri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".fileprovider", file)
        } else{
            Uri.fromFile(file)
        }

        val openFileIntent = Intent(Intent.ACTION_VIEW)
        openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        openFileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        openFileIntent.setDataAndType(uri, "application/vnd.android.package-archive")
        startActivity(openFileIntent)
        finish()
    }

    private fun checkVersion() : Boolean{
        val currentVersionSplit = BuildConfig.VERSION_NAME.split(".")
        val latestVersionSplit = latestVersion.substring(1).split(".")

        val length = max(currentVersionSplit.size, latestVersionSplit.size)
        var chk = 0
        for(i in 0 until length){
            var c = if(i < currentVersionSplit.size) currentVersionSplit[i].toInt() else 0
            var l = if(i < latestVersionSplit.size) latestVersionSplit[i].toInt() else 0
            if(c<=l){
                chk += l - c
            } else {
                if(chk<=0){
                    return false
                }
            }
        }

        return chk > 0
    }

}