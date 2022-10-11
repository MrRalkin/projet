package ca.bntec.itineraireplusplus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import ca.bntec.itineraireplusplus.tools.Tools
import classes.AppGlobal
import classes.settings.Settings
import com.google.android.material.textfield.TextInputEditText
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import classes.settings.Activity
import classes.settings.Energy
import classes.settings.Vehicle
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import interfaces.user.IEnergy
import interfaces.user.IVehicle

class AddDestinationActivity : AppCompatActivity() {
    val appGlobal = AppGlobal.instance

    lateinit var context: Context

    lateinit var inputDepart: TextInputEditText
    lateinit var inputDest: TextInputEditText
    lateinit var inputName: TextInputEditText
    lateinit var btnShowDestination: Button
    lateinit var user: IUser
    lateinit var radioButtonElectrique: RadioButton
    lateinit var radioButtonEssence: RadioButton
    lateinit var checkBoxManger: CheckBox
    lateinit var checkBoxRecharge: CheckBox
    lateinit var checkBoxEssence: CheckBox
    lateinit var checkBoxDormir: CheckBox
    lateinit var gazLinearLayout: LinearLayout
    lateinit var rechargeLinearLayout: LinearLayout
    lateinit var pickTimeManger: TextView
    lateinit var pickDurationManger: TextView
    lateinit var pickTimeEssence: TextView
    lateinit var pickDurationEssence: TextView
    lateinit var pickTimeRecharge: TextView
    lateinit var pickDurationRecharge: TextView
    lateinit var pickTimeDormir: TextView
    lateinit var pickDurationDormir: TextView

