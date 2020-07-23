package my.weather.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_accound_setting.*

class AccoundSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accound_setting)

        setupListener()
    }

    private fun setupListener() {
        // onBackPressed() 뒤로가기
        account_setting_back.setOnClickListener { onBackPressed() }
        // 로그아웃
        account_setting_logout.setOnClickListener { signOutAccount() }
        // 계정탈퇴
        account_setting_delete.setOnClickListener { showDeleteDialog() }
    }


    private fun signOutAccount() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                moveToMainActivity()
                Toast.makeText(this,"로그아웃",Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteAccount() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                moveToMainActivity()
                Toast.makeText(this,"계정탈퇴",Toast.LENGTH_SHORT).show()
            }

    }
    private fun showDeleteDialog(){
        AccountDeleteDialog().apply {
            addAccountDeleteDialogInterface(object : AccountDeleteDialog.AccountDeleteDialogInterface {
                override fun delete() {
                    deleteAccount()
                }
                override fun cancleDelete() {
                }
            })
        }.show(supportFragmentManager,"")
    }

    private fun moveToMainActivity(){
        startActivity(Intent(this,MainActivity::class.java))
    }

}
