package ca.bntec.itineraireplusplus.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ca.bntec.itineraireplusplus.DestinationsActivity
import ca.bntec.itineraireplusplus.R
import interfaces.user.IStep

class AdapterSteps(
    ctx: DestinationsActivity,
    var steps: ArrayList<IStep>?
) : BaseAdapter() {

    var inf: LayoutInflater? = LayoutInflater.from(ctx)


    override fun getCount(): Int {
        return this.steps!!.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(pos: Int, view: View?, parent : ViewGroup?): View {

        val returnView = inf?.inflate(R.layout.adapter_step, parent, false)

        val tv_step_name = returnView?.findViewById<TextView>(R.id.tv_step_name)

        tv_step_name?.text = "${steps?.get(pos)?.step.toString()}:${steps?.get(pos)?.start?.name} --> ${steps?.get(pos)?.end?.name}"

        return returnView!!
    }
}