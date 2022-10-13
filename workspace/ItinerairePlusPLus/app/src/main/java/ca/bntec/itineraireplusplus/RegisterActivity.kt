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
import classes.auth.Register
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
    private val userManager = AppGlobal.instance.userManager
    lateinit var loadingPB: ProgressBar
    lateinit var msgShow: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        msgShow = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        userNameEdt = findViewById(R.id.idEdtUserName)
        userEmailEdt = findViewById(R.id.idEdtEmail)
        passwordEdt = findViewById(R.id.idEdtPassword)
        loadingPB = findViewById(R.id.idPBLoading)
        confirmPwdEdt = findViewById(R.id.idEdtConfirmPassword)
        loginTV = findViewById(R.id.idTVLoginUser)
        registerBtn = findViewById(R.id.idBtnRegister)


        loginTV.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

        registerBtn.setOnClickListener {
            loadingPB.visibility = View.VISIBLE

            val userName = userNameEdt.text.toString()
            val pwd = passwordEdt.text.toString()
            val cnfPwd = confirmPwdEdt.text.toString()
            val email = userEmailEdt.text.toString()
            if (pwd != cnfPwd) {
                showMessage("Veuillez vérifier que les deux mot de passe sont identiques")
                loadingPB.visibility = View.GONE
            } else if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(email) && TextUtils.isEmpty(pwd)
                && TextUtils.isEmpty(cnfPwd)) {
                showMessage("Veuillez saisir vos identifiants...")
                loadingPB.visibility = View.GONE
            } else {
                MainScope().launch(Dispatchers.IO) {
                    val result =
                        async { userManager.userRegister(Register(userName, email, pwd)) }.await()
                    if (result.isSuccess) {

                        this@RegisterActivity.runOnUiThread(java.lang.Runnable {
                            loadingPB.visibility = View.GONE
                            showMessage("Utilisateur enregistré...")

                            val i = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(i)
                            finish()
                        })
                    } else {

                        this@RegisterActivity.runOnUiThread(java.lang.Runnable {
                            loadingPB.visibility = View.GONE
                            showMessage("L'enregistrement de l'utilisateur a échoué...")
                        })
                    }
                }
            }
        }
    }

    private fun showMessage(message: String) {
        msgShow.setText(message)
        msgShow.show()

    }
}