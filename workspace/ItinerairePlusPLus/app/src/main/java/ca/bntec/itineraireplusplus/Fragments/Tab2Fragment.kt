package ca.bntec.itineraireplusplus.Fragments


import android.os.Bundle
import ca.bntec.itineraireplusplus.R
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.CheckBox
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat


class Tab2Fragment : Fragment() {

    lateinit var checkBoxManger: CheckBox
    lateinit var checkBoxRecharge: CheckBox
    lateinit var checkBoxEssence: CheckBox
    lateinit var checkBoxDormir: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.activity_tab2_fragment, container, false)

        val pickTimeManger: TextView = view.findViewById(R.id.pickTimeManger) as  TextView
        val pickDurationManger: TextView = view.findViewById(R.id.pickDurationManger) as  TextView

        val pickTimeEssence: TextView = view.findViewById(R.id.pickTimeEssence) as  TextView
        val pickDurationEssence: TextView = view.findViewById(R.id.pickDurationEssence) as  TextView

        val pickTimeRecharge: TextView = view.findViewById(R.id.pickTimeRecharge) as  TextView
        val pickDurationRecharge: TextView = view.findViewById(R.id.pickDurationRecharge) as  TextView


        checkboxStatus(view)

        pickTimeManger.setOnClickListener {
            timeModal(pickTimeManger)
        }
        pickDurationManger.setOnClickListener {
            timeModal(pickDurationManger)
        }
        /*****/
        pickTimeEssence.setOnClickListener {
            timeModal(pickTimeEssence)
        }
        pickDurationEssence.setOnClickListener {
            timeModal(pickDurationEssence)
        }
        /******/
        pickTimeRecharge.setOnClickListener {
            timeModal(pickTimeRecharge)
        }
        pickDurationRecharge.setOnClickListener {
            timeModal(pickDurationRecharge)
        }

        return view
    }

    fun timeModal( pickTimeDuration:TextView){
        // instance of MDC time picker
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            // set the title for the alert dialog
            .setTitleText("SELECTION DU TEMPS")
            // set the default hour for the
            // dialog when the dialog opens
            .setHour(12)
            // set the default minute for the
            // dialog when the dialog opens
            .setMinute(10)
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
            val formattedTime: String = when {
                pickedHour > 12 -> {
                    if (pickedMinute < 10) {
                        "dans ${materialTimePicker.hour} h 0${materialTimePicker.minute}"
                    } else {
                        "dans ${materialTimePicker.hour} h ${materialTimePicker.minute}"
                    }
                }
                pickedHour == 12 -> {
                    if (pickedMinute < 10) {
                        "dans ${materialTimePicker.hour} h 0${materialTimePicker.minute}"
                    } else {
                        "dans ${materialTimePicker.hour} h ${materialTimePicker.minute}"
                    }
                }
                pickedHour == 0 -> {
                    if (pickedMinute < 10) {
                        "dans ${materialTimePicker.hour + 12} h 0${materialTimePicker.minute}"
                    } else {
                        "dans ${materialTimePicker.hour + 12} h ${materialTimePicker.minute}"
                    }
                }
                else -> {
                    if (pickedMinute < 10) {
                        "dans ${materialTimePicker.hour} h 0${materialTimePicker.minute}"
                    } else {
                        "dans ${materialTimePicker.hour} h ${materialTimePicker.minute}"
                    }
                }
            }

            // then update the preview TextView
            pickTimeDuration.text = formattedTime
        }
    }

    fun checkboxStatus(view : View){

        checkBoxManger =  view.findViewById(R.id.checkboxManger)
        if (checkBoxManger.isChecked()) {

        }

        checkBoxEssence =  view.findViewById(R.id.checkboxEssence)
        if (checkBoxEssence.isChecked()) {
            checkBoxRecharge.setChecked(false)
        }

        checkBoxRecharge =  view.findViewById(R.id.checkboxRecharge)
        if (checkBoxRecharge.isChecked()) {
            checkBoxEssence.setChecked(false)
        }

        checkBoxDormir =  view.findViewById(R.id.checkboxDormir)
        if (checkBoxDormir.isChecked()) {

        }
    }
}






