package ca.bntec.itineraireplusplus

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import ca.bntec.itineraireplusplus.adapter.AdapterDestinations
import ca.bntec.itineraireplusplus.adapter.AdapterSteps
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

            var lv_destinations = findViewById<ListView>(R.id.lv_destinations)

            val adapterDestinations = AdapterDestinations(this@DestinationsActivity, user.destinations)
            lv_destinations.adapter = adapterDestinations
        }
    }
}