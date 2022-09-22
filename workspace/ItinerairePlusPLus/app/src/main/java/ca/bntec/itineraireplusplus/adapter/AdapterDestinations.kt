package ca.bntec.itineraireplusplus.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import ca.bntec.itineraireplusplus.DestinationsActivity
import ca.bntec.itineraireplusplus.R
import interfaces.user.IDestination

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

        val btnDestinationToSteps = returnView?.findViewById<Button>(R.id.btn_destination_to_steps)!!
        btnDestinationToSteps.setOnClickListener {
            toggleVisible(lvSteps, btnDestinationToSteps)
        }

        val adapterSteps = AdapterSteps(ctx, destinations?.get(pos)?.steps)
        lvSteps?.adapter = adapterSteps

        tvDestinationName?.text = destinations?.get(pos)?.name
        tvDestinationTime?.text = "Date: 2022-mm-jj - Temps du voyage :" + destinations?.get(pos)?.trip_time.toString()

        return returnView
    }

    private fun toggleVisible(view: ListView?, button : Button){
        if (view?.visibility?.equals(View.VISIBLE) == true) {
            button.text = "+"
            view.visibility = View.GONE
        } else {
            button.text = "-"
            view?.visibility = View.VISIBLE
        }
    }
}