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
import ca.bntec.itineraireplusplus.tools.Tools
import interfaces.user.IActivity


class AdapterSettingsActivities(
    var mCtx: Context,
    var resource: Int,
    var items: ArrayList<IActivity>
) :
    ArrayAdapter<IActivity>(mCtx, resource, items) {


    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)

        val view: View = layoutInflater.inflate(resource, null)

        val data = items[position]
        val txt = view.findViewById<TextView>(R.id.setting_activity_ln)
        val btnEdit = view.findViewById<Button>(R.id.setting_activity_btn_edit)
        val btnDel = view.findViewById<Button>(R.id.setting_activity_btn_delete)

        txt.text = "${data.name} : ${Tools.convertSecondsToTime(data.time, Tools.FMT_HM_LONG)} : ${Tools.convertSecondsToTime(data.duration, Tools.FMT_HM_LONG)}"

        btnEdit.setOnClickListener { view ->
            if (context is SettingsActivity) {
                (context as SettingsActivity).activityAddEdit(data,position)
            }
        }
        btnDel.setOnClickListener { view ->
            if (context is SettingsActivity) {
                (context as SettingsActivity).activityDelete(data)
            }
        }
        return view
    }

}