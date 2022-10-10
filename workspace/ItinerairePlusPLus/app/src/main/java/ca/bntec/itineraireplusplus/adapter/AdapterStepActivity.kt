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

        tvStepActivityName?.text = "${activities?.get(pos)?.name} :"

        val time = Tools.convertSecondsToTime(activities?.get(pos)?.time!!, Tools.FMT_HM_SHORT)
        val duration = Tools.convertSecondsToTime(activities?.get(pos)?.duration!!, Tools.FMT_HM_SHORT)
        tvStepActivityTime?.text = "Temps: $time -- Durée: $duration"

        return returnView!!
    }
}