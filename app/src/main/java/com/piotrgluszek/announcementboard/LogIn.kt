package com.piotrgluszek.announcementboard


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*

class LogIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        register_link.setOnClickListener {
            val intent = Intent(this@LogIn, UserRegistration::class.java)
            startActivity(intent)
        }

        log_in.setOnClickListener {
            validateCredentials(username.text.toString(), password.text.toString())}
    }

    fun validateCredentials(username : String, password : String) : Boolean {
        //TODO: send credentials validation request to API
        return true;
    }


}
