package ca.bntec.itineraireplusplus.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import ca.bntec.itineraireplusplus.DestinationsActivity
import ca.bntec.itineraireplusplus.R
import ca.bntec.itineraireplusplus.tools.Tools
import classes.settings.Step
import interfaces.user.IStep

class AdapterSteps(
    var ctx: DestinationsActivity,
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

    @SuppressLint("ViewHolder", "SetTextI18n", "PrivateResource")
    override fun getView(pos: Int, view: View?, parent : ViewGroup?): View {

        val returnView = inf?.inflate(R.layout.adapter_step, parent, false)

        val tv_step_name = returnView?.findViewById<TextView>(R.id.tv_step_name)
        val btnStepToDetail = returnView?.findViewById<Button>(R.id.btn_step_to_detail)


        btnStepToDetail?.setOnClickListener { view ->
            displayDetails(steps?.get(pos))
        }


        tv_step_name?.text = "${steps?.get(pos)?.step.toString()}: de ${steps?.get(pos)?.start?.name} à ${steps?.get(pos)?.end?.name}"
        return returnView!!
    }

    private fun displayDetails(step : IStep?) {

        val dialog = Dialog(ctx)
        dialog.setContentView(R.layout.dialog_detail_step_layout)
        val dialogTitle = dialog.findViewById<TextView>(R.id.detail_step_title)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        val detailStepStart = dialog.findViewById<TextView>(R.id.detail_step_start_value)
        val detailStepEnd = dialog.findViewById<TextView>(R.id.detail_step_end_value)
        val detailStepTripTime = dialog.findViewById<TextView>(R.id.detail_step_trip_time_value)
        val detailStepActivity = dialog.findViewById<ListView>(R.id.detail_step_activity_value)

        dialogTitle.text = "Étape ${step?.step}"
        detailStepStart.text = step?.start?.name
        detailStepEnd.text = step?.end?.name
        detailStepTripTime.text = Tools.convertSecondsToTime(step?.trip_time!!, Tools.FMT_HM_LONG)

        val adapterSteps = AdapterStepActivity(ctx, step.activities)
        detailStepActivity.adapter = adapterSteps

        btnOk.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }
}
