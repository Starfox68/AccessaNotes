package com.shaphr.accessanotes.ui.screens

import android.R
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


//class Test : ComponentActivity() {
//
//    var gso: GoogleSignInOptions? = null;
//
//    var mGoogleSignInClient: GoogleSignInClient? = null;
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            Button()
//        }
//        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso!!);
//    }
//
//    @Composable
//    fun Button() {
//        Button(
//            onClick = {
//                signIn();
//            }
//        ){
//            Text(text = "Signin")
//        }
//    }
//
//    private fun signIn() {
//
//        val someActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult(),
//            ActivityResultCallback {result ->
//                if (result.resultCode == RESULT_OK){
//                    Log.d("NO", "WAY");
//                }
//            }
//        )
//
//        val signInIntent = mGoogleSignInClient!!.signInIntent
//
//        someActivityResultLauncher.launch(signInIntent);
//
//
////        val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
////            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(it.data);
////            handleSignInResult(task)
////        }
//
////        getResult.launch(signInIntent);
//
//
//    }
//
//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//
//            // Signed in successfully, show authenticated UI.
//            Log.i("YO", "NO WAY");
//        } catch (e: ApiException) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w("BRUH", "signInResult:failed code=" + e.statusCode)
//        }
//    }
//}

class Test : ComponentActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Button()
        }

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("803397781115-o5jto1m1lgdtmrc5c2pot61puggcrpf1.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        val getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == Activity.RESULT_OK){
                    Log.d("G", "OH BOY")
                }
            }

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    getResult.launch(result.pendingIntent)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("YER", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d("YER", e.localizedMessage)
            }
    }
}

@Composable
fun Button() {
    Button(
        onClick = {
        }
    ){
        Text(text = "Signin")
    }
}



