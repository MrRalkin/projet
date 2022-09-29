package ca.bntec.itineraireplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import classes.*
import classes.settings.*
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ca.bntec.itineraireplusplus.data.TestData
import interfaces.user.IActivity
import interfaces.user.IDestination
import interfaces.user.IStep

class MainActivity : AppCompatActivity() {
    val db = AppGlobal.instance.userManager
    lateinit var user: IUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txt = findViewById<TextView>(R.id.hello)

        MainScope().launch(Dispatchers.IO) {
            if (db.userIsAuthenticated()) {
                user = db.userGetCurrent()!!
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    if (user != null) {
                        txt.text = "Hello ${user.settings.activities.get(0).name}"
//                        txt.text = "Hello ${user.settings.vehicles.get(0).type}"
                    }
                })
            } else {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    val i = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(i)
                    finish()
                })
            }
        }

        var btnSetData = findViewById<Button>(R.id.setData)
        btnSetData.setOnClickListener() {
            TestData().setTestData(user)
        }

        var btnResetDate = findViewById<Button>(R.id.getData)
        btnResetDate.setOnClickListener() {
            resetSettings()
        }

    }

    fun resetSettings() {
        MainScope().launch(Dispatchers.IO) {
            var result = async { db.resetSettingsToDefault() }.await()
            var t = result.isSuccess
            println("********************* RESET SETTINGS *********************")
            println("*** >>> " + if (result.isSuccess) {result.successMessage} else {result.errorMessage})
            println("**********************************************************")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // adding a click listener for option selected on below line.
        val id = item.itemId
        return when (id) {
            R.id.idSettings->{
                showSettings()
                true
            }

            R.id.idDestinations->{
                showDestinations()
                true
            }
            R.id.idMap->{
                showMap()
                true
            }
            R.id.idAddDestination->{
                addDestinations()
                true
            }
            R.id.idLogOut -> {
                // displaying a toast message on user logged out inside on click.
                Toast.makeText(applicationContext, "User Logged Out", Toast.LENGTH_LONG).show()
                // on below line we are signing out our user.
                MainScope().launch(Dispatchers.IO) {
                    var result = async { db.userLogout() }.await()
                    val i = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(i)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showMap(){
        val i = Intent(this@MainActivity, MapsActivity::class.java)
        startActivity(i)
    }
    fun showSettings(){
        val i = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(i)
    }

    fun showDestinations(){
        val i = Intent(this@MainActivity, DestinationsActivity::class.java)
        startActivity(i)
    }
    fun addDestinations(){
        val i = Intent(this@MainActivity, AddDestinationActivity::class.java)
        startActivity(i)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}