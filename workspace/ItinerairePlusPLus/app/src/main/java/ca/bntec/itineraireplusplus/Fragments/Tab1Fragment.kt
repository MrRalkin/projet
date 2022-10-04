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
import classes.settings.Energy
import com.google.android.material.textfield.TextInputEditText
import interfaces.user.IEnergy
import interfaces.user.IUser


class Tab1Fragment : Fragment() {
    val appGlobal = AppGlobal.instance
    private lateinit var mContext: Context
    var radioGroup: RadioGroup? = null
    lateinit var radioButton: RadioButton
    lateinit var radioButton2: RadioButton
    lateinit var user: IUser

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.activity_tab1_fragment, container, false)
        val tab1GazPriceAddBtn: Button = view.findViewById(R.id.tab1GazPriceAddBtn) as Button
        val tab1ChargePriceAddBtn: Button = view.findViewById(R.id.tab1ChargePriceAddBtn) as Button

        //user.settings.energies[0].
        radioButtonStatus(view)

        tab1GazPriceAddBtn.setOnClickListener { view ->
            tab1EnergyPriceEdit(Energy(), -1)
        }

        tab1ChargePriceAddBtn.setOnClickListener { view ->
            tab1EnergyPriceEdit(Energy(), -1)
        }

        return view
    }

    fun tab1EnergyPriceEdit(energie: IEnergy, idx: Int) {
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
    }

    fun radioButtonStatus( view: View){
        radioGroup = view.findViewById(R.id.radioGroup)
        radioButton = view.findViewById(R.id.radio_button_1)
        radioButton2 = view.findViewById(R.id.radio_button_2)

        val checkedRadioButtonId =
            radioGroup?.checkedRadioButtonId // Returns View.NO_ID if nothing is checked.
        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            // Responds to child RadioButton checked/unchecked
        }
        // To check a radio button
        radioButton.isChecked = true

        // To listen for a radio button's checked/unchecked state changes
        radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            // Responds to radio button being checked/unchecked
        }
    }
}







