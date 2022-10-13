package ca.bntec.itineraireplusplus

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import ca.bntec.itineraireplusplus.adapter.AdapterDestinations
import classes.AppGlobal
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DestinationsActivity: AppCompatActivity() {
    val db = AppGlobal.instance.userManager
    lateinit var user: IUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destinations)

        title = "Vos Destinations"
        MainScope().launch(Dispatchers.IO) {
            user = async { db.userGetCurrent()!! }.await()

            val lvDestinations = findViewById<ListView>(R.id.lv_destinations)

            val adapterDestinations = AdapterDestinations(this@DestinationsActivity, user.destinations)
            lvDestinations.adapter = adapterDestinations
        }
    }
}