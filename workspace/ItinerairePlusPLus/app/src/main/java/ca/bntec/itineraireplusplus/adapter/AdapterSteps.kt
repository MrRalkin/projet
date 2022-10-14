package ca.bntec.itineraireplusplus.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ca.bntec.itineraireplusplus.DestinationsActivity
import ca.bntec.itineraireplusplus.R
import ca.bntec.itineraireplusplus.tools.Tools
import classes.AppGlobal
import interfaces.user.IActivity
import interfaces.user.INearPlace
import interfaces.user.IStep

class AdapterSteps(
    var ctx: DestinationsActivity,
    var steps: ArrayList<IStep>?
) : BaseAdapter() {
    var appGlobal = AppGlobal.instance
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
        val btnNearBy = dialog.findViewById<Button>(R.id.btnNearBy)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        val detailStepStart = dialog.findViewById<TextView>(R.id.detail_step_start_value)
        val detailStepEnd = dialog.findViewById<TextView>(R.id.detail_step_end_value)
        val detailStepTripTime = dialog.findViewById<TextView>(R.id.detail_step_trip_time_value)
        val detailStepActivity = dialog.findViewById<ListView>(R.id.detail_step_activity_value)



        dialogTitle.text = "Étape ${step?.step}"
        detailStepStart.text = step?.start?.name
        detailStepEnd.text = step?.end?.name
        detailStepTripTime.text = Tools.convertSecondsToTime(step?.trip_time!!, Tools.FMT_OTHER)

        val adapterStepActivity = AdapterStepActivity(ctx, step.activities)
        detailStepActivity.adapter = adapterStepActivity

        btnNearBy.setOnClickListener{
            displayNearBy(step?.activities!!)
        }

        btnOk.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun displayNearBy(activities : ArrayList<IActivity>) {
        val dialog = Dialog(ctx)
        dialog.setContentView(R.layout.dialog_nearby_layout)
        val dialogTitle = dialog.findViewById<TextView>(R.id.nearby_title)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        val llActivity1 = dialog.findViewById<LinearLayout>(R.id.ll_activity1)
        val tvActivity1Name = dialog.findViewById<TextView>(R.id.tv_activity1_name)
        val lvActivity1 = dialog.findViewById<ListView>(R.id.lv_activity1)

        val llActivity2 = dialog.findViewById<LinearLayout>(R.id.ll_activity2)
        val tvActivity2Name = dialog.findViewById<TextView>(R.id.tv_activity2_name)
        val lvActivity2 = dialog.findViewById<ListView>(R.id.lv_activity2)

        val llActivity3 = dialog.findViewById<LinearLayout>(R.id.ll_activity3)
        val tvActivity3Name = dialog.findViewById<TextView>(R.id.tv_activity3_name)
        val lvActivity3 = dialog.findViewById<ListView>(R.id.lv_activity3)

        val llActivity4 = dialog.findViewById<LinearLayout>(R.id.ll_activity4)
        val tvActivity4Name = dialog.findViewById<TextView>(R.id.tv_activity4_name)
        val lvActivity4 = dialog.findViewById<ListView>(R.id.lv_activity4)

        dialogTitle.text = "Places à proximités"

        for (activity in activities) {
            when (activity.name) {
                appGlobal.ACTIVITY_DORMIR -> {
                    showNearbyActivities(
                        tvActivity1Name,
                        appGlobal.ACTIVITY_DORMIR,
                        llActivity1,
                        lvActivity1,
                        activity.nearPlaces!!)
                }
                appGlobal.ACTIVITY_MANGER -> {
                    showNearbyActivities(
                        tvActivity2Name,
                        appGlobal.ACTIVITY_MANGER,
                        llActivity2,
                        lvActivity2,
                        activity.nearPlaces!!)
                }
                appGlobal.ACTIVITY_ESSENCE -> {
                    showNearbyActivities(
                        tvActivity3Name,
                        appGlobal.ACTIVITY_ESSENCE,
                        llActivity3,
                        lvActivity3,
                        activity.nearPlaces!!)
                }
                appGlobal.ACTIVITY_RECHARGE -> {
                    showNearbyActivities(
                        tvActivity4Name,
                        appGlobal.ACTIVITY_RECHARGE,
                        llActivity4,
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
        lv : ListView,
        nearPlaces : ArrayList<INearPlace>
    ) {
        var list = nearPlaces.sortedBy { it.distance }
        when (actName) {
            appGlobal.ACTIVITY_DORMIR -> tvName.text = "Hotêls"
            appGlobal.ACTIVITY_MANGER -> tvName.text = "Restaurants"
            else -> tvName.text = "Stations services"
        }

        ll.visibility = View.VISIBLE

        val adapterNearByDialog = AdapterNearBy2Dialog(ctx, ArrayList(list))
        lv.adapter = adapterNearByDialog
    }
}
