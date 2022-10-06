package ca.bntec.itineraireplusplus

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import ca.bntec.itineraireplusplus.adapter.AdapterSettingsActivities
import ca.bntec.itineraireplusplus.adapter.AdapterSettingsEnergies
import ca.bntec.itineraireplusplus.adapter.AdapterSettingsVehicles
import classes.settings.Activity
import classes.AppGlobal
import classes.settings.Energy
import classes.settings.Vehicle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
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
    lateinit var btnVehiclesMoreLess:Button
    lateinit var btnActivityAdd: Button
    lateinit var btnActivityMoreLess: Button
    lateinit var btnEnergyAdd: Button
    lateinit var btnEnergyMoreLess: Button
    lateinit var btnEditUser: Button

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
        listViewVehicles = findViewById(R.id.setting_vehicles_listview)
        btnVehiclesMoreLess = findViewById(R.id.setting_vehicle_btn_more_less)
        btnVehiclesMoreLess.setOnClickListener { view ->
            setListViewVisibilityAndMoreLessButton(listViewVehicles, btnVehiclesMoreLess)
        }

        btnActivityAdd = findViewById(R.id.setting_activity_btn_add)
        btnActivityAdd.setOnClickListener { view ->
            activityAddEdit(Activity(), -1)
        }
        listViewActivities = findViewById(R.id.setting_activities_listview)
        btnActivityMoreLess = findViewById(R.id.setting_activity_btn_more_less)
        btnActivityMoreLess.setOnClickListener { view ->
            setListViewVisibilityAndMoreLessButton(listViewActivities, btnActivityMoreLess)
        }
        btnEnergyAdd = findViewById(R.id.setting_energy_btn_add)
        btnEnergyAdd.setOnClickListener { view ->
            energyAddEdit(Energy(), -1)
        }
        listViewEnergies = findViewById(R.id.setting_energies_listview)
        btnEnergyMoreLess = findViewById(R.id.setting_energy_btn_more_less)
        btnEnergyMoreLess.setOnClickListener { view ->
            setListViewVisibilityAndMoreLessButton(listViewEnergies, btnEnergyMoreLess)
        }

        btnEditUser = findViewById(R.id.settings_user_edit)
        btnEditUser.setOnClickListener { view ->
            userEdit()
        }

        getUserData()
    }

    private fun setListViewVisibilityAndMoreLessButton(lv : ListView, btn : Button) {
        if (lv.isVisible) {
            btn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.baseline_expand_more_24,0)
            lv.isVisible = false
        } else {
            btn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.baseline_expand_less_24,0)
            lv.isVisible = true
        }
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
            val txtMessage = """
                    Adresse: ${user.address.address}
                    Ville:${user.address.city}, (${user.address.state})
                    Pays:${user.address.country}, Zip:${user.address.zip}
                """.trimIndent()
            txtAddress.text = txtMessage

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
        val zip = dialog.findViewById<EditText>(R.id.setting_edit_user_postalCode)
        val country = dialog.findViewById<EditText>(R.id.setting_edit_user_country)

        name.setText(user.name)
        address.setText(user.address.address)
        city.setText(user.address.city)
        state.setText(user.address.state)
        zip.setText(user.address.zip)
        country.setText(user.address.country)

        dialogTitle.setText("Modification de l'utilisateur")
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            if (name.text.toString().isEmpty()) {
                name.error = "Entrer le nom"
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
        val item: IVehicle = vehicle
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.settings_add_edit_vehicle)
        val btnCancel = dialog.findViewById<Button>(R.id.btnAnnuler)
        val btnOk = dialog.findViewById<Button>(R.id.btnConfirmer)
        val dialogTitle = dialog.findViewById<TextView>(R.id.txt_setting_vehicle_title)

        val type = dialog.findViewById<TextInputEditText>(R.id.setting_edit_vehicle_type)
        val energy = dialog.findViewById<TextInputEditText>(R.id.setting_edit_vehicle_energy)
        val distance = dialog.findViewById<TextInputEditText>(R.id.setting_edit_vehicle_distance)
        val mesure = dialog.findViewById<TextInputEditText>(R.id.setting_edit_vehicle_mesure)
        val capacity = dialog.findViewById<TextInputEditText>(R.id.setting_edit_vehicle_capacity)
        val unit = dialog.findViewById<TextInputEditText>(R.id.setting_edit_vehicle_unit)

        type.setText(item.type)
        energy.setText(item.energy)
        distance.setText(item.distance.toString())
        mesure.setText(item.mesure)
        capacity.setText(item.capacity.toString())
        unit.setText(item.unit)

        dialogTitle.text = "Ajouter un véhicule"
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            if (type.text.toString().isEmpty()) {
                type.error = "Entrer le type"
                return@OnClickListener
            } else {
                type.error = null
            }
            if (energy.text.toString().isEmpty()) {
                energy.error = "Entrer l'énergie"
                return@OnClickListener
            } else {
                energy.error = null
            }
            if (distance.text.toString().isEmpty()) {
                distance.error = "Entrer la distance"
                return@OnClickListener
            } else {
                distance.error = null
            }
            if (mesure.text.toString().isEmpty()) {
                mesure.error = "Entrer la mesure"
                return@OnClickListener
            } else {
                mesure.error = null
            }
            if (capacity.text.toString().isEmpty()) {
                capacity.error = "Entrer la capacité"
                return@OnClickListener
            } else {
                capacity.error = null
            }
            if (unit.text.toString().isEmpty()) {
                unit.error = "Entrer l'unité"
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
            Snackbar.make(context, viewUser, "Can't delete last record", Snackbar.LENGTH_SHORT).show()
            return
        }
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_confirm_layout)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        var propertyValue = dialog.findViewById<EditText>(R.id.propertyValue)

        dialogTitle.setText("Supprimer le vehicule")
        propertyValue.setText(vehicle.type)
        propertyValue.isEnabled = false
        propertyValue.setTextColor(ContextCompat.getColor(context, R.color.black))
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            user.settings.vehicles.remove(vehicle)
            isDataChanged = true
            showData(user)
            dialog.dismiss()
        })
        dialog.show()
    }

    fun energyAddEdit(energie: IEnergy, idx: Int) {
        var item: IEnergy = energie
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.settings_add_edit_energy)
        val btnCancel = dialog.findViewById<Button>(R.id.btnAnnuler)
        val btnOk = dialog.findViewById<Button>(R.id.btnConfirmer)
        val dialogTitle = dialog.findViewById<TextView>(R.id.txt_setting_energy_title)

        val type = dialog.findViewById<TextInputEditText>(R.id.setting_edit_energy_type)
        val price = dialog.findViewById<TextInputEditText>(R.id.setting_edit_energy_price)
        val unit = dialog.findViewById<TextInputEditText>(R.id.setting_edit_energy_unit)

        type.setText(item.type)
        price.setText(item.price.toString())
        unit.setText(item.unit)

        dialogTitle.setText("Ajouter l'énergie")
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            if (type.text.toString().isEmpty()) {
                type.error = "Entrer le type"
                return@OnClickListener
            } else {
                type.error = null
            }
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
            Snackbar.make(context, viewUser, "Can't delete last record", Snackbar.LENGTH_SHORT).show()
            return
        }
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_confirm_layout)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val propertyValue = dialog.findViewById<EditText>(R.id.propertyValue)
        dialogTitle.setText("Supprimer l'énergie")
        propertyValue.setText(energy.type)
        propertyValue.isEnabled = false
        propertyValue.setTextColor(ContextCompat.getColor(context, R.color.black))
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {

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

        val name = dialog.findViewById<TextInputEditText>(R.id.setting_edit_activity_name)
        val time = dialog.findViewById<TextInputEditText>(R.id.setting_edit_activity_time)

        name.setText(item.name)
        time.setText(item.time.toString())

        dialogTitle.setText("Ajouter une activité")
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            if (name.text.toString().isEmpty()) {
                name.error = "Entrer le nom"
                return@OnClickListener
            } else {
                name.error = null
            }
            if (time.text.toString().isEmpty()) {
                time.error = "Entrer le temps"
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

//    private fun askedToDeleteItem(titre : String, texte :String) {
//        val dialog = Dialog(context)
//        dialog.setContentView(R.layout.dialog_confirm_layout)
//        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
//        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
//        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
//        val propertyValue = dialog.findViewById<EditText>(R.id.propertyValue)
//
//        dialogTitle.setText(titre)
//        propertyValue.setText(texte)
//
//        propertyValue.isEnabled = false
//        propertyValue.setTextColor(ContextCompat.getColor(context, R.color.black))
//        btnCancel.setOnClickListener { dialog.dismiss() }
//        dialog.show()
//    }

    fun activityDelete(activity: IActivity) {
        if (user.settings.activities.count() <= 1) {
            Snackbar.make(context, viewUser, "Can't delete last record", Snackbar.LENGTH_SHORT).show()
            return
        }

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_confirm_layout)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val propertyValue = dialog.findViewById<EditText>(R.id.propertyValue)

        dialogTitle.text = "Supprimer une activité"
        propertyValue.setText(activity.name)

        propertyValue.isEnabled = false
        propertyValue.setTextColor(ContextCompat.getColor(context, R.color.black))
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnOk.setOnClickListener(View.OnClickListener {
            user.settings.activities.remove(activity)
            dialog.dismiss()
            isDataChanged = true
            showData(user)
        })
        dialog.show()
    }

    fun toggleVisible(view: View) {
        view.isVisible = !view.isVisible
    }

    fun showMessage(message: String) {
        this@SettingsActivity.runOnUiThread(java.lang.Runnable {
            Snackbar.make(context, viewUser, message, Snackbar.LENGTH_SHORT).show()
        })
    }
}