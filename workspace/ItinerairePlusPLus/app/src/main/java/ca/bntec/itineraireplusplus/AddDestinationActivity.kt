package ca.bntec.itineraireplusplus

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import classes.AppGlobal
import classes.settings.Settings

import com.google.android.material.textfield.TextInputEditText
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

import androidx.viewpager.widget.ViewPager
import ca.bntec.itineraireplusplus.Fragments.Tab1Fragment
import ca.bntec.itineraireplusplus.Fragments.Tab2Fragment
import ca.bntec.itineraireplusplus.adapter.ViewPagerAdapter
import classes.settings.Energy
import com.google.android.material.tabs.TabLayout
import interfaces.user.IEnergy

class AddDestinationActivity : AppCompatActivity() {

    val appGlobal = AppGlobal.instance
    val db = appGlobal.userManager
    lateinit var inputDepart: TextInputEditText
    lateinit var inputDest: TextInputEditText
    lateinit var btnShowDetination: Button
    lateinit var user: IUser
    private lateinit var pager: ViewPager // creating object of ViewPager
    private lateinit var tab: TabLayout  // creating object of TabLayout

    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_destination)
        context = this
        title = "Nouvelle destination"
        // set the references of the declared objects above
        pager = findViewById(R.id.viewPager)
        tab = findViewById(R.id.tabs)

        // Initializing the ViewPagerAdapter
        val adapter = ViewPagerAdapter(supportFragmentManager)

        // add fragment to the list
        adapter.addFragment(Tab1Fragment(), "vehicules")
        adapter.addFragment(Tab2Fragment(), "activitees")

        // Adding the Adapter to the ViewPager
        pager.adapter = adapter

        // bind the viewPager with the TabLayout.
        tab.setupWithViewPager(pager)

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

    fun tab1EnergyPriceEdit(energie: IEnergy, idx: Int) {
        var item: IEnergy = energie
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_tab1_energy_price)
        val btnCancel = dialog.findViewById<Button>(R.id.btnAnnuler)
        val btnOk = dialog.findViewById<Button>(R.id.btnConfirmer)
        val dialogTitle = dialog.findViewById<TextView>(R.id.tab1DialogTitleTextView)

        val price = dialog.findViewById<TextInputEditText>(R.id.tab1EnergyPrice)
        val unit = dialog.findViewById<TextInputEditText>(R.id.tab1EnergyUnit)

        price.setText(item.price.toString())
        unit.setText(item.unit)

        dialogTitle.setText("Ajouter le prix")
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {

            if (price.text.toString().isEmpty()) {
                price.error = "Entrer le prix"
                return@OnClickListener
            } else {
                price.error = null
            }
            if (unit.text.toString().isEmpty()) {
                unit.error = "Entrer l'unité"
                return@OnClickListener
            } else {
                unit.error = null
            }

            item.price = price.text.toString().toDouble()
            item.unit = unit.text.toString()

            if (idx < 0) {
                user.settings.energies.add(item)
            } else {
                user.settings.energies[idx] = item
            }
    //        isDataChanged = true
    //        showData(user)
            dialog.dismiss()
        })

        dialog.show()
    }
}