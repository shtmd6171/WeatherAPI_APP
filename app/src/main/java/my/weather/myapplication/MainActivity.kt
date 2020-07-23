package my.weather.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1000
    private val EMAIL = "samantha.n.morton@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showLoginWindow()

    }

    // 이미 로그인한 적이 있으면 (user값이 존재 하면) startActivity를 수행하고
    // user값이 없다면 showLoginWindow()를 통해 로그인 창을 띄움
    private fun checkPreviewLogin() {
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("TEST",user?.email)
        if(user == null )
        {
            showLoginWindow()
        }
        else{
            moveToOpenWeatherActivity()
            Log.d("TEST", user?.email)
        }
    }

    private fun moveToOpenWeatherActivity() {
        startActivity(Intent(this, OpenWeatherActivity::class.java))
    }

    private fun showLoginWindow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.GreenTheme)
                .setLogo(R.drawable.weather_icon_png_5)
                .build(),
            RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                moveToOpenWeatherActivity()

            } else {
                Toast.makeText(this,"로그인 실패",Toast.LENGTH_SHORT).show()
            }
        }
    }

}
