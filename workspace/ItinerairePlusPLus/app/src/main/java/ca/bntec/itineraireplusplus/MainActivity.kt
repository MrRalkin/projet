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
import interfaces.user.IActivity
import interfaces.user.IDestination
import interfaces.user.IStep
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
                        txt.text = "Hello ${user.name}"
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
            setTestData()
        }


    }




    fun setTestData() {
        user.address = Address("829 rue paris", "Paris", "Qc", "J4J1A5", "Canada")
        var destination: IDestination = Destination()
        destination.name = "First destination"
        destination.address = Address("", "Ottawa", "On", "", "Canada")//
        destination.coord = Coord("45.2487862", "-76.3606792")
        destination.trip_time = 2400


        var act = ArrayList<IActivity>()
        act.add(Activity(1, "Manger", 120))
        act.add(Activity(2, "Back home", 2400))

        var step: Step = Step(
            1,
            Point(
                "Montreal",
                Coord("-12.000909", "-12.000909"),
                Address("", "Montreal", "Qc", "", "Canada")
            ),
            Point(
                "Ottawa",
                Coord("-12.000909", "-12.000909"),
                Address("", "Ottawa", "On", "", "Canada")
            ),
            trip_time = 2400,
            activities = act
        )
        var steps = ArrayList<IStep>()
        steps.add(step)

        destination.steps = steps
        var destanations = ArrayList<IDestination>()
        destanations.add(destination)
        user.destinations = destanations
        MainScope().launch(Dispatchers.IO) {
         var result=  async{ db.userUpdateCurrent(user)}.await()
            var t=result.isSuccess
        }
//
//        "step" : 1,
//        "start": {
//            "name" : "Montreal",
//            "coord" : {"longitude" : "-12.000909", "latitude" : "-12.000909"},
//            "adresse" : {
//            "no_civic" : "123",
//            "street" : "rue paris",
//            "city" : "Paris",
//            "province" : "oups",
//            "postal_code" : "J4J 3K5",
//            "country" : "France",
//        },
//        },
//        "end" : {
//            "name" : "New York",
//            "coord" : {"longitude" : "-12.000909", "latitude" : "-12.000909"},
//            "adresse" : {
//            "no_civic" : "123",
//            "street" : "rue paris",
//            "city" : "Paris",
//            "province" : "oups",
//            "postal_code" : "J4J 3K5",
//            "country" : "France",
//        },
//        },
//        "trip_time_minutes" : 360,
//        "activities": [
//        {"activity" : 1, "name" : "Manger", "time_minutes" : "120"},
//        {"activity" : 2, "name" : "Essence", "time_minutes" : "15"}]


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // adding a click listener for option selected on below line.
        val id = item.itemId
        return when (id) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


}