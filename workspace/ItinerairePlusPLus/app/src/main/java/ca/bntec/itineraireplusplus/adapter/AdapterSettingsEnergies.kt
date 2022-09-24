package ca.bntec.itineraireplusplus.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import ca.bntec.itineraireplusplus.R
import ca.bntec.itineraireplusplus.SettingsActivity
import interfaces.user.IEnergy

class AdapterSettingsEnergies(
    var mCtx: Context,
    var resource: Int,
    var items: ArrayList<IEnergy>
    )
        : ArrayAdapter<IEnergy>(mCtx, resource, items)
    {

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)

        val view: View = layoutInflater.inflate(resource, null)

        val data = items[position]
        val txt = view.findViewById<TextView>(R.id.setting_energy_ln)
        val btnEdit = view.findViewById<Button>(R.id.setting_energy_btn_edit)
        val btnDel = view.findViewById<Button>(R.id.setting_energy_btn_delete)


        val prix =
            if (data.unit.equals("litre")) String.format("%.2f", data.price)
            else String.format("%.3f", data.price)

//        ${String.format("%." + String.format("%df", if (data.unit.equals("litre")) 2 else 3), data.price)}

        txt.text = "${data.type} : $prix $ le ${data.unit}"

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