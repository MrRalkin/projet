package ca.bntec.itineraireplusplus.adapter

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.isVisible
import ca.bntec.itineraireplusplus.DestinationsActivity
import ca.bntec.itineraireplusplus.R
import ca.bntec.itineraireplusplus.tools.Tools
import interfaces.user.IDestination
import interfaces.user.IStep

class AdapterDestinations(
    var ctx: DestinationsActivity,
    private var destinations: ArrayList<IDestination>?
) : BaseAdapter() {

    var inf: LayoutInflater? = LayoutInflater.from(ctx)

    override fun getCount(): Int {
        return this.destinations!!.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(pos: Int, view: View?, parent : ViewGroup?): View {

        val returnView = inf?.inflate(R.layout.adapter_destinations, parent, false)

        val tvDestinationName = returnView?.findViewById<TextView>(R.id.tv_destination_name)
        val tvDestinationTime = returnView?.findViewById<TextView>(R.id.tv_destination_time)
        val lvSteps = returnView?.findViewById<ListView>(R.id.lv_steps)

        var btnDestinationToSteps = returnView?.findViewById<Button>(R.id.btn_destination_to_steps)!!
        btnDestinationToSteps.setOnClickListener {
            toggleVisible(lvSteps, btnDestinationToSteps)
            tvDestinationTime?.isVisible = tvDestinationTime?.isVisible != true
        }

        var btnEstimation = returnView?.findViewById<Button>(R.id.btn_estimation)!!
        btnEstimation.setOnClickListener {
            displayEstimation(destinations?.get(pos))
        }

        val adapterSteps = AdapterSteps(ctx, destinations?.get(pos)?.steps)
        lvSteps?.adapter = adapterSteps
        var ada = adapterSteps.getView(0, null, lvSteps)

        ada.measure(0,0)
        var t = ada.measuredHeight

        var lp = lvSteps?.layoutParams

        lp?.height = this.destinations?.get(pos)?.steps!!.size * ada.measuredHeight
        lvSteps?.layoutParams = lp


        tvDestinationName?.text = destinations?.get(pos)?.name
        tvDestinationTime?.text = "Estimation : ${Tools.convertSecondsToTime(destinations?.get(pos)?.trip_time!!, Tools.FMT_HM_LONG)}"

        return returnView
    }

    private fun toggleVisible(view: ListView?, button: Button){
        if (view?.isVisible == true) {
            button.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.baseline_expand_more_24,0)
            view.isVisible = false
        } else {
            button.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.baseline_expand_less_24,0)
            view?.isVisible = true
        }
    }

    private fun displayEstimation(destination : IDestination?) {

        val dialog = Dialog(ctx)
        dialog.setContentView(R.layout.dialog_estimation_layout)
        val dialogTitle = dialog.findViewById<TextView>(R.id.estimation_title)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        val estimationStartDeplacementValue = dialog.findViewById<TextView>(R.id.estimation_deplacement_value)
        val estimationArretValue = dialog.findViewById<TextView>(R.id.estimation_arret_value)
        val estimationTotalValue = dialog.findViewById<TextView>(R.id.estimation_total_value)
        val estimationCoutValue = dialog.findViewById<TextView>(R.id.estimation_cout_value)


        dialogTitle.text = "Estimation ${destination?.name}"

        estimationStartDeplacementValue.text = Tools.convertSecondsToTime(6 * Tools.HOUR + 34 * Tools.MINUTES, Tools.FMT_HM_LONG)
        estimationArretValue.text = Tools.convertSecondsToTime(2 * Tools.HOUR + 10 * Tools.MINUTES, Tools.FMT_HM_LONG)
        estimationTotalValue.text = Tools.convertSecondsToTime(8 * Tools.HOUR + 44 * Tools.MINUTES, Tools.FMT_HM_LONG)
        estimationCoutValue.text = String.format("%.2f $", 739.56)

        btnOk.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }

}