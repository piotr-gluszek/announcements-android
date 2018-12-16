package com.piotrgluszek.announcementboard.view


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.injection.ApiComponent
import com.piotrgluszek.announcementboard.injection.ApiModule
import com.piotrgluszek.announcementboard.injection.DaggerApiComponent
import kotlinx.android.synthetic.main.acrivity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class Login : AppCompatActivity() {


    @Inject
    lateinit var announcementApi: AnnouncementsApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acrivity_login)
        getApiComponent().inject(this)
        register_link.setOnClickListener {
            val intent = Intent(this@Login, Registration::class.java)
            startActivity(intent)
        }
        log_in.setOnClickListener {
            validateCredentials(username.text.toString(), password.text.toString())
        }
    }

    fun validateCredentials(username: String, password: String): Boolean {
          announcementApi.getAllAnnouncements().enqueue(object : Callback<Any> {
               override fun onFailure(call: Call<Any>?, t: Throwable?) {
                   Log.e("GET ANNOUNCEMENTS", "Reqest failed", t)
               }

               override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
                   Log.e("GET ANNOUNCEMENTS", response.toString())
               }
           })

        return true;
    }
    fun getApiComponent() : ApiComponent {
        return DaggerApiComponent.builder().apiModule(ApiModule(application)).build();
    }
}
