package com.piotrgluszek.announcementboard.view

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.ilhasoft.support.validation.Validator
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.databinding.ActivityRegistrationBinding
import com.piotrgluszek.announcementboard.dto.ApiMessage
import com.piotrgluszek.announcementboard.dto.RegistrationData
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.extenstions.replaceIfEmpty
import com.piotrgluszek.announcementboard.extenstions.toast
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.interfaces.CanReact
import com.piotrgluszek.announcementboard.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_registration.*
import okhttp3.ResponseBody
import retrofit2.Converter
import javax.inject.Inject

class Registration : AppCompatActivity(), CanReact {

    companion object {
        const val LOG_TAG = "LOGIN_REGISTRATION"
    }

    @Inject
    lateinit var converter: Converter<ResponseBody, ApiMessage>
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component.inject(this)
        setContentView(R.layout.activity_registration)
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        val binding: ActivityRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        val validator = Validator(binding)

        register_submit.setOnClickListener {
            if(validator.validate()){
                val data = getRegistrationData()
                userViewModel.create(data, this)
            }

        }
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

    override fun onSuccess(action: Action) {
        when (action) {
            Action.CREATE -> {
                toast("Account created. You may now log in")
                finish()
            }
        }
    }

    override fun onFail(action: Action, t: Throwable) {
        when (action) {
            Action.CREATE -> {
                t.message?.let {
                    toast(it)
                }
            }
        }
    }
}
