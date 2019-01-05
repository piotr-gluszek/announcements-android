package com.piotrgluszek.announcementboard.view


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import br.com.ilhasoft.support.validation.Validator
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.auth.TokenStorage
import com.piotrgluszek.announcementboard.databinding.ActivityLoginBinding
import com.piotrgluszek.announcementboard.dto.ApiMessage
import com.piotrgluszek.announcementboard.dto.Credentials
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.exceptions.user.InvalidCredentials
import com.piotrgluszek.announcementboard.extenstions.toast
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.interfaces.CanReact
import com.piotrgluszek.announcementboard.viewmodel.UserViewModel
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import okio.Utf8
import retrofit2.Converter
import java.nio.charset.Charset
import javax.crypto.SecretKey
import javax.inject.Inject


class Login : AppCompatActivity(), CanReact {
    companion object {
        const val LOG_TAG = "LOGIN_ACTIVITY"
        const val REQ_FAIL = "Failed to send request: %s"
    }

    @Inject
    lateinit var tokenStorage: TokenStorage
    @Inject
    lateinit var converter: Converter<ResponseBody, ApiMessage>
    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component.inject(this)
        if(tokenStorage.sharedPreferences.contains("jwt")){
            val token = tokenStorage.sharedPreferences.getString("jwt", null)
            autoLogin(token)
        }
        setContentView(R.layout.activity_login)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val validator = Validator(binding)
        validator.enableFormValidationMode()

        register_link.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
        log_in.setOnClickListener {
            if (validator.validate())
                attemptLogin(username.text.toString(), password.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        password.text.clear()
    }

    private fun attemptLogin(username: String, password: String) {
        userViewModel.authenticate(Credentials(username, password), this)
    }

    override fun onSuccess(action: Action, data: Any?) {
        when (action) {
            Action.AUTHENTICATE -> {
                val token = data as String
                tokenStorage.store(token)
                val userId = tokenStorage.readUserId(token)
                Log.v(LOG_TAG, "User ID obtained from token: $token is: $userId ")
                userViewModel.get(userId, this@Login)
            }
            Action.GET_ONE -> {
                val intent = Intent(this@Login, Board::class.java)
                startActivity(intent)
                Log.d(LOG_TAG, "User data fetched successfully")
            }
        }
    }

    override fun onFail(action: Action, t: Throwable) {
        when (t) {
            is InvalidCredentials -> toast("Bad credentials")
            is NoSuchElementException -> toast("Failed to fetch user data")
            else -> toast("Connection problem")
        }
    }

    private fun autoLogin(token: String){
        tokenStorage.store(token)
        val userId = tokenStorage.readUserId(token)
        Log.v(LOG_TAG, "User ID obtained from token: $token is: $userId ")
        userViewModel.get(userId, this@Login)
    }
}
