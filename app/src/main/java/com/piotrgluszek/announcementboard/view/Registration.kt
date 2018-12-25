package com.piotrgluszek.announcementboard.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import br.com.ilhasoft.support.validation.Validator
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.databinding.ActivityRegistrationBinding
import com.piotrgluszek.announcementboard.dto.ApiMessage
import com.piotrgluszek.announcementboard.dto.RegistrationData
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.extenstions.replaceIfEmpty
import com.piotrgluszek.announcementboard.extenstions.toast
import com.piotrgluszek.announcementboard.image.ImageConverter
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
        const val PICK_PHOTO = 1
    }

    @Inject
    lateinit var converter: Converter<ResponseBody, ApiMessage>
    lateinit var userViewModel: UserViewModel
    private var isPhotoDefault = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component.inject(this)
        setContentView(R.layout.activity_registration)
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        val binding: ActivityRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        val validator = Validator(binding)
        validator.enableFormValidationMode()

        register_submit.setOnClickListener {
            if (isRegistrationDataValid(validator)) {
                val data = getRegistrationData()
                userViewModel.create(data, this)
            }
        }
        photo.setOnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, PICK_PHOTO)
        }
    }

    private fun getRegistrationData(): RegistrationData {
        var newPhoto: String? = null
        if(!isPhotoDefault){
            val bitmap = (photo.drawable as? BitmapDrawable)?.bitmap
            if (bitmap != null)
                newPhoto = ImageConverter.toBase64(bitmap)
        }
        return RegistrationData(
            username.text.toString(),
            pwd.text.toString(),
            newPhoto,
            name.text.toString().replaceIfEmpty(null),
            mail.text.toString().replaceIfEmpty(null),
            phone.text.toString().replaceIfEmpty(null)
        )
    }

    private fun isRegistrationDataValid(validator: Validator): Boolean {
        val inputs = mutableListOf<EditText>(username, pwd)
        val arePasswordsNotEmpty = pwd.text.isNotEmpty() && rep_pwd.text.isNotEmpty()
        val arePasswordsNotIdentical = pwd.text.toString() != rep_pwd.text.toString()
        if (arePasswordsNotIdentical && arePasswordsNotEmpty) {
            this@Registration.toast("Passwords are not identical")
            return false
        } else if (mail.text.isNotEmpty() || phone.text.isNotEmpty()) {
            val optionalInputs = listOf<EditText>(phone, mail, name)
            inputs.addAll(optionalInputs.filter { input -> input.text.isNotEmpty() })
            return validator.validate(inputs)
        }
        this@Registration.toast("Contact info (mail or phone) required")
        return false
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_PHOTO && resultCode == RESULT_OK) {
            val imageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            photo.setImageBitmap(bitmap)
            isPhotoDefault = false
        }
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
