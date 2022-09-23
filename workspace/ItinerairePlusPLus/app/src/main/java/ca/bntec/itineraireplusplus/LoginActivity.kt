package ca.bntec.itineraireplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import classes.AppGlobal
import classes.auth.Login
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    lateinit var userNameEdt: TextInputEditText
    lateinit var passwordEdt: TextInputEditText
    lateinit var loginBtn: Button
    lateinit var newUserTV: TextView
    val userManager = AppGlobal.instance.userManager
    lateinit var loadingPB: ProgressBar
    lateinit var msgShow: Toast
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        msgShow = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        userNameEdt = findViewById(R.id.idEdtUserName)
        passwordEdt = findViewById(R.id.idEdtPassword)
        loginBtn = findViewById(R.id.idBtnLogin)
        newUserTV = findViewById(R.id.idTVNewUser)
        loadingPB = findViewById(R.id.idPBLoading)


        newUserTV.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }


        loginBtn.setOnClickListener(View.OnClickListener {
            // hiding our progress bar.
            loadingPB.visibility = View.VISIBLE
            // getting data from our edit text on below line.
            val email = userNameEdt.text.toString()
            val password = passwordEdt.text.toString()
            // on below line validating the text input.
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                showMessage(
                    "Please enter your credentials.."
                )
                return@OnClickListener
            }

            MainScope().launch(Dispatchers.IO) {
                val result = async { userManager.userLogin(Login(email, password)) }.await()
                if (result.isSuccess) {
                    async { userManager.userGetCurrent() }.await()
                    this@LoginActivity.runOnUiThread(java.lang.Runnable {
                        showMessage("Login Successful..")
                        loadingPB.visibility = View.GONE

                        // on below line we are opening our mainactivity.
                        val i = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    })
                } else {

                    this@LoginActivity.runOnUiThread(java.lang.Runnable {
                        loadingPB.visibility = View.GONE
                        showMessage("Please enter valid user credentials..")
                    })
                }
            }


        })
    }

    fun showMessage(message: String) {
        msgShow.setText(message)
        msgShow.show()

    }
}