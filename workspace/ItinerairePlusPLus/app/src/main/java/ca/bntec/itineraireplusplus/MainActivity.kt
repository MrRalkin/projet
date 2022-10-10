package ca.bntec.itineraireplusplus

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import classes.*
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ca.bntec.itineraireplusplus.data.TestData


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
                        txt.text = "Bonjour ${user.name}"
                        recentesDestinations()
                        newDestBtnListener()
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

      /*  var btnSetData = findViewById<Button>(R.id.setData)
        btnSetData.setOnClickListener() {
            TestData().setTestData(user)
        }

        var btnResetDate = findViewById<Button>(R.id.getData)
        btnResetDate.setOnClickListener() {
            resetSettings()
        }*/

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

    private fun recentesDestinations(){
        // use arrayadapter and define an array
        val arrayAdapter: ArrayAdapter<*>
//        val previousdest = arrayOf(
//            user.destinations?.get(0)?.name,  user.destinations?.get(1)?.name,  user.destinations?.get(2)?.name,
//            user.destinations?.get(3)?.name,  user.destinations?.get(4)?.name
//        )

        val previousdest = ArrayList<String>()

        val nbDest = if (user.destinations!!.size > 5) 5 else user.destinations!!.size

        for ((idx, destination) in user.destinations!!.withIndex()) {
            if (idx < nbDest)
                previousdest.add(destination.name)
            else
                break
        }

        // access the listView from xml file
        var mListView = findViewById<ListView>(R.id.lv_recentes_destinations)
        arrayAdapter = ArrayAdapter(this,
            R.layout.listrow, R.id.textView2,previousdest)
        mListView.adapter = arrayAdapter

        mListView.setBackgroundColor(Color.argb(80,0,0 ,0))
    }

    fun newDestBtnListener() {
        var main_adddestination_btn = findViewById<Button>(R.id.main_adddestination_btn)
        main_adddestination_btn.setOnClickListener { // opening a login activity on clicking login text.
            val i = Intent(this, AddDestinationActivity::class.java)
            startActivity(i)
        }
    }
}

