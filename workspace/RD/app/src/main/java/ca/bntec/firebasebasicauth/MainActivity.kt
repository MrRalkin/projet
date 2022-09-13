package ca.bntec.firebasebasicauth

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        // in on start method checking if
        // the user is already sign in.
        val user = mAuth.currentUser
        if (user == null) {
            // if the user is not null then we are
            // opening a main activity on below line.
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        } else {

            val txt = findViewById<TextView>(R.id.hello)
            txt.text = "Hello ${user.toString()}"
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
                mAuth.signOut()
                // on below line we are opening our login activity.
                val i = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(i)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


}