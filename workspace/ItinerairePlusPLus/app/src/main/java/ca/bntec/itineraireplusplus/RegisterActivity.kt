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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    lateinit var userNameEdt: TextInputEditText
    lateinit var passwordEdt: TextInputEditText
    lateinit var confirmPwdEdt: TextInputEditText
    lateinit var loginTV: TextView
    lateinit var registerBtn: Button
    lateinit var mAuth: FirebaseAuth
    lateinit var loadingPB: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        // initializing all our variables.
        userNameEdt = findViewById(R.id.idEdtUserName)
        passwordEdt = findViewById(R.id.idEdtPassword)
        loadingPB = findViewById(R.id.idPBLoading)
        confirmPwdEdt = findViewById(R.id.idEdtConfirmPassword)
        loginTV = findViewById(R.id.idTVLoginUser)
        registerBtn = findViewById(R.id.idBtnRegister)
        mAuth = FirebaseAuth.getInstance()


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

            // checking if the password and confirm password is equal or not.
            if (pwd != cnfPwd) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Please check both having same password..",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(pwd) && TextUtils.isEmpty(
                    cnfPwd
                )
            ) {

                // checking if the text fields are empty or not.
                Toast.makeText(
                    this@RegisterActivity,
                    "Please enter your credentials..",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {

                // on below line we are creating a new user by passing email and password.
                mAuth.createUserWithEmailAndPassword(userName, pwd).addOnCompleteListener { task ->
                    // on below line we are checking if the task is success or not.
                    if (task.isSuccessful) {

                        // in on success method we are hiding our progress bar and opening a login activity.
                        loadingPB.visibility = View.GONE
                        Toast.makeText(
                            this@RegisterActivity,
                            "User Registered..",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val i = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {

                        // in else condition we are displaying a failure toast message.
                        loadingPB.visibility = View.GONE
                        Toast.makeText(
                            this@RegisterActivity,
                            "Fail to register user..",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }

    }

}