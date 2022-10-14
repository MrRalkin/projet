package ca.bntec.itineraireplusplus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import ca.bntec.itineraireplusplus.tools.Tools
import classes.AppGlobal
import classes.settings.*
import com.google.android.material.textfield.TextInputEditText
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import interfaces.user.IActivity
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

    //    lateinit var radioButtonElectrique: RadioButton
//    lateinit var radioButtonEssence: RadioButton
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
    lateinit var spinner: Spinner

    private val db = appGlobal.userManager
    var vehicle: IVehicle = Vehicle()
    var radioGroup: RadioGroup? = null
    var isElectric: Boolean = false
    var dDormir: Int = 0
    var tDormir: Int = 0
    var dManger: Int = 0
    var tManger: Int = 0
    var tEssence: Int = 0
    var dEssence: Int = 0
    var dRecharge: Int = 0
    var tRecharge: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_destination)
        context = this
        title = "Nouvelle destination"

        appGlobal.curSetting = Settings()
        appGlobal.name = ""
        appGlobal.curDestination = Destination()
        appGlobal.destAddress = ""
        appGlobal.departAddress = ""
        appGlobal.isCurDestinationSaved = false

        // Initialisation des champs texts et du bouton de calcul de destination
        inputDepart = findViewById(R.id.textInputEdit_depart)
        inputDest = findViewById(R.id.textInputEdit_dest)
        inputName = findViewById(R.id.textInputEdit_name)
        btnShowDestination = findViewById(R.id.btnShowDestination)

//        radioButtonEssence = findViewById(R.id.radio_button_essence)
//        radioButtonElectrique = findViewById(R.id.radio_button_electrique)
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
        spinner = findViewById(R.id.spn_vehicles)
        creerListeners()
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
                this@AddDestinationActivity.runOnUiThread(java.lang.Runnable {
                    vehicle = user.settings.vehicles.first()
                    setSpinner()
                    setChecked()
                    afficherActivity()
                    createCheckboxListener()
                    remplirBoutons()
                })
                //  radioButtonStatus()
            }
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //setSetting()
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                vehicle =
                    user.settings.vehicles.filter { it.type == spinner.selectedItem.toString() }
                        .first()
                setChecked()
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

    fun setSpinner() {

        val vehicles: ArrayList<IVehicle> = user.settings.vehicles
        var list: ArrayList<String> = ArrayList<String>()

        if (vehicles.size > 0) {
            for (v in vehicles) {
                list.add(v.type)
            }
            var dataAdapter: ArrayAdapter<String> =
                ArrayAdapter(context, R.layout.main_spinner_style, list)
            dataAdapter.setDropDownViewResource(R.layout.main_spinner_style);
            spinner.setAdapter(dataAdapter);
        }
//        if (vehicle.type.isEmpty()) {
//            vehicle = vehicles.first()
//        }
    }

    // Savoir le type de vehicule choisi par l'utilisateur et mettre a jour les donnees dans l'Array appGlobal
    private fun radioButtonStatus() {

//        val vehicles = user.settings.vehicles
//
//        if (vehicles.any { it.energy == appGlobal.VEHICLE_ESSENCE }) {
//            radioButtonEssence.visibility = View.VISIBLE;
//        } else {
//            radioButtonEssence.visibility = View.INVISIBLE;
//        }
//
//        if (vehicles.any { it.energy == appGlobal.ENERGY_ELECTRICITE }) {
//            radioButtonElectrique.visibility = View.VISIBLE;
//        } else {
//            radioButtonElectrique.visibility = View.INVISIBLE;
//        }
//
//        val firstVehicle =
//            vehicles.first { it.energy == appGlobal.ENERGY_ELECTRICITE || it.energy == appGlobal.ENERGY_ESSENCE }

//        if (vehicle.energy == appGlobal.ENERGY_ELECTRICITE) {
//            setChecked(R.id.radio_button_electrique)
//        } else {
//            setChecked(R.id.radio_button_essence)
//        }
//
//        radioGroup = findViewById(R.id.radioGroup)
//        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
//            // Responds to child RadioButton checked/unchecked
//            setChecked(checkedId)
//        }
    }

    private fun setChecked() {
        if (vehicle.energy == appGlobal.ENERGY_ELECTRICITE) {
            //radioButtonElectrique.isChecked = true
            checkBoxRecharge.isChecked = true
            // radioButtonEssence.isChecked = false
            checkBoxEssence.isChecked = false
            isElectric = true
            setSetting()
        } else {
            // radioButtonElectrique.isChecked = false
            checkBoxRecharge.isChecked = false
            //radioButtonEssence.isChecked = true
            checkBoxEssence.isChecked = true
            isElectric = false
            setSetting()
        }
    }

