package ca.bntec.itineraireplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import classes.AppGlobal
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    //lateinit var mAuth: FirebaseAuth
    val database = AppGlobal.instance.database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //mAuth = FirebaseAuth.getInstance()

        // in on start method checking if
        // the user is already sign in.
        val user = database.getCurrentUser()
        if (user == null) {
            // if the user is not null then we are
            // opening a main activity on below line.
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        } else {

            val txt = findViewById<TextView>(R.id.hello)

            txt.text = "Hello ${user.name}"
        }

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
                    var result = async { database.logout() }.await()
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