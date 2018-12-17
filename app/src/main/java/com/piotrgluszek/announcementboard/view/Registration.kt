package com.piotrgluszek.announcementboard.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.dto.ApiMessage
import com.piotrgluszek.announcementboard.dto.RegistrationData
import com.piotrgluszek.announcementboard.extenstions.replaceIfEmpty
import com.piotrgluszek.announcementboard.injection.ApiComponent
import com.piotrgluszek.announcementboard.injection.ApiModule
import com.piotrgluszek.announcementboard.injection.DaggerApiComponent
import kotlinx.android.synthetic.main.activity_registration.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import javax.inject.Inject

class Registration : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "LOGIN_REGISTRATION"
        const val REQ_FAIL = "Failed to send request: %s"
    }

    @Inject
    lateinit var announcementApi: AnnouncementsApi
    @Inject
    lateinit var converter: Converter<ResponseBody, ApiMessage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getApiComponent().inject(this)
        setContentView(R.layout.activity_registration)

        register_submit.setOnClickListener {
            attemptRegister(getRegistrationData())

        }
    }

    private fun attemptRegister(registrationData: RegistrationData) {
        announcementApi.register(registrationData).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@Registration, Login::class.java)
                    startActivity(intent)
                } else {
                    val message = converter.convert(response.errorBody())?.message
                    Log.d(LOG_TAG, String.format(REQ_FAIL, message))
                    Toast.makeText(this@Registration, message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e(LOG_TAG, String.format(Login.REQ_FAIL, t.message), t)
                Toast.makeText(this@Registration, "Connection problem occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getApiComponent(): ApiComponent {
        return DaggerApiComponent.builder().apiModule(ApiModule(application)).build();
    }

    private fun getRegistrationData(): RegistrationData {
        return RegistrationData(
            username.text.toString(),
            pwd.text.toString(),
            null,
            name.text.toString().replaceIfEmpty(null),
            mail.text.toString().replaceIfEmpty(null),
            phone.text.toString().replaceIfEmpty(null)
        )
    }
}
