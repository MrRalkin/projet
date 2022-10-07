package ca.bntec.itineraireplusplus.Fragments

import android.content.Context
import android.os.Bundle
import ca.bntec.itineraireplusplus.R
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import android.widget.CheckBox
import ca.bntec.itineraireplusplus.tools.Tools
import classes.AppGlobal
import classes.settings.Activity
import classes.settings.Vehicle
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import interfaces.user.IActivity
import interfaces.user.IUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class Tab2Fragment : Fragment() {

    val appGlobal = AppGlobal.instance
    lateinit var user: IUser


    lateinit var checkBoxManger: CheckBox
    lateinit var checkBoxRecharge: CheckBox
    lateinit var checkBoxEssence: CheckBox
    lateinit var checkBoxDormir: CheckBox
    lateinit var gazLinearLayout: LinearLayout
    lateinit var rechargeLinearLayout: LinearLayout
    lateinit var pickTimeManger: TextView
    lateinit var pickDurationManger: TextView
    lateinit var pickTimeEssence: TextView
    lateinit var pickDurationEssence: TextView
    lateinit var pickTimeRecharge: TextView
    lateinit var pickDurationRecharge: TextView
    lateinit var pickTimeDormir: TextView
    lateinit var pickDurationDormir: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.activity_tab2_fragment, container, false)

        checkBoxManger =  view.findViewById(R.id.checkboxManger)
        checkBoxDormir =  view.findViewById(R.id.checkboxDormir)
        checkBoxRecharge =  view.findViewById(R.id.checkboxRecharge)
        checkBoxEssence =  view.findViewById(R.id.checkboxEssence)
        pickTimeRecharge = view.findViewById(R.id.pickTimeRecharge)
        pickDurationRecharge = view.findViewById(R.id.pickDurationRecharge)
        pickTimeEssence = view.findViewById(R.id.pickTimeEssence)
        pickDurationEssence = view.findViewById(R.id.pickDurationEssence)
        gazLinearLayout = view.findViewById(R.id.gazLinearLayout)
        rechargeLinearLayout = view.findViewById(R.id.rechargeLinearLayout)
        pickTimeManger = view.findViewById(R.id.pickTimeManger)
        pickDurationManger = view.findViewById(R.id.pickDurationManger)
        pickTimeDormir = view.findViewById(R.id.pickTimeDormir)
        pickDurationDormir = view.findViewById(R.id.pickDurationDormir)

        var db =appGlobal.userManager

        MainScope().launch(Dispatchers.IO) {
            user = db.userGetCurrent()!!
            afficherActivity()
            remplirBoutons()
            creerListeners()
            createCheckbocListener()
         //   checkboxStatus(view)
        }
        return view
    }

    fun afficherActivity() {
        var isEssence = false
        var isRecharge = false
        for (i in user.settings.vehicles) {
            if (i.energy.equals(appGlobal.VEHICLE_ESSENCE)) {
                isEssence = true
            }
            if (i.energy.equals(appGlobal.VEHICLE_ELECTRIQUE)) {
                isRecharge = true
            }
        }
        if (isEssence) {
            gazLinearLayout.setVisibility(View.VISIBLE);
        }
        if (isRecharge) {
            rechargeLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    fun findActivity(currentActivity: String):Int{
        var idx:Int = 0
        for (i in 0 until appGlobal.curSetting.activities.size) {
            if ( appGlobal.curSetting.activities[i].name.equals(currentActivity)) {
                idx = i
            }
        }
        return idx
    }

    fun remplirBoutons() {
        var a = getActivity(appGlobal.ACTIVITY_MANGER)
        pickTimeManger.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationManger.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
        a = getActivity(appGlobal.ACTIVITY_ESSENCE)
        pickTimeEssence.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationEssence.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
        a = getActivity(appGlobal.ACTIVITY_RECHARGE)
        pickTimeRecharge.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationRecharge.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
        a = getActivity(appGlobal.ACTIVITY_DORMIR)
        pickTimeDormir.text = Tools.convertSecondsToTime(a.time, Tools.FMT_HM_SHORT)
        pickDurationDormir.text = Tools.convertSecondsToTime(a.duration, Tools.FMT_HM_SHORT)
    }

    fun creerListeners(){
        pickTimeManger.setOnClickListener {
            if(checkBoxManger.isChecked()) {
                timeModal(pickTimeManger)
                var indice = findActivity(appGlobal.ACTIVITY_MANGER)
                appGlobal.curSetting.activities[indice].time =
                    convertirStrToSec(pickTimeManger.text.toString())
            }
        }

        pickDurationManger.setOnClickListener {
            if (checkBoxManger.isChecked()) {
                timeModal(pickDurationManger)
                var indice = findActivity(appGlobal.ACTIVITY_MANGER)
                appGlobal.curSetting.activities[indice].duration =
                    convertirStrToSec(pickTimeManger.text.toString())
            }
        }
        pickTimeEssence.setOnClickListener {
            if (checkBoxEssence.isChecked()) {
                timeModal(pickTimeEssence)
                var indice = findActivity(appGlobal.ACTIVITY_ESSENCE)
                appGlobal.curSetting.activities[indice].time =
                    convertirStrToSec(pickTimeEssence.text.toString())
            }
        }
        pickDurationEssence.setOnClickListener {
            if (checkBoxEssence.isChecked()) {
                timeModal(pickDurationEssence)
                var indice = findActivity(appGlobal.ACTIVITY_ESSENCE)
                appGlobal.curSetting.activities[indice].duration =
                    convertirStrToSec(pickTimeEssence.text.toString())
            }
        }
        pickTimeRecharge.setOnClickListener {
            if (checkBoxRecharge.isChecked()) {
                timeModal(pickTimeRecharge)
                var indice = findActivity(appGlobal.ACTIVITY_RECHARGE)
                appGlobal.curSetting.activities[indice].time =
                    convertirStrToSec(pickTimeRecharge.text.toString())
            }
        }
        pickDurationRecharge.setOnClickListener {
            if (checkBoxRecharge.isChecked()) {
                timeModal(pickDurationRecharge)
                var indice = findActivity(appGlobal.ACTIVITY_RECHARGE)
                appGlobal.curSetting.activities[indice].duration =
                    convertirStrToSec(pickDurationRecharge.text.toString())
            }
        }
        pickTimeDormir.setOnClickListener {
            if (checkBoxDormir.isChecked()) {
                timeModal(pickTimeDormir)
                var indice = findActivity(appGlobal.ACTIVITY_DORMIR)
                appGlobal.curSetting.activities[indice].time =
                    convertirStrToSec(pickTimeDormir.text.toString())
            }
        }
        pickDurationDormir.setOnClickListener {
            if (checkBoxDormir.isChecked()) {
                timeModal(pickDurationDormir)
                var indice = findActivity(appGlobal.ACTIVITY_DORMIR)
                appGlobal.curSetting.activities[indice].duration =
                    convertirStrToSec(pickDurationDormir.text.toString())
            }
        }
    }

    fun timeModal( pickTimeDuration:TextView){
        // instance of MDC time picker
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            // set the title for the alert dialog
            .setTitleText("SELECTION DU TEMPS")
            // set the default hour for the
            // dialog when the dialog opens
            .setHour(pickTimeDuration.text.subSequence(0,2).toString().toInt())
            // set the default minute for the
            // dialog when the dialog opens
            .setMinute(pickTimeDuration.text.subSequence(3,4).toString().toInt())
            // set the time format
            // according to the region
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

        materialTimePicker.show(childFragmentManager, "Tab2Fragment")

        // on clicking the positive button of the time picker
        // dialog update the TextView accordingly
        materialTimePicker.addOnPositiveButtonClickListener {

            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute

            // check for single digit hour hour and minute
            // and update TextView accordingly
            val formattedTime: String = if(pickedHour >= 10) {

                if (pickedMinute >= 10) {
                        "${materialTimePicker.hour}:${materialTimePicker.minute}"
                } else {
                        "${materialTimePicker.hour}:0${materialTimePicker.minute}"
                }
            } else if(pickedMinute >= 10){

                        "0${materialTimePicker.hour}:${materialTimePicker.minute}"
            } else {
                        "0${materialTimePicker.hour}:0${materialTimePicker.minute}"
            }

            // then update the preview TextView
            pickTimeDuration.text = formattedTime
        }
    }

    fun createCheckbocListener() {
        checkBoxManger.setOnClickListener(){  View.OnClickListener {
            if (checkBoxManger.isChecked){
                appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_MANGER))
            } else{
                appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_MANGER))
            }
        }

        }
        checkBoxDormir.setOnClickListener(){  View.OnClickListener {
            if (checkBoxDormir.isChecked){
                appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_DORMIR))
            } else{
                appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_DORMIR))
            }
        }

        }
    }
    fun onCheckBoxClicked(view : View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkboxEssence ->{
                    if (checked) {
                        appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_ESSENCE))
                    } else{
                        appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_ESSENCE))
                    }
                }
                R.id.checkboxRecharge ->{
                    if (checked) {
                        appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_RECHARGE))
                    } else{
                        appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_RECHARGE))
                    }
                }
                R.id.checkboxDormir ->{
                    if (checked) {
                        appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_DORMIR))
                    } else{
                        appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_DORMIR))
                    }
                }
                R.id.checkboxManger ->{
                    if (checked) {
                        appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_MANGER))
                    } else{
                        appGlobal.curSetting.activities.remove(getActivity(appGlobal.ACTIVITY_MANGER))
                    }
                }
            }
        }
    }

//    fun checkboxStatus(view : View){
//
//        checkBoxEssence =  view.findViewById(R.id.checkboxEssence)
//        if (checkBoxEssence.isChecked()) {
//            appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_ESSENCE))
//        }
//
//        checkBoxRecharge =  view.findViewById(R.id.checkboxRecharge)
//        if (checkBoxRecharge.isChecked()) {
//            appGlobal.curSetting.activities.add(getActivity(appGlobal.ACTIVITY_RECHARGE))
//        }
//
//    }

    fun getActivity(name:String) : Activity {

        var a = Activity()
        for (activity in user.settings.activities) {
            if (activity.name.equals(name)) {

                a.activity = activity.activity
                a.duration = activity.duration
                a.time = activity.time
                a.name = activity.name
            }
        }
        return a
    }

    fun convertirStrToSec(strToConvert : String) : Int {
        var h = strToConvert.subSequence(0,2).toString().toInt()
        var m = strToConvert.subSequence(3,4).toString().toInt()
        return (h*3600)+(m*60)
    }
}







