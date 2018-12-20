package com.piotrgluszek.announcementboard.view


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.auth.TokenStorage
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.dto.ApiMessage
import com.piotrgluszek.announcementboard.dto.Credentials
import com.piotrgluszek.announcementboard.injection.ApiComponent
import com.piotrgluszek.announcementboard.injection.ApiModule
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.injection.DaggerApiComponent
import kotlinx.android.synthetic.main.acrivity_login.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import javax.inject.Inject

class Login : AppCompatActivity() {
    companion object {
        const val LOG_TAG = "LOGIN_ACTIVITY"
        const val REQ_FAIL = "Failed to send request: %s"
    }

    @Inject
    lateinit var announcementApi: AnnouncementsApi
    @Inject
    lateinit var tokenStorage: TokenStorage
    @Inject
    lateinit var converter: Converter<ResponseBody, ApiMessage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acrivity_login)
        getApiComponent()?.inject(this)
        register_link.setOnClickListener {
            val intent = Intent(this@Login, Registration::class.java)
            startActivity(intent)
        }
        log_in.setOnClickListener {
            attemptLogin(username.text.toString(), password.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        password.text.clear()
    }

    private fun attemptLogin(username: String, password: String) {
        announcementApi.login(Credentials(username, password)).enqueue(object : Callback<ApiMessage> {
            override fun onFailure(call: Call<ApiMessage>, t: Throwable) {
                Log.e(LOG_TAG, String.format(REQ_FAIL, t.message), t)
                Toast.makeText(this@Login, "Connection problem occurred", Toast.LENGTH_SHORT)
            }

            override fun onResponse(call: Call<ApiMessage>, response: Response<ApiMessage>) {
                when (response.code()) {
                    200 -> {
                        val token = response.body()?.message
                        tokenStorage.store(token)
                        val intent = Intent(this@Login, Board::class.java)
                        startActivity(intent)
                    }
                    403 -> {
                        Toast.makeText(this@Login, converter.convert(response.errorBody())?.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

    private fun getApiComponent(): ApiComponent? {
        return App.component
        //DaggerApiComponent.builder().apiModule(ApiModule(application)).build();
    }
}
