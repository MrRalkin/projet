package ca.bntec.itineraireplusplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import classes.AppGlobal
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = AppGlobal.instance.userManager
        lateinit var user: IUser

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        var viewUser=findViewById<View>(R.id.setting_profile_view)
        var viewSettings=findViewById<View>(R.id.setting_settings_view)

        var txtName=findViewById<TextView>(R.id.setting_profile_info_name)
        var txtAddress=findViewById<TextView>(R.id.setting_profile_info_address)
       // var txtSettings=findViewById<TextView>(R.id.setting_profile_settings)


        var btnViewUser = findViewById<Button>(R.id.idBtnShowUserInfo)
        btnViewUser.setOnClickListener { view ->
            toggleVisible(viewUser)
        }
        var btnViewSetting = findViewById<Button>(R.id.idBtnShowSettingInfo)
        btnViewSetting.setOnClickListener { view ->
            toggleVisible(viewSettings)
        }

        MainScope().launch(Dispatchers.IO) {
            user= async {db.userGetCurrent()!!}.await()
            this@SettingsActivity.runOnUiThread(java.lang.Runnable {

                txtName.text=user.name
                txtAddress.text = """
                    Address: ${user.address.address}
                    City:${user.address.city}, State:${user.address.state}
                    Country:${user.address.zip}, Zip:${user.address.state}
                """.trimIndent()

                var vehicles:String=""
                for(item in user.settings.vehicles){
                    vehicles+="Type:${item.type}, Energy:${item.energy}, Distance:${item.distance},\n Mesure: ${item.mesure}, Capacity:${item.capacity}, Unit:${item.unit}\n"
                }
                var energies:String=""
                for(item in user.settings.energies){
                    energies+="Type:${item.type}, Price:${item.price}, Unit:${item.unit}\n"
                }
                var activities:String=""
                for(item in user.settings.activities){
                    activities+="Name:${item.name}, Time:${item.time}\n"
                }

//                txtSettings.text="""
//                Vehicles:
//                ${vehicles}
//                Energies:
//                ${energies}
//                Activities:
//                ${activities}
//                """.trimIndent()

            })
        }

    }

    fun toggleVisible(view:View){
        if (view.visibility === View.VISIBLE) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }


}