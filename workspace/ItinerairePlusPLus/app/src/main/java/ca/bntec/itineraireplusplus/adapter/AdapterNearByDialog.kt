package ca.bntec.itineraireplusplus.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ca.bntec.itineraireplusplus.DestinationsActivity
import ca.bntec.itineraireplusplus.MapsActivity
import ca.bntec.itineraireplusplus.R
import ca.bntec.itineraireplusplus.tools.Tools
import interfaces.user.IActivity
import interfaces.user.INearPlace

class AdapterNearByDialog(
    var ctx: MapsActivity,
    private var nearPlaces: ArrayList<INearPlace>?
) : BaseAdapter() {

    var inf: LayoutInflater? = LayoutInflater.from(ctx)

    override fun getCount(): Int {
        return this.nearPlaces!!.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(pos: Int, view: View?, parent : ViewGroup?): View {

        val returnView = inf?.inflate(R.layout.adapter_nearby_dialog_layout, parent, false)

        val tvNearbyName = returnView?.findViewById<TextView>(R.id.tv_nearby_name)
        val tvNearbyDistance = returnView?.findViewById<TextView>(R.id.tv_nearby_distance)

        tvNearbyName?.text = nearPlaces?.get(pos)?.name
        tvNearbyDistance?.text = String.format("%d km", nearPlaces?.get(pos)?.distance!! / 1000)

        return returnView!!
    }
}