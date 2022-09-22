package ca.bntec.itineraireplusplus

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import interfaces.user.IEnergy

class AdapterSettingsEnergies(var mCtx: Context, var resource: Int, var items: ArrayList<IEnergy>) :
    ArrayAdapter<IEnergy>(mCtx, resource, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)

        val view: View = layoutInflater.inflate(resource, null)

        val data = items[position]
        val txt = view.findViewById<TextView>(R.id.setting_energy_ln)
        val btnEdit = view.findViewById<Button>(R.id.setting_energy_btn_edit)
        val btnDel = view.findViewById<Button>(R.id.setting_energy_btn_delete)

        txt.text = "${data.type}, ${data.price}, ${data.unit}"

        btnEdit.setOnClickListener { view ->
            if (context is SettingsActivity) {
                (context as SettingsActivity).energyAddEdit(data,position)
            }
        }
        btnDel.setOnClickListener { view ->
            if (context is SettingsActivity) {
                (context as SettingsActivity).energyDelete(data)
            }
        }
        return view
    }

}