package ca.bntec.itineraireplusplus.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import ca.bntec.itineraireplusplus.MapsActivity
import ca.bntec.itineraireplusplus.R
import classes.AppGlobal
import interfaces.user.IActivity
import interfaces.user.INearPlace
import interfaces.user.IStep

class AdapterDialogDestinations(
    var ctx: MapsActivity,
    private var steps : ArrayList<IStep>
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

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(pos: Int, view: View?, parent : ViewGroup?): View {

        val returnView = inf?.inflate(R.layout.adapter_dialog_destinations, parent, false)

        val tvDestinationName = returnView?.findViewById<TextView>(R.id.tv_dialog_destination_name)

        tvDestinationName!!.text = "${this.steps?.get(pos)?.step}: De ${this.steps?.get(pos)?.start!!.name} A ${this.steps?.get(pos)?.end!!.name}"

        tvDestinationName.setOnClickListener {
            displayNearBy(this.steps?.get(pos)!!.activities!!)
        }

        return returnView!!
    }

    private fun displayNearBy(activities : ArrayList<IActivity>) {
        val appGlobal = AppGlobal.instance
        val dialog = Dialog(ctx)
        dialog.setContentView(R.layout.dialog_nearby_layout)
        val dialogTitle = dialog.findViewById<TextView>(R.id.nearby_title)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        val llActivity1 = dialog.findViewById<LinearLayout>(R.id.ll_activity1)
        val llActivity1Sub = dialog.findViewById<LinearLayout>(R.id.ll_activity1sub)
        val tvActivity1Name = dialog.findViewById<TextView>(R.id.tv_activity1_name)
        val lvActivity1 = dialog.findViewById<ListView>(R.id.lv_activity1)

        val llActivity2 = dialog.findViewById<LinearLayout>(R.id.ll_activity2)
        val llActivity2Sub = dialog.findViewById<LinearLayout>(R.id.ll_activity2sub)
        val tvActivity2Name = dialog.findViewById<TextView>(R.id.tv_activity2_name)
        val lvActivity2 = dialog.findViewById<ListView>(R.id.lv_activity2)

        val llActivity3 = dialog.findViewById<LinearLayout>(R.id.ll_activity3)
        val llActivity3Sub = dialog.findViewById<LinearLayout>(R.id.ll_activity3sub)
        val tvActivity3Name = dialog.findViewById<TextView>(R.id.tv_activity3_name)
        val lvActivity3 = dialog.findViewById<ListView>(R.id.lv_activity3)

        val llActivity4 = dialog.findViewById<LinearLayout>(R.id.ll_activity4)
        val llActivity4Sub = dialog.findViewById<LinearLayout>(R.id.ll_activity4sub)
        val tvActivity4Name = dialog.findViewById<TextView>(R.id.tv_activity4_name)
        val lvActivity4 = dialog.findViewById<ListView>(R.id.lv_activity4)

        dialogTitle.text = "Near By Places"

        for (activity in activities) {
            when (activity.name) {
                appGlobal.ACTIVITY_DORMIR -> {
                    showNearbyActivities(
                        tvActivity1Name,
                        appGlobal.ACTIVITY_DORMIR,
                        llActivity1,
                        llActivity1Sub,
                        lvActivity1,
                        activity.nearPlaces!!)
                }
                appGlobal.ACTIVITY_MANGER -> {
                    showNearbyActivities(
                        tvActivity2Name,
                        appGlobal.ACTIVITY_MANGER,
                        llActivity2,
                        llActivity2Sub,
                        lvActivity2,
                        activity.nearPlaces!!)
                }
                appGlobal.ACTIVITY_ESSENCE -> {
                    showNearbyActivities(
                        tvActivity3Name,
                        appGlobal.ACTIVITY_ESSENCE,
                        llActivity3,
                        llActivity3Sub,
                        lvActivity3,
                        activity.nearPlaces!!)
                }
                appGlobal.ACTIVITY_RECHARGE -> {
                    showNearbyActivities(
                        tvActivity4Name,
                        appGlobal.ACTIVITY_RECHARGE,
                        llActivity4,
                        llActivity4Sub,
                        lvActivity4,
                        activity.nearPlaces!!)
                }
            }
        }

        btnOk.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }

    private fun showNearbyActivities(
        tvName : TextView,
        actName : String,
        ll : LinearLayout,
        llSub : LinearLayout,
        lv : ListView,
        nearPlaces : ArrayList<INearPlace>
    ) {
        tvName.text = actName
        ll.visibility = View.VISIBLE
//        lv.visibility = View.INVISIBLE

        val adapterNearByDialog = AdapterNearByDialog(ctx, nearPlaces)
        lv.adapter = adapterNearByDialog

        tvName.setOnClickListener {
            toggleList(llSub)
            if (llSub.visibility == View.INVISIBLE)
            {
                ll.visibility = View.INVISIBLE
//                ll.visibility = View.VISIBLE
            }
        }
    }

    private fun toggleList(ll : LinearLayout) {
        ll.visibility = if (ll.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }
}