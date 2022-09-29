package ca.bntec.itineraireplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import classes.AppGlobal
import classes.settings.Settings

import com.google.android.material.textfield.TextInputEditText
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddDestinationActivity : AppCompatActivity() {
    val appGlobal = AppGlobal.instance
    val db = appGlobal.userManager
    lateinit var inputDepart: TextInputEditText
    lateinit var inputDest: TextInputEditText
    lateinit var btnShowDetination: Button
    lateinit var user: IUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_destination)

        MainScope().launch(Dispatchers.IO) {
            if (db.userIsAuthenticated()) {
                user = db.userGetCurrent()!!
                this@AddDestinationActivity.runOnUiThread(java.lang.Runnable {
                    if (user == null) {
                        this@AddDestinationActivity.runOnUiThread(java.lang.Runnable {
                            val i = Intent(this@AddDestinationActivity, LoginActivity::class.java)
                            startActivity(i)
                            finish()
                        })
                    }
                })
            }
        }
        inputDepart = findViewById(R.id.textInputEdit_depart)
        inputDest = findViewById(R.id.textInputEdit_dest)
        btnShowDetination = findViewById(R.id.btnShowDestination)

        btnShowDetination.setOnClickListener() {

            if (inputDepart.text.toString().isEmpty()) {
                inputDepart.error = "Entrer le depart"
                return@setOnClickListener
            } else {
                inputDepart.error = null
            }
            if (inputDest.text.toString().isEmpty()) {
                inputDest.error = "Entrer le depart"
                return@setOnClickListener
            } else {
                inputDest.error = null
            }
            appGlobal.curSetting = Settings()
            appGlobal.departAddress = inputDepart.text.toString()
            appGlobal.destAddress = inputDest.text.toString()
            appGlobal.curSetting.activities = user.settings.activities
            appGlobal.curSetting.vehicles.add(user.settings.vehicles[0])
            appGlobal.curSetting.energies.add(user.settings.energies[0])

            val i = Intent(this@AddDestinationActivity, MapsActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}