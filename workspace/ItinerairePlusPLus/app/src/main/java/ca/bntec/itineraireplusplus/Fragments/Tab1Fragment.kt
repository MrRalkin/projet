package ca.bntec.itineraireplusplus.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ca.bntec.itineraireplusplus.R
import classes.AppGlobal
import classes.settings.Activity
import classes.settings.Energy
import classes.settings.Vehicle
import com.google.android.material.textfield.TextInputEditText
import interfaces.user.IEnergy
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class Tab1Fragment : Fragment() {
    val appGlobal = AppGlobal.instance
    private lateinit var mContext: Context
    var radioGroup: RadioGroup? = null
    lateinit var radioButtonElectrique: RadioButton
    lateinit var radioButtonEssence: RadioButton
    lateinit var user: IUser
    lateinit var setRadioVisibility: RadioButton

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.activity_tab1_fragment, container, false)
       // val tab1GazPriceAddBtn: Button = view.findViewById(R.id.tab1GazPriceAddBtn) as Button
        //val tab1ChargePriceAddBtn: Button = view.findViewById(R.id.tab1ChargePriceAddBtn) as Button
        var db =appGlobal.userManager
        MainScope().launch(Dispatchers.IO) {
            user = db.userGetCurrent()!!

            for (i in user.settings.vehicles) {
                if (i.energy.equals(appGlobal.VEHICLE_ESSENCE)) {
                    setRadioVisibility = view.findViewById(R.id.radio_button_essence)
                    setRadioVisibility.setVisibility(View.VISIBLE);
                } else {
                    setRadioVisibility = view.findViewById(R.id.radio_button_electrique)
                    setRadioVisibility.setVisibility(View.VISIBLE);
                }
            }
        }
        //user.settings.energies[0].
      //  radioButtonStatus(view)

       /* tab1GazPriceAddBtn.setOnClickListener { view ->
            tab1EnergyPriceEdit(Energy(), -1)
        }

        tab1ChargePriceAddBtn.setOnClickListener { view ->
            tab1EnergyPriceEdit(Energy(), -1)
        }*/

        return view
    }

   /* fun tab1EnergyPriceEdit(energie: IEnergy, idx: Int) {
        var item: IEnergy = energie
        val dialog = Dialog(mContext)
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

            if(appGlobal.curSetting.energies.size ==0){
                appGlobal.curSetting.energies.add(item)
            }else{
                appGlobal.curSetting.energies[idx] = item
            }
                dialog.dismiss()
        })
        dialog.show()
    }*/
//
    fun radioButtonStatus( view: View){
        radioGroup = view.findViewById(R.id.radioGroup)

        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            // Responds to child RadioButton checked/unchecked
            if (checkedId == R.id.radio_button_electrique) {
                appGlobal.curSetting.vehicles.add(getVehicle(appGlobal.VEHICLE_ELECTRIQUE))
                appGlobal.curSetting.energies.add(getEnergy(appGlobal.ENERGY_ELECTRICITE))
                appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_RECHARGE))
            }

            if (checkedId == R.id.radio_button_essence) {
                appGlobal.curSetting.vehicles.add(getVehicle(appGlobal.VEHICLE_ESSENCE))
                appGlobal.curSetting.energies.add(getEnergy(appGlobal.ENERGY_ESSENCE))
                appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_ESSENCE))
            }
        }
    }

    fun getVehicle(energy:String) : Vehicle {
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

    fun getEnergy(energy:String) : Energy {
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

    fun getActivity(name:String) : Activity {
        var a = Activity()
        for (activity in user.settings.activities) {
            if (activity.name.equals(name)) {
                a.activity = activity.activity
                a.duration = activity.duration
                a.time = activity.time
                a.name = activity.name
            }
        }
        return a
    }
}







