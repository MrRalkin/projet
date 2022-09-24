package ca.bntec.itineraireplusplus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import ca.bntec.itineraireplusplus.R
import ca.bntec.itineraireplusplus.SettingsActivity
import interfaces.user.IVehicle

class AdapterSettingsVehicles(
    var mCtx: Context,
    var resource: Int,
    var items: ArrayList<IVehicle>
) :
    ArrayAdapter<IVehicle>(mCtx, resource, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)

        val view: View = layoutInflater.inflate(resource, null)

        val data = items[position]
        val txt = view.findViewById<TextView>(R.id.setting_vehicle_ln)
        val btnEdit = view.findViewById<Button>(R.id.setting_vehicle_btn_edit)
        val btnDel = view.findViewById<Button>(R.id.setting_vehicle_btn_delete)

        txt.text = "${data.type} : (${data.energy}) ${data.capacity} ${data.unit} pour ${data.distance} ${data.mesure}"

        btnEdit.setOnClickListener { view ->
            if (context is SettingsActivity) {
                (context as SettingsActivity).vehicleAddEdit(data,position)
            }
        }
        btnDel.setOnClickListener { view ->
            if (context is SettingsActivity) {
                (context as SettingsActivity).vehicleDelete(data)
            }
        }
        return view
    }

}