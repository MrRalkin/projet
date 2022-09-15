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
import classes.Register
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    lateinit var userNameEdt: TextInputEditText
    lateinit var passwordEdt: TextInputEditText
    lateinit var confirmPwdEdt: TextInputEditText
    lateinit var userEmailEdt: TextInputEditText
    lateinit var loginTV: TextView
    lateinit var registerBtn: Button
    val userManager = AppGlobal.instance.userManager
    lateinit var loadingPB: ProgressBar
    lateinit var msgShow: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        msgShow = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        // initializing all our variables.
        userNameEdt = findViewById(R.id.idEdtUserName)
        userEmailEdt = findViewById(R.id.idEdtEmail)
        passwordEdt = findViewById(R.id.idEdtPassword)
        loadingPB = findViewById(R.id.idPBLoading)
        confirmPwdEdt = findViewById(R.id.idEdtConfirmPassword)
        loginTV = findViewById(R.id.idTVLoginUser)
        registerBtn = findViewById(R.id.idBtnRegister)


        // adding on click for login tv.
        loginTV.setOnClickListener { // opening a login activity on clicking login text.
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }


        // adding click listener for register button.
        registerBtn.setOnClickListener {
            // hiding our progress bar.
            loadingPB.visibility = View.VISIBLE

            // getting data from our edit text.
            val userName = userNameEdt.text.toString()
            val pwd = passwordEdt.text.toString()
            val cnfPwd = confirmPwdEdt.text.toString()
            val email = userEmailEdt.text.toString()
            // checking if the password and confirm password is equal or not.
            if (pwd != cnfPwd) {
                showMessage(
                    "Please check both having same password..",
                   )
            } else if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(email) && TextUtils.isEmpty(
                    pwd
                ) && TextUtils.isEmpty(
                    cnfPwd
                )
            ) {

                // checking if the text fields are empty or not.
                showMessage(
                    "Please enter your credentials.."
                )

            } else {


                MainScope().launch(Dispatchers.IO) {

                    val result =
                        async { userManager.userRegister(Register(userName, email, pwd)) }.await()
                    if (result.isSuccess) {
                        // in on success method we are hiding our progress bar and opening a login activity.
                        this@RegisterActivity.runOnUiThread(java.lang.Runnable {
                            loadingPB.visibility = View.GONE
                            showMessage("User Registered..")


                        val i = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(i)
                        finish()
                        })
                    } else {

                        // in else condition we are displaying a failure toast message.
                        loadingPB.visibility = View.GONE
                        this@RegisterActivity.runOnUiThread(java.lang.Runnable {
                            showMessage("Fail to register user..")
                        })

                    }

                }

            }
        }

    }


    fun showMessage(message: String) {
        msgShow.setText(message)
        msgShow.show()

    }
}