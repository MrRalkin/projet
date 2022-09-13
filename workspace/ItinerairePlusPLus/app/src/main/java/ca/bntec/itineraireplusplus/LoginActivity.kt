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

class LoginActivity : AppCompatActivity() {
    lateinit var userNameEdt: TextInputEditText
    lateinit var passwordEdt: TextInputEditText
    lateinit var loginBtn: Button
    lateinit var newUserTV: TextView
    lateinit var mAuth: FirebaseAuth
    lateinit var loadingPB: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // initializing all our variables.
        userNameEdt = findViewById(R.id.idEdtUserName)
        passwordEdt = findViewById(R.id.idEdtPassword)
        loginBtn = findViewById(R.id.idBtnLogin)
        newUserTV = findViewById(R.id.idTVNewUser)
        mAuth = FirebaseAuth.getInstance()
        loadingPB = findViewById(R.id.idPBLoading)

        // adding click listener for our new user tv.
        newUserTV.setOnClickListener { // on below line opening a login activity.
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

        // adding on click listener for our login button.
        loginBtn.setOnClickListener(View.OnClickListener {
            // hiding our progress bar.
            loadingPB.visibility = View.VISIBLE
            // getting data from our edit text on below line.
            val email = userNameEdt.text.toString()
            val password = passwordEdt.text.toString()
            // on below line validating the text input.
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter your credentials..",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            // on below line we are calling a sign in method and passing email and password to it.
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                // on below line we are checking if the task is success or not.
                if (task.isSuccessful) {
                    // on below line we are hiding our progress bar.
                    loadingPB.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Login Successful..", Toast.LENGTH_SHORT)
                        .show()
                    // on below line we are opening our mainactivity.
                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    // hiding our progress bar and displaying a toast message.
                    loadingPB.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter valid user credentials..",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}