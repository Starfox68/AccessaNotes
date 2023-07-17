package com.shaphr.accessanotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class AuthActivity : AppCompatActivity{
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_APPLICATION_CLIENT_ID")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // val googleLoginButton = findViewById<Button>(R.id.google_login_btn)
        // googleLoginButton.setOnClickListener {
        //     signIn()
        // }
    }

//    private fun signIn() {
//        val signInIntent = mGoogleSignInClient.signInIntent
//        startActivityForResult(
//            signInIntent, RC_SIGN_IN
//        )
//    }

// ...

    companion object {
        const val RC_SIGN_IN = 9001
    }
}