    private val db = appGlobal.userManager
    var radioGroup: RadioGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_destination)
        context = this
        title = "Nouvelle destination"

        appGlobal.curSetting = Settings()

        // Initialisation des champs texts et du bouton de calcul de destination
        inputDepart = findViewById(R.id.textInputEdit_depart)
        inputDest = findViewById(R.id.textInputEdit_dest)
        inputName = findViewById(R.id.textInputEdit_name)
        btnShowDestination = findViewById(R.id.btnShowDestination)

        radioButtonEssence = findViewById(R.id.radio_button_essence)
        radioButtonElectrique = findViewById(R.id.radio_button_electrique)
        checkBoxManger = findViewById(R.id.checkboxManger)
        checkBoxDormir = findViewById(R.id.checkboxDormir)
        checkBoxRecharge = findViewById(R.id.checkboxRecharge)
        checkBoxEssence = findViewById(R.id.checkboxEssence)
        pickTimeRecharge = findViewById(R.id.pickTimeRecharge)
        pickDurationRecharge = findViewById(R.id.pickDurationRecharge)
        pickTimeEssence = findViewById(R.id.pickTimeEssence)
        pickDurationEssence = findViewById(R.id.pickDurationEssence)
        gazLinearLayout = findViewById(R.id.gazLinearLayout)
        rechargeLinearLayout = findViewById(R.id.rechargeLinearLayout)
        pickTimeManger = findViewById(R.id.pickTimeManger)
        pickDurationManger = findViewById(R.id.pickDurationManger)
        pickTimeDormir = findViewById(R.id.pickTimeDormir)
        pickDurationDormir = findViewById(R.id.pickDurationDormir)

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

                radioButtonStatus()
                afficherActivity()
                remplirBoutons()
                creerListeners()
                createCheckboxListener()

            }
        }

        // Prise en charge des coordonnees d'adresses entrees par l'utilisateur et lancement de l'activite MapsActivity
        btnShowDestination.setOnClickListener() {

            if (inputName.text.toString().isEmpty()) {
                inputName.error = "Entrer le nom"
                return@setOnClickListener
            } else {
                inputName.error = null
            }
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
            appGlobal.departAddress = inputDepart.text.toString()
            appGlobal.destAddress = inputDest.text.toString()
            appGlobal.name = inputName.text.toString()
            val i = Intent(this@AddDestinationActivity, MapsActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    // Savoir le type de vehicule choisi par l'utilisateur et mettre a jour les donnees dans l'Array appGlobal
    private fun radioButtonStatus() {


        val vehucles = user.settings.vehicles

        if (vehucles.any { it.energy == appGlobal.VEHICLE_ESSENCE }) {
            radioButtonEssence.visibility = View.VISIBLE;
//            radioButtonEssence.isChecked = true
//            radioButtonElectrique.isChecked = false
        } else {
//            radioButtonEssence.isChecked = false
            radioButtonEssence.visibility = View.GONE;
        }

        if (vehucles.any { it.energy == appGlobal.ENERGY_ELECTRICITE }) {
            radioButtonElectrique.visibility = View.VISIBLE;
//            radioButtonElectrique.isChecked = true
//            radioButtonEssence.isChecked = false
        } else {
//            radioButtonElectrique.isChecked = false
            radioButtonElectrique.visibility = View.GONE;
        }
        val firstVehicle =
            vehucles.first { it.energy == appGlobal.ENERGY_ELECTRICITE || it.energy == appGlobal.ENERGY_ESSENCE }
        if (firstVehicle.energy == appGlobal.ENERGY_ELECTRICITE) {
            setChecked(R.id.radio_button_electrique)
        } else {
            setChecked(R.id.radio_button_essence)
        }


        radioGroup = findViewById(R.id.radioGroup)
        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            // Responds to child RadioButton checked/unchecked
            setChecked(checkedId)
        }
    }

    private fun setChecked(id: Int) {
        if (id == R.id.radio_button_electrique) {
            radioButtonElectrique.isChecked = true
            checkBoxRecharge.isChecked = true
            radioButtonEssence.isChecked = false
            checkBoxEssence.isChecked = false
            setSetting(true)
        } else {
            radioButtonElectrique.isChecked = false
            checkBoxRecharge.isChecked = false
            radioButtonEssence.isChecked = true
            checkBoxEssence.isChecked = true
            setSetting(false)
        }
    }


    private fun setSetting(isElectric: Boolean) {

        appGlobal.curSetting.vehicles = ArrayList<IVehicle>()
        appGlobal.curSetting.energies = ArrayList<IEnergy>()
        appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_RECHARGE))
        appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_ESSENCE))
        if (isElectric) {
            appGlobal.curSetting.vehicles.add(getVehicle(appGlobal.VEHICLE_ELECTRIQUE))
            appGlobal.curSetting.energies.add(getEnergy(appGlobal.ENERGY_ELECTRICITE))
            appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_RECHARGE))
        } else {
            appGlobal.curSetting.vehicles.add(getVehicle(appGlobal.VEHICLE_ESSENCE))
            appGlobal.curSetting.energies.add(getEnergy(appGlobal.VEHICLE_ESSENCE))
            appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_ESSENCE))
        }


    }


    // Mettre a jour les donnees sur le vehicule dans appGlobal
    private fun getVehicle(energy: String): Vehicle {
        var ve = Vehicle()
        for (vehicle in user.settings.vehicles) {
            if (vehicle.energy.equals(energy)) {
                ve.energy = vehicle.energy
                ve.type = vehicle.type
                ve.unit = vehicle.unit
                ve.capacity = vehicle.capacity
                ve.mesure = vehicle.mesure
                ve.distance = vehicle.distance
            }
        }
        return ve
    }

    // Mettre a jour les donnees sur l'energie dans appGlobal
    private fun getEnergy(energy: String): Energy {
        var en = Energy()
        for (e in user.settings.energies) {
            if (e.type.equals(energy)) {
                en.type = e.type
                en.unit = e.unit
                en.price = e.price
            }
        }
        return en
    }

    // Mettre a jour les donnees sur l'activite dans appGlobal
    private fun getActivity(name: String): Activity {
        var a = Activity()
        for (activity in user.settings.activities) {
            if (activity.name.equals(name)) {
                a.activity = activity.activity
                a.duration = activity.duration
                a.time = activity.time
                a.name = activity.name
                a.nearPlaces = activity.nearPlaces
            }
        }
        return a
    }

    private fun afficherActivity() {
        var isEssence = false
        var isRecharge = false
        for (i in user.settings.vehicles) {
            if (i.energy.equals(appGlobal.VEHICLE_ESSENCE)) {
                isEssence = true
            }
            if (i.energy.equals(appGlobal.VEHICLE_ELECTRIQUE)) {
                isRecharge = true
            }
        }
        if (isEssence) {
            gazLinearLayout.setVisibility(View.VISIBLE);
        }
        if (isRecharge) {
            rechargeLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    private fun findActivity(currentActivity: String): Int {
        var idx: Int = -1
        for (i in 0 until appGlobal.curSetting.activities.size) {
            if (appGlobal.curSetting.activities[i].name.equals(currentActivity)) {
                idx = i
            }
        }
        return idx
    }

    private fun remplirBoutons() {
        var a = getActivity(appGlobal.ACTIVITY_MANGER)
        pickTimeManger.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationManger.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
        a = getActivity(appGlobal.ACTIVITY_ESSENCE)
        pickTimeEssence.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationEssence.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
        a = getActivity(appGlobal.ACTIVITY_RECHARGE)
        pickTimeRecharge.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationRecharge.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
        a = getActivity(appGlobal.ACTIVITY_DORMIR)
        pickTimeDormir.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationDormir.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
    }

    private fun creerListeners() {
        val strTemps = "Temps avant l'arrêt."
        val strDuration = "Durée de l'arrêt."
        pickTimeManger.setOnClickListener {
            if (checkBoxManger.isChecked) {
                timeModal(pickTimeManger, "Manger - $strTemps")
            }
        }
        pickDurationManger.setOnClickListener {
            if (checkBoxManger.isChecked) {
                timeModal(pickDurationManger,"Manger - $strDuration")
            }
        }
        pickTimeEssence.setOnClickListener {
            if (checkBoxEssence.isChecked) {
                timeModal(pickTimeEssence, "Essence - $strTemps")
            }
        }
        pickDurationEssence.setOnClickListener {
            if (checkBoxEssence.isChecked) {
                timeModal(pickDurationEssence,"Essence - $strDuration")
            }
        }
        pickTimeRecharge.setOnClickListener {
            if (checkBoxRecharge.isChecked) {
                timeModal(pickTimeRecharge, "Recharge - $strTemps")
            }
        }
        pickDurationRecharge.setOnClickListener {
            if (checkBoxRecharge.isChecked) {
                timeModal(pickDurationRecharge, "Recharge - $strDuration")
            }
        }
        pickTimeDormir.setOnClickListener {
            if (checkBoxDormir.isChecked) {
                timeModal(pickTimeDormir, "Dormir - $strTemps")
            }
        }
        pickDurationDormir.setOnClickListener {
            if (checkBoxDormir.isChecked) {
                timeModal(pickDurationDormir, "Dormir - $strDuration")
            }
        }
    }

    private fun timeModal(pickTimeDuration: TextView, titre : String) {
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText(titre)
            .setHour(pickTimeDuration.text.subSequence(0, 2).toString().toInt())
            .setMinute(pickTimeDuration.text.subSequence(3, 5).toString().toInt())
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

        materialTimePicker.show(supportFragmentManager, "Tab2Fragment")

        materialTimePicker.addOnPositiveButtonClickListener {

            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute

            val seconds = (pickedHour * 3600) + (pickedMinute * 60)

            pickTimeDuration.text = Tools.convertSecondsToTime(seconds, Tools.FMT_HM_SHORT)

            addToSettings(pickTimeDuration, seconds)
        }
    }

    private fun addToSettings(tv: TextView, seconds: Int) {

        var indice = 0
        when (tv.id) {
            R.id.pickDurationDormir -> {
                indice = findActivity(appGlobal.ACTIVITY_DORMIR)
                appGlobal.curSetting.activities[indice].duration = seconds
            }
            R.id.pickTimeDormir -> {
                indice = findActivity(appGlobal.ACTIVITY_DORMIR)
                appGlobal.curSetting.activities[indice].time = seconds
            }
            R.id.pickTimeManger -> {
                indice = findActivity(appGlobal.ACTIVITY_MANGER)
                appGlobal.curSetting.activities[indice].duration = seconds
            }
            R.id.pickDurationManger -> {
                indice = findActivity(appGlobal.ACTIVITY_MANGER)
                appGlobal.curSetting.activities[indice].time = seconds
            }
            R.id.pickTimeEssence -> {
                indice = findActivity(appGlobal.ACTIVITY_ESSENCE)
                appGlobal.curSetting.activities[indice].duration = seconds
            }
            R.id.pickDurationEssence -> {
                indice = findActivity(appGlobal.ACTIVITY_ESSENCE)
                appGlobal.curSetting.activities[indice].time = seconds
            }
            R.id.pickTimeRecharge -> {
                indice = findActivity(appGlobal.ACTIVITY_RECHARGE)
                appGlobal.curSetting.activities[indice].duration = seconds
            }
            R.id.pickDurationRecharge -> {
                indice = findActivity(appGlobal.ACTIVITY_RECHARGE)
                appGlobal.curSetting.activities[indice].time = seconds
            }
        }
    }

    private fun createCheckboxListener() {
        checkBoxManger.setOnClickListener {
            if (checkBoxManger.isChecked) {
                appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_MANGER))
            } else {
                appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_MANGER))
            }
        }

        checkBoxDormir.setOnClickListener {
            if (checkBoxDormir.isChecked) {
                appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_DORMIR))
            } else {
                appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_DORMIR))
            }
        }
    }
}