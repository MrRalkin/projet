package ca.bntec.itineraireplusplus

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import ca.bntec.itineraireplusplus.adapter.AdapterSettingsActivities
import ca.bntec.itineraireplusplus.adapter.AdapterSettingsEnergies
import ca.bntec.itineraireplusplus.adapter.AdapterSettingsVehicles
import classes.settings.Activity
import classes.AppGlobal
import classes.settings.Energy
import classes.settings.Vehicle
import interfaces.user.IActivity
import interfaces.user.IEnergy
import interfaces.user.IUser
import interfaces.user.IVehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    val db = AppGlobal.instance.userManager
    lateinit var listViewVehicles: ListView
    lateinit var listViewEnergies: ListView
    lateinit var listViewActivities: ListView
    lateinit var user: IUser
    lateinit var context: Context
    lateinit var viewUser: View
    lateinit var viewSettings: View
    lateinit var txtName: TextView
    lateinit var txtAddress: TextView
    lateinit var btnViewUser: Button
    lateinit var btnViewSetting: Button
    lateinit var btnSaveChanges: Button
    lateinit var btnCancelChanges: Button
    lateinit var btnVehicleAdd: Button
    lateinit var btnActivityAdd: Button
    lateinit var btnEnergyAdd: Button
    lateinit var btnEditUser: Button

    lateinit var toast: Toast
    var isDataChanged = false
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        context = this

        viewUser = findViewById<View>(R.id.setting_profile_view)
        viewSettings = findViewById<View>(R.id.setting_settings_view)

        txtName = findViewById<TextView>(R.id.setting_profile_info_name)
        txtAddress = findViewById<TextView>(R.id.setting_profile_info_address)

        btnViewUser = findViewById<Button>(R.id.idBtnShowUserInfo)
        btnViewUser.setOnClickListener { view ->
            toggleVisible(viewUser)
        }
        btnViewSetting = findViewById<Button>(R.id.idBtnShowSettingInfo)
        btnViewSetting.setOnClickListener { view ->
            toggleVisible(viewSettings)
        }

        btnSaveChanges = findViewById(R.id.setting_btn_save_changes)
        btnSaveChanges.setOnClickListener { view ->
            setUserData()
        }

        btnCancelChanges = findViewById(R.id.setting_btn_cancel_changes)
        btnCancelChanges.setOnClickListener { view ->
            getUserData()
        }

        btnVehicleAdd = findViewById(R.id.setting_vehicle_btn_add)
        btnVehicleAdd.setOnClickListener { view ->
            vehicleAddEdit(Vehicle(), -1)
        }
        btnActivityAdd = findViewById(R.id.setting_activity_btn_add)
        btnActivityAdd.setOnClickListener { view ->
            activityAddEdit(Activity(), -1)
        }
        btnEnergyAdd = findViewById(R.id.setting_energy_btn_add)
        btnEnergyAdd.setOnClickListener { view ->
            energyAddEdit(Energy(), -1)
        }

        btnEditUser = findViewById(R.id.settings_user_edit)
        btnEditUser.setOnClickListener { view ->
            userEdit()
        }

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        listViewVehicles = findViewById<ListView>(R.id.setting_vehicles_listview)
        listViewActivities = findViewById<ListView>(R.id.setting_activities_listview)
        listViewEnergies = findViewById<ListView>(R.id.setting_energies_listview)

        getUserData()

    }

    fun setUserData() {
        MainScope().launch(Dispatchers.IO) {
            var result = async { db.userUpdateCurrent(user) }.await()
            if (result.isSuccess) {
                showMessage(result.successMessage)
            } else {
                showMessage(result.errorMessage)
            }
            isDataChanged = false
            getUserData()
        }
    }

    fun getUserData() {

        MainScope().launch(Dispatchers.IO) {
            user = async { db.userGetCurrent()!! }.await()
            isDataChanged = false
            showData(user)
        }
    }

    fun showData(user: IUser) {
        this@SettingsActivity.runOnUiThread(java.lang.Runnable {

            txtName.text = user.name
            txtAddress.text = """
                    Address: ${user.address.address}
                    City:${user.address.city}, State:${user.address.state}
                    Country:${user.address.zip}, Zip:${user.address.state}
                """.trimIndent()

            var vehicles: String = ""
            for (item in user.settings.vehicles) {
                vehicles += "Type:${item.type}, Energy:${item.energy}, Distance:${item.distance},\n Mesure: ${item.mesure}, Capacity:${item.capacity}, Unit:${item.unit}\n"
            }
            var energies: String = ""
            for (item in user.settings.energies) {
                energies += "Type:${item.type}, Price:${item.price}, Unit:${item.unit}\n"
            }
            var activities: String = ""
            for (item in user.settings.activities) {
                activities += "Name:${item.name}, Time:${item.time}\n"
            }
            listVehiclesShow(user.settings.vehicles)
            listEnergiesShow(user.settings.energies)
            listActivitiesShow(user.settings.activities)
            setSaveButtons()

        })
    }

    fun setSaveButtons() {
        if (isDataChanged) {
            btnSaveChanges.visibility = View.VISIBLE
            btnCancelChanges.visibility = View.VISIBLE
        } else {
            btnSaveChanges.visibility = View.GONE
            btnCancelChanges.visibility = View.GONE
        }
    }

    fun listVehiclesShow(items: ArrayList<IVehicle>) {
        this@SettingsActivity.runOnUiThread(java.lang.Runnable {
            listViewVehicles.adapter =
                AdapterSettingsVehicles(context, R.layout.adapter_settings_vehicles, items)
        })
    }

    fun listEnergiesShow(items: ArrayList<IEnergy>) {
        this@SettingsActivity.runOnUiThread(java.lang.Runnable {
            listViewEnergies.adapter =
                AdapterSettingsEnergies(context, R.layout.adapter_settings_energies, items)
        })
    }

    fun listActivitiesShow(items: ArrayList<IActivity>) {
        this@SettingsActivity.runOnUiThread(java.lang.Runnable {
            listViewActivities.adapter =
                AdapterSettingsActivities(context, R.layout.adapter_settings_activities, items)
        })
    }

    fun userEdit() {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.settings_edit_user_info)
        val btnCancel = dialog.findViewById<Button>(R.id.btnAnnuler)
        val btnOk = dialog.findViewById<Button>(R.id.btnConfirmer)
        val dialogTitle = dialog.findViewById<TextView>(R.id.txt_setting_user_title)

        val name = dialog.findViewById<EditText>(R.id.setting_edit_user_name)
        val address = dialog.findViewById<EditText>(R.id.setting_edit_user_address)
        val city = dialog.findViewById<EditText>(R.id.setting_edit_user_city)
        val state = dialog.findViewById<EditText>(R.id.setting_edit_user_state)
        val zip = dialog.findViewById<EditText>(R.id.setting_edit_user_zip)
        val country = dialog.findViewById<EditText>(R.id.setting_edit_user_country)

        name.setText(user.name)
        address.setText(user.address.address)
        city.setText(user.address.city)
        state.setText(user.address.state)
        zip.setText(user.address.zip)
        country.setText(user.address.country)

        dialogTitle.setText("Ajouter un vehucle")
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            if (name.text.toString().isEmpty()) {
                name.error = "Rentre le nome"
                return@OnClickListener
            } else {
                name.error = null
            }

            user.name = name.text.toString()
            user.address.address = address.text.toString()
            user.address.city = city.text.toString()
            user.address.state = state.text.toString()
            user.address.zip = zip.text.toString()
            user.address.country = country.text.toString()
            isDataChanged = true
            showData(user)
            dialog.dismiss()
        })

        dialog.show()
    }


    fun vehicleAddEdit(vehicle: IVehicle, idx: Int) {
        var item: IVehicle = vehicle
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.settings_add_edit_vehicle)
        val btnCancel = dialog.findViewById<Button>(R.id.btnAnnuler)
        val btnOk = dialog.findViewById<Button>(R.id.btnConfirmer)
        val dialogTitle = dialog.findViewById<TextView>(R.id.txt_setting_vehicle_title)

        val type = dialog.findViewById<EditText>(R.id.setting_edit_vehicle_type)
        val energy = dialog.findViewById<EditText>(R.id.setting_edit_vehicle_energy)
        val distance = dialog.findViewById<EditText>(R.id.setting_edit_vehicle_distance)
        val mesure = dialog.findViewById<EditText>(R.id.setting_edit_vehicle_mesure)
        val capacity = dialog.findViewById<EditText>(R.id.setting_edit_vehicle_capacity)
        val unit = dialog.findViewById<EditText>(R.id.setting_edit_vehicle_unit)

        type.setText(item.type)
        energy.setText(item.energy)
        distance.setText(item.distance.toString())
        mesure.setText(item.mesure)
        capacity.setText(item.capacity.toString())
        unit.setText(item.unit)

        dialogTitle.setText("Ajouter un vehucle")
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            if (type.text.toString().isEmpty()) {
                type.error = "Rentre le type"
                return@OnClickListener
            } else {
                type.error = null
            }
            if (energy.text.toString().isEmpty()) {
                energy.error = "Rentre l'energy"
                return@OnClickListener
            } else {
                energy.error = null
            }
            if (distance.text.toString().isEmpty()) {
                distance.error = "Rentre la distance"
                return@OnClickListener
            } else {
                distance.error = null
            }
            if (mesure.text.toString().isEmpty()) {
                mesure.error = "Rentre le mesure"
                return@OnClickListener
            } else {
                mesure.error = null
            }
            if (capacity.text.toString().isEmpty()) {
                capacity.error = "Rentre la capacite"
                return@OnClickListener
            } else {
                capacity.error = null
            }
            if (unit.text.toString().isEmpty()) {
                unit.error = "Rentre l'unit'"
                return@OnClickListener
            } else {
                unit.error = null
            }

            item.type = type.text.toString()
            item.energy = energy.text.toString()
            item.distance = distance.text.toString().toInt()
            item.mesure = mesure.text.toString()
            item.capacity = capacity.text.toString().toInt()
            item.unit = unit.text.toString()
            if (idx < 0) {
                user.settings.vehicles.add(item)
            } else {
                user.settings.vehicles[idx] = item
            }
            isDataChanged = true
            showData(user)
            dialog.dismiss()
        })

        dialog.show()
    }

    fun vehicleDelete(vehicle: IVehicle) {

        if (user.settings.vehicles.count() <= 1) {
            Toast.makeText(context, "Can't delete last record", Toast.LENGTH_SHORT)
            return
        }
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_confirm_layout)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val propertyName = dialog.findViewById<TextView>(R.id.propertyName)
        var propertyValue = dialog.findViewById<EditText>(R.id.propertyValue)

        dialogTitle.setText("Supprimer le vehucle")
        propertyName.setText("Name")
        propertyValue.setText(vehicle.type)
        propertyValue.isEnabled = false
        propertyValue.setTextColor(ContextCompat.getColor(context, R.color.black))
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {

            dialog.dismiss()
            title = "Delete confirm"
            user.settings.vehicles.remove(vehicle)
            showData(user)
            dialog.dismiss()
        })
        isDataChanged = true
        showData(user)
        dialog.show()
    }

    fun energyAddEdit(energie: IEnergy, idx: Int) {
        var item: IEnergy = energie
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.settings_add_edit_energy)
        val btnCancel = dialog.findViewById<Button>(R.id.btnAnnuler)
        val btnOk = dialog.findViewById<Button>(R.id.btnConfirmer)
        val dialogTitle = dialog.findViewById<TextView>(R.id.txt_setting_energy_title)

        val type = dialog.findViewById<EditText>(R.id.setting_edit_energy_type)
        val price = dialog.findViewById<EditText>(R.id.setting_edit_energy_price)
        val unit = dialog.findViewById<EditText>(R.id.setting_edit_energy_unit)

        type.setText(item.type)
        price.setText(item.price.toString())
        unit.setText(item.unit)

        dialogTitle.setText("Ajouter un vehucle")
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            if (type.text.toString().isEmpty()) {
                type.error = "Rentre le type"
                return@OnClickListener
            } else {
                type.error = null
            }
            if (price.text.toString().isEmpty()) {
                price.error = "Rentre le price"
                return@OnClickListener
            } else {
                price.error = null
            }
            if (unit.text.toString().isEmpty()) {
                unit.error = "Rentre l'unit'"
                return@OnClickListener
            } else {
                unit.error = null
            }

            item.type = type.text.toString()
            item.price = price.text.toString().toDouble()
            item.unit = unit.text.toString()

            if (idx < 0) {
                user.settings.energies.add(item)
            } else {
                user.settings.energies[idx] = item
            }
            isDataChanged = true
            showData(user)
            dialog.dismiss()
        })

        dialog.show()
    }

    fun energyDelete(energy: IEnergy) {
        if (user.settings.energies.count() <= 1) {
            Toast.makeText(context, "Can't delete last record", Toast.LENGTH_SHORT)
            return
        }
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_confirm_layout)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val propertyName = dialog.findViewById<TextView>(R.id.propertyName)
        val propertyValue = dialog.findViewById<EditText>(R.id.propertyValue)
        dialogTitle.setText("Supprimer le vehucle")
        propertyName.setText("Name")
        propertyValue.setText(energy.type)
        propertyValue.isEnabled = false
        propertyValue.setTextColor(ContextCompat.getColor(context, R.color.black))
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {

            dialog.dismiss()
            title = "Delete confirm"
            user.settings.energies.remove(energy)
            isDataChanged = true
            showData(user)
            dialog.dismiss()
        })

        dialog.show()
    }

    fun activityAddEdit(activity: IActivity, idx: Int) {
        var item: IActivity = activity
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.settings_add_edit_activity)
        val btnCancel = dialog.findViewById<Button>(R.id.btnAnnuler)
        val btnOk = dialog.findViewById<Button>(R.id.btnConfirmer)
        val dialogTitle = dialog.findViewById<TextView>(R.id.txt_setting_activity_title)

        val name = dialog.findViewById<EditText>(R.id.setting_edit_activity_name)
        val time = dialog.findViewById<EditText>(R.id.setting_edit_activity_time)

        name.setText(item.name)
        time.setText(item.time.toString())

        dialogTitle.setText("Ajouter un vehucle")
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            if (name.text.toString().isEmpty()) {
                name.error = "Rentre le name"
                return@OnClickListener
            } else {
                name.error = null
            }
            if (time.text.toString().isEmpty()) {
                time.error = "Rentre le time"
                return@OnClickListener
            } else {
                time.error = null
            }

            item.activity = 0
            item.name = name.text.toString()
            item.time = time.text.toString().toInt()

            if (idx < 0) {
                user.settings.activities.add(item)
            } else {
                user.settings.activities[idx] = item
            }
            isDataChanged = true
            showData(user)
            dialog.dismiss()
        })

        dialog.show()
    }

    fun activityDelete(activity: IActivity) {
        if (user.settings.activities.count() <= 1) {
            Toast.makeText(context, "Can't delete last record", Toast.LENGTH_SHORT)
            return
        }
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_confirm_layout)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val propertyName = dialog.findViewById<TextView>(R.id.propertyName)
        val propertyValue = dialog.findViewById<EditText>(R.id.propertyValue)
        dialogTitle.setText("Supprimer le vehucle")
        propertyName.setText("Name")
        propertyValue.setText(activity.name)
        propertyValue.isEnabled = false
        propertyValue.setTextColor(ContextCompat.getColor(context, R.color.black))
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            title = "Delete confirm"
            user.settings.activities.remove(activity)
            dialog.dismiss()
        })
        isDataChanged = true
        showData(user)
        dialog.show()
    }

    fun toggleVisible(view: View) {
        if (view.visibility === View.VISIBLE) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }

    fun showMessage(message: String) {
        this@SettingsActivity.runOnUiThread(java.lang.Runnable {
            toast.setText(message)
            toast.show()
        })
    }

}