//    private fun setChecked(id: Int) {
//        if (id == R.id.radio_button_electrique) {
//            radioButtonElectrique.isChecked = true
//            checkBoxRecharge.isChecked = true
//            radioButtonEssence.isChecked = false
//            checkBoxEssence.isChecked = false
//            isElectric = true
//            setSetting()
//        } else {
//            radioButtonElectrique.isChecked = false
//            checkBoxRecharge.isChecked = false
//            radioButtonEssence.isChecked = true
//            checkBoxEssence.isChecked = true
//            isElectric = false
//            setSetting()
//        }
//    }


    private fun setSetting() {

        appGlobal.curSetting.vehicles = ArrayList<IVehicle>()
        appGlobal.curSetting.energies = ArrayList<IEnergy>()
        appGlobal.curSetting.activities = ArrayList<IActivity>()
        if (isElectric) {
            appGlobal.curSetting.vehicles.add(getVehicle(appGlobal.VEHICLE_ELECTRIQUE))
            appGlobal.curSetting.energies.add(getEnergy(appGlobal.ENERGY_ELECTRICITE))
            var activity = getActivity(appGlobal.ACTIVITY_RECHARGE)
            activity.duration = dRecharge
            activity.time = tRecharge
            appGlobal.curSetting.activities.add(activity)
        } else {
            appGlobal.curSetting.vehicles.add(getVehicle(appGlobal.VEHICLE_ESSENCE))
            appGlobal.curSetting.energies.add(getEnergy(appGlobal.VEHICLE_ESSENCE))
            var activity = getActivity(appGlobal.ACTIVITY_ESSENCE)
            activity.duration = dEssence
            activity.time = tEssence
            appGlobal.curSetting.activities.add(activity)

        }
        if (checkBoxManger.isChecked) {
            var activity = getActivity(appGlobal.ACTIVITY_MANGER)
            activity.duration = dManger
            activity.time = tManger
            appGlobal.curSetting.activities.add(activity)
        }
        if (checkBoxDormir.isChecked) {
            var activity = getActivity(appGlobal.ACTIVITY_DORMIR)
            activity.duration = dDormir
            activity.time = tDormir

            appGlobal.curSetting.activities.add(activity)
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
        tManger = a.time
        dManger = a.duration
        pickTimeManger.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationManger.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
        a = getActivity(appGlobal.ACTIVITY_ESSENCE)
        tEssence = a.time
        dEssence = a.duration
        pickTimeEssence.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationEssence.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
        a = getActivity(appGlobal.ACTIVITY_RECHARGE)
        tRecharge = a.time
        dRecharge = a.duration
        pickTimeRecharge.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationRecharge.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
        a = getActivity(appGlobal.ACTIVITY_DORMIR)
        tDormir = a.time
        dDormir = a.duration
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
                timeModal(pickDurationManger, "Manger - $strDuration")
            }
        }
        pickTimeEssence.setOnClickListener {
            if (checkBoxEssence.isChecked) {
                timeModal(pickTimeEssence, "Essence - $strTemps")
            }
        }
        pickDurationEssence.setOnClickListener {
            if (checkBoxEssence.isChecked) {
                timeModal(pickDurationEssence, "Essence - $strDuration")
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

    private fun timeModal(pickTimeDuration: TextView, titre: String) {
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText(titre)
            .setHour(pickTimeDuration.text.subSequence(0, 2).toString().toInt())
            .setMinute(pickTimeDuration.text.subSequence(3, 5).toString().toInt())
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

        materialTimePicker.show(supportFragmentManager, "SSNTeam")

        materialTimePicker.addOnPositiveButtonClickListener {

            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute

            val seconds = (pickedHour * 3600) + (pickedMinute * 60)

            pickTimeDuration.text = Tools.convertSecondsToTime(seconds, Tools.FMT_HM_SHORT)

            addToSettings(pickTimeDuration, seconds)
        }
    }


    private fun addToSettings(tv: TextView, seconds: Int) {

        when (tv.id) {
            R.id.pickDurationDormir -> {
                dDormir = seconds
            }
            R.id.pickTimeDormir -> {
                tDormir = seconds
            }
            R.id.pickTimeManger -> {
                tManger = seconds
            }
            R.id.pickDurationManger -> {
                dManger = seconds
            }
            R.id.pickTimeEssence -> {
                tEssence = seconds
            }
            R.id.pickDurationEssence -> {
                dEssence = seconds
            }
            R.id.pickTimeRecharge -> {
                tRecharge = seconds
            }
            R.id.pickDurationRecharge -> {
                dRecharge = seconds
            }
        }
        setSetting()
    }

    private fun createCheckboxListener() {

        checkBoxManger.setOnClickListener {
            setSetting()
        }

        checkBoxDormir.setOnClickListener {
            setSetting()
        }
    }
}