package ca.bntec.itineraireplusplus.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ca.bntec.itineraireplusplus.DestinationsActivity
import ca.bntec.itineraireplusplus.R
import ca.bntec.itineraireplusplus.tools.Tools
import classes.AppGlobal
import interfaces.user.IActivity

class AdapterStepActivity(
    var ctx: DestinationsActivity,
    private var activities: ArrayList<IActivity>?
) : BaseAdapter() {

    var inf: LayoutInflater? = LayoutInflater.from(ctx)

    override fun getCount(): Int {
        return this.activities!!.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(pos: Int, view: View?, parent : ViewGroup?): View {

        val returnView = inf?.inflate(R.layout.adapter_step_activity_layout, parent, false)

        val tvStepActivityName = returnView?.findViewById<TextView>(R.id.step_activity_name)
        val tvStepActivityTime = returnView?.findViewById<TextView>(R.id.step_activity_time)

        val activityName = activities?.get(pos)?.name
        tvStepActivityName?.text = "$activityName :"

        val time = Tools.convertSecondsToTime(activities?.get(pos)?.time!!, Tools.FMT_HM_SHORT)
        val duration = Tools.convertSecondsToTime(activities?.get(pos)?.duration!!, Tools.FMT_HM_SHORT)

        when (activityName) {
            AppGlobal.instance.ACTIVITY_RECHARGE ->
                tvStepActivityTime?.text = "Durée: $duration -- Recharge électique"
            AppGlobal.instance.ACTIVITY_ESSENCE ->
                tvStepActivityTime?.text = "Durée: $duration -- Plein d'essence"
            else ->
                tvStepActivityTime?.text = "Durée: $duration -- Temps: $time"
        }

        return returnView!!
    }
}