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
import classes.AppGlobal
import interfaces.user.IActivity


class AdapterSettingsActivities(
    var mCtx: Context,
    var resource: Int,
    var items: ArrayList<IActivity>
) :
    ArrayAdapter<IActivity>(mCtx, resource, items)
{
    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)

        val view: View = layoutInflater.inflate(resource, null)

        val data = items[position]
        val tvSettingActivityName = view.findViewById<TextView>(R.id.tv_setting_activity_name)
        val tvSettingActivityValue = view.findViewById<TextView>(R.id.tv_setting_activity_value)

        val btnEdit = view.findViewById<Button>(R.id.setting_activity_btn_edit)
        val btnDel = view.findViewById<Button>(R.id.setting_activity_btn_delete)

        tvSettingActivityName.text = "${data.name} :"

        var message = "D:${Tools.convertSecondsToTime(data.duration, Tools.FMT_HM_LONG)}"

        if (data.name.equals(AppGlobal.instance.ACTIVITY_ESSENCE))
            message += " - Faire le plein"
        else if(data.name.equals(AppGlobal.instance.ACTIVITY_RECHARGE))
            message += " - Faire la recharge"
        else
            message += " T:${Tools.convertSecondsToTime(data.time, Tools.FMT_HM_LONG)}"

        tvSettingActivityValue.text = message

        btnEdit.setOnClickListener {
            if (context is SettingsActivity) {
                (context as SettingsActivity).activityAddEdit(data,position)
            }
        }
        btnDel.setOnClickListener {
            if (context is SettingsActivity) {
                (context as SettingsActivity).activityDelete(data)
            }
        }
        return view
    }
}