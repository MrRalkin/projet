package ca.bntec.itineraireplusplus

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ca.bntec.itineraireplusplus.adapter.AdapterDialogDestinations
import ca.bntec.itineraireplusplus.databinding.ActivityMapsBinding
import ca.bntec.itineraireplusplus.tools.CreateSteps
import classes.ActionResult
import classes.AppGlobal
import classes.StepActivities
import classes.map.MapData
import classes.map.MapLegData
import classes.map.MapRawData
import classes.map.NearPlace
import classes.settings.Address
import classes.settings.Coord
import classes.settings.Destination
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import interfaces.user.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mapFragment: SupportMapFragment
    lateinit var progressDialog: ProgressDialog
    lateinit var destination: IDestination
    lateinit var mapRawData: IMapRawData
    lateinit var mapLegData: MapLegData
    var activityPlaces = ArrayList<INearPlace>()
    val MAX_ACTIVITY = 3
    val mapData = MapData()
    val appGlobal = AppGlobal.instance
    val db = appGlobal.userManager
    val uid: String = UUID.randomUUID().toString()
    var activitiesList: ArrayList<StepActivities> = ArrayList<StepActivities>()

    //    var origin = LatLng(45.5419056, -73.4924797)
//    var dest = LatLng(25.8102247,-80.2101818)
    var origin = LatLng(34.1993851, -79.8373477)
    var dest = LatLng(30.3321579, -81.6736059)

    //var dest = LatLng(45.4779697, -75.5184535)
    lateinit var context: Context
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        context = this

        destination = getDestination(appGlobal.departAddress, appGlobal.destAddress)
        origin = LatLng(
            destination.coordDepart!!.latitude.toDouble(),
            destination.coordDepart!!.longitude.toDouble()
        )
        dest = LatLng(
            destination.coordDestination!!.latitude.toDouble(),
            destination.coordDestination!!.longitude.toDouble()
        )
        // val list: List<String> = listOf("restaurant", "hotel", "gas_station")
        //   val list: List<String> = listOf("restaurant", "hotel", "gas_station")
        //val list: List<String> = listOf("gas_station")

        drawPolylines()
        //  getActivityPlaces()
        //  getPlacesAwaitAll()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true

        googleMap.addMarker(
            MarkerOptions().position(origin).title("LinkedIn")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        googleMap.addMarker(
            MarkerOptions().position(dest)
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 15f))
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"
        val mode = "mode=driving"

        val app = context.packageManager.getApplicationInfo(
            context.packageName, PackageManager.GET_META_DATA
        )
        val bundle = app.metaData

        var metaKey = bundle.getString("com.google.android.geo.API_KEY")

        val key = "key=$metaKey"

        val parameters = "$str_origin&$str_dest&$sensor&$mode&$key"

        val output = "json"

        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    private fun drawPolylines() {
        progressDialog = ProgressDialog(this@MapsActivity)
        progressDialog.setMessage("Please Wait, Polyline between two locations is building.")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Checks, whether start and end locations are captured
        // Getting URL to the Google Directions API
        val url: String = getDirectionsUrl(origin, dest)
        Log.d("url", url + "")
        //val downloadTask = DownloadTask()
        // Start downloading json data from Google Directions API
        //downloadTask.execute(url)
        getData(url)
    }

    private fun getData(url: String) {
        var data = ""
        var jObject: JSONObject
        var routes = ArrayList<ArrayList<HashMap<String, String>>>()

        MainScope().launch(Dispatchers.IO) {
            data = async { mapData.downloadUrl(url) }.await()

            var date = LocalDate.now()
            var l: String = date.year.toString().padStart(4, '0')
            l += date.monthValue.toString().padStart(2, '0')
            l += date.dayOfMonth.toString().padStart(2, '0')

            mapRawData = MapRawData(uid, l.toLong(), data)
            db.setMapRawData(mapRawData)
            jObject = JSONObject(data)

            routes = mapData.parse(jObject)
            ///code for Nicol
            mapLegData = mapData.getMapLegData()

            Log.d("result", routes.toString())
            var points = ArrayList<LatLng>()
            var lineOptions: PolylineOptions? = null

            for (i in routes.indices) {

                lineOptions = PolylineOptions()
                val path: List<java.util.HashMap<String, String>> = routes.get(i)
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                lineOptions.addAll(points)
                lineOptions.width(12f)
                lineOptions.color(Color.RED)
                lineOptions.geodesic(true)
            }

// Drawing polyline in the Google Map for the i-th route

// Drawing polyline in the Google Map for the i-th route
            this@MapsActivity.runOnUiThread(java.lang.Runnable {
                setActivities()
                mMap.addPolyline(lineOptions!!)

            })

        }
    }

    private fun setActivities() {

        var dest = appGlobal.curDestination
        dest.settings = appGlobal.curSetting

        appGlobal.curDestination = CreateSteps.createSteps(dest, mapLegData)

        getPlacesAwaitAll()
    }


    private fun getDestination(locDep: String, locDest: String): IDestination {
        var result: IDestination = Destination()
        val geoCoder = Geocoder(this)
        try {
            val addressListDepart = geoCoder.getFromLocationName(locDep, 1)
            val addressListDest = geoCoder.getFromLocationName(locDest, 1)

            var addressDepart: IAddress = getAddress(addressListDepart[0])

            var addressDest: IAddress = getAddress(addressListDest[0])


            var coordDepart: ICoord = getCoord(addressListDepart[0])

            var coordDest: ICoord = getCoord(addressListDest[0])

            result.addressDepart = addressDepart
            result.addressDestination = addressDest
            result.coordDepart = coordDepart
            result.coordDestination = coordDest

            //return LatLng(addressList[0].latitude, addressList[0].longitude)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun getCoord(line: android.location.Address): ICoord {
        var result: ICoord = Coord()
        if (line.hasLatitude()) {
            result.latitude = line.latitude.toString()
        }
        if (line.hasLongitude()) {
            result.longitude = line.longitude.toString()
        }

        return result
    }

    private fun getAddress(line: android.location.Address): IAddress {
        var result = Address()

        if (line.subThoroughfare != null) {
            result.address = "${line.subThoroughfare.toString()}"
        }
        if (line.thoroughfare != null) {
            result.address += ", ${line.thoroughfare.toString()}"
        }

        if (line.locality != null) {
            result.city = line.locality.toString()
        } else if (line.subLocality != null) {
            result.city = line.subLocality.toString()
        }

        if (line.adminArea != null) {
            result.state = line.adminArea.toString()
        }

        if (line.postalCode != null) {
            result.zip = line.postalCode.toString()
        }

        if (line.countryName != null) {
            result.country = line.countryName.toString()
        }

        return result

    }

    private suspend fun getPlace(coord: Coord, type: String, metaKey: String, step: Int) {
        var places: ArrayList<NearPlace> =
            mapData.getActivityPlaces(coord, type, metaKey!!, step)

        if (places.size > 0) {
            //       val sorted = places.sortBy { it.distance } as ArrayList<NearPlace>
            for (idx in 0 until places.size) {
                if (idx < MAX_ACTIVITY) {
                    activityPlaces.add(places[idx])
                }
            }
        }
    }

    private fun getPlacesAwaitAll() {
        val start = System.currentTimeMillis()
        val app = context.packageManager.getApplicationInfo(
            context.packageName, PackageManager.GET_META_DATA
        )
        val bundle = app.metaData
        var metaKey = bundle.getString("com.google.android.geo.API_KEY")
        //  var coord = Coord(dest.latitude.toString(), dest.longitude.toString())
        //var types: List<String> = listOf("restaurant", "lodging", "gas_station")
        MainScope().launch(Dispatchers.IO) {
            // runBlocking {
            val tasks = ArrayList<Deferred<Unit>>()
            val steps = appGlobal.curDestination.steps
            if (steps != null) {

                for (idx in 0 until steps.count()) {
                    var step = steps[idx]

                    var coord: ICoord
                    if (idx == 0) {
                        coord = Coord(step.start!!.coord!!.latitude, step.start!!.coord!!.longitude)

                        tasks.add(async(Dispatchers.IO) {
                            getPlace(
                                coord as Coord,
                                appGlobal.ACTIVITY_MANGER_TYPE,
                                metaKey!!,
                                0
                            )
                        })
                    }

                    if (step != null && step.end != null) {
                        coord = Coord(step.end!!.coord!!.latitude, step.end!!.coord!!.longitude)

                        var id = step.step
//                        println("step:${step.step} [${step.end!!.coord!!.latitude}, ${step.end!!.coord!!.longitude}]")
                        if (step.activities != null) {
                            for (activity: IActivity in step.activities!!) {
                                var type = ""
                                when (activity.name) {
                                    appGlobal.ACTIVITY_DORMIR -> type =
                                        appGlobal.ACTIVITY_DORMIR_TYPE
                                    appGlobal.ACTIVITY_MANGER -> type =
                                        appGlobal.ACTIVITY_MANGER_TYPE
                                    appGlobal.ACTIVITY_ESSENCE -> type =
                                        appGlobal.ACTIVITY_ESSENCE_TYPE
                                    appGlobal.ACTIVITY_RECHARGE -> type =
                                        appGlobal.ACTIVITY_RECHARGE_TYPE
                                }
                                tasks.add(async(Dispatchers.IO) {
                                    getPlace(
                                        coord,
                                        type,
                                        metaKey!!,
                                        id
                                    )
                                })
                            }
                        }
                    }
                }
            }

//
//            tasks.add(async(Dispatchers.IO) { getPlace(coord, "restaurant", metaKey!!, 0) })
//            tasks.add(async(Dispatchers.IO) { getPlace(coord, "lodging", metaKey!!, 0) })
//            tasks.add(async(Dispatchers.IO) { getPlace(coord, "gas_station", metaKey!!, 0) })
//            = listOf(
//                async(Dispatchers.IO) { getPlace(coord, "restaurant", metaKey!!,0) },
//                async(Dispatchers.IO) { getPlace(coord, "lodging", metaKey!!,0) },
//                async(Dispatchers.IO) { getPlace(coord, "gas_station", metaKey!!,0) }
//            )
            tasks.awaitAll()


            //  var pls:ArrayList<NearPlace> = activityPlaces.sortBy { it.step } as ArrayList<NearPlace>
            for (step in appGlobal.curDestination.steps!!) {
                val list = activityPlaces.filter {
                    it.step == step.step
                }
                if (list != null) {
                    for (activity in step.activities!!) {
                        var type = ""
                        when (activity.name) {
                            appGlobal.ACTIVITY_DORMIR -> type =
                                appGlobal.ACTIVITY_DORMIR_TYPE
                            appGlobal.ACTIVITY_MANGER -> type =
                                appGlobal.ACTIVITY_MANGER_TYPE
                            appGlobal.ACTIVITY_ESSENCE -> type =
                                appGlobal.ACTIVITY_ESSENCE_TYPE
                            appGlobal.ACTIVITY_RECHARGE -> type =
                                appGlobal.ACTIVITY_RECHARGE_TYPE
                        }


                        if (activity.nearPlaces == null) {
                            activity.nearPlaces = ArrayList<INearPlace>()
                        }
                        activity.nearPlaces =
                            list.filter { it.type == type } as ArrayList<INearPlace>
                        var listItem: StepActivities = StepActivities()
                        listItem.stepId = step.step
                        listItem.activityId = activity.activity
                        listItem.activityName = activity.name
                        listItem.activityDuration = activity.duration
                        listItem.stepCoord = step.end!!.coord!!
                        listItem.activityPlaces = activity.nearPlaces
                        activitiesList.add(listItem)
                    }
                }

                // step.activities
            }

            val time = System.currentTimeMillis() - start
            val t = time
            val l = activitiesList

            addCitiesToSteps(activityPlaces)

//            CreateSteps.dumpDestination(appGlobal.curDestination as Destination)
//            dumpNearByPlaces(activityPlaces)

            this@MapsActivity.runOnUiThread(java.lang.Runnable {
                progressDialog.dismiss()
                modalDialogDestination()
            })
        }

    }

    private fun addCitiesToSteps(ap: ArrayList<INearPlace>) {

        var cityName = "vide"
        for (place in ap) {
            if (place.step == 0) {
                var tmp  = place.vicinity.split(",")
                cityName = tmp[tmp.size - 1]
            }
        }
        this.appGlobal.curDestination.steps!![0].start!!.name = cityName.trim()

        for (step in this.appGlobal.curDestination.steps!!) {
            for (place in ap) {
                if (place.step == step.step) {
                    var tmp  = place.vicinity.split(",")
                    cityName = tmp[tmp.size - 1]
                }
            }

            if (step.step != 1) {
                this.appGlobal.curDestination.steps!![step.step - 1].start!!.name = this.appGlobal.curDestination.steps!![step.step - 2].end!!.name
            }

            this.appGlobal.curDestination.steps!![step.step - 1].end!!.name = cityName.trim()
        }
    }

    private fun modalDialogDestination() {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_display_destinations_layout)

        val dialogTitle = dialog.findViewById<TextView>(R.id.estimation_title)
        val lvDialogDestinations = dialog.findViewById<ListView>(R.id.lv_dialog_destinations)
        val llDisplayStepsView = dialog.findViewById<LinearLayout>(R.id.ll_display_steps_view)

        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        dialogTitle.text = "Les Étapes"

        val adapterDialogDestinations = AdapterDialogDestinations(this@MapsActivity,appGlobal.curDestination.steps!!)
        lvDialogDestinations.adapter = adapterDialogDestinations

        btnCancel.setOnClickListener {
            confirmer(dialog)
        }

        btnOk.setOnClickListener {
            saveUserData(llDisplayStepsView)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun confirmer(dialogParent : Dialog) {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_cancel_confirmation_layout)

        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val tvWarningMessage = dialog.findViewById<TextView>(R.id.tv_warning_message)

        dialogTitle.text = "Confimation"

        tvWarningMessage.text = "Veullez confirmer l'annulation!"

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnOk.setOnClickListener {
            dialog.dismiss()
            dialogParent.dismiss()
        }

        dialog.show()
    }

    fun saveUserData(ll : LinearLayout) {
        MainScope().launch(Dispatchers.IO) {
//            var user = async { db.userGetCurrent() }.await()

//            user!!.destinations!!.add(appGlobal.curDestination)
//
//            var result = async { db.userUpdateCurrent(user) }.await()

            var result = ActionResult()
            result.isSuccess = true
            result.successMessage = "Yaaahoooooo!!!"

            if (result.isSuccess) {
                showMessage(result.successMessage, ll)
            } else {
                showMessage(result.errorMessage, ll)
            }
        }
    }

    private fun showMessage(message : String, view: View) {
        Snackbar.make(context, view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun dumpNearByPlaces(ap: ArrayList<INearPlace>) {

        println("****************************************************************************")
        for (aPlace in ap) {

            var tmp  = aPlace.vicinity.split(",")
            println("${aPlace.name} type:${aPlace.type} distance:${aPlace.distance} business_status:${aPlace.business_status}")
            var msg = "[${aPlace.step} ${aPlace.vicinity}]"
            for (s in tmp) {
                msg += " -$s-"
            }
            println(msg)
        }
        println("****************************************************************************")
    }
}
