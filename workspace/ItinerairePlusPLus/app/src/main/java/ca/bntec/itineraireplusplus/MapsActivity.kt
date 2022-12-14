package ca.bntec.itineraireplusplus

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.bntec.itineraireplusplus.adapter.AdapterDialogDestinations
import ca.bntec.itineraireplusplus.databinding.ActivityMapsBinding
import ca.bntec.itineraireplusplus.tools.CreateSteps
import classes.AppGlobal
import classes.map.StepActivities
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

    val appGlobal = AppGlobal.instance
    var activitiesList: ArrayList<StepActivities> = ArrayList<StepActivities>()

    lateinit var mapFragment: SupportMapFragment
    lateinit var progressDialog: ProgressDialog
    lateinit var destination: IDestination
    lateinit var mapRawData: IMapRawData
    lateinit var mapLegData: MapLegData
    lateinit var btnSeeSteps : Button
    lateinit var btnSaveData : Button
    lateinit var context: Context

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var activityPlaces = ArrayList<INearPlace>()
    private val MAX_ACTIVITY = 3
    private val mapData = MapData()
    private val db = appGlobal.userManager
    private val uid: String = UUID.randomUUID().toString()

    lateinit var origin : LatLng
    lateinit var dest : LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        context = this

        btnSeeSteps = findViewById(R.id.btn_see_steps)
        btnSaveData = findViewById(R.id.btn_save_data)

        destination = getDestination(appGlobal.departAddress, appGlobal.destAddress)
        origin = LatLng(
            destination.coordDepart!!.latitude.toDouble(),
            destination.coordDepart!!.longitude.toDouble()
        )
        dest = LatLng(
            destination.coordDestination!!.latitude.toDouble(),
            destination.coordDestination!!.longitude.toDouble()
        )

        drawPolylines()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true

        googleMap.addMarker(
            MarkerOptions().position(origin).title("SSNTeam")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        googleMap.addMarker(
            MarkerOptions().position(dest)
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 15f))
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        val strOrigin = "origin=" + origin.latitude + "," + origin.longitude
        val strDest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"
        val mode = "mode=driving"

        val app = context.packageManager.getApplicationInfo(
            context.packageName, PackageManager.GET_META_DATA
        )
        val bundle = app.metaData

        var metaKey = bundle.getString("com.google.android.geo.API_KEY")

        val key = "key=$metaKey"

        val parameters = "$strOrigin&$strDest&$sensor&$mode&$key"

        val output = "json"

        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    private fun drawPolylines() {
        progressDialog = ProgressDialog(this@MapsActivity)
        progressDialog.setMessage("Veuillez patienter, la polyligne entre deux emplacements est en cours de construction.")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val url: String = getDirectionsUrl(origin, dest)

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
            /***
             * FUTURE feature
             * JSON will be save in DB for later reference.
             */
//            db.setMapRawData(mapRawData)
            jObject = JSONObject(data)

            routes = mapData.parse(jObject)
            mapLegData = mapData.getMapLegData()

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

            this@MapsActivity.runOnUiThread(java.lang.Runnable {
                creationDesEtapes()
                mMap.addPolyline(lineOptions!!)
            })
        }
    }

    private fun creationDesEtapes() {

        appGlobal.curDestination = CreateSteps.createSteps(Destination(), mapLegData)

        getPlacesAwaitAll()
    }

    private fun getDestination(locDep: String, locDest: String): IDestination {
        var result: IDestination = Destination()
        val geoCoder = Geocoder(this)
        try {
            val addressListDepart = geoCoder.getFromLocationName(locDep, 1)
            val addressListDest = geoCoder.getFromLocationName(locDest, 1)

            val addressDepart: IAddress = getAddress(addressListDepart[0])

            val addressDest: IAddress = getAddress(addressListDest[0])

            val coordDepart: ICoord = getCoord(addressListDepart[0])

            val coordDest: ICoord = getCoord(addressListDest[0])

            result.addressDepart = addressDepart
            result.addressDestination = addressDest
            result.coordDepart = coordDepart
            result.coordDestination = coordDest

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun getCoord(line: android.location.Address): ICoord {
        val result: ICoord = Coord()
        if (line.hasLatitude()) {
            result.latitude = line.latitude.toString()
        }
        if (line.hasLongitude()) {
            result.longitude = line.longitude.toString()
        }
        return result
    }

    private fun getAddress(line: android.location.Address): IAddress {
        val result = Address()

        if (line.subThoroughfare != null) {
            result.address = line.subThoroughfare.toString()
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
        val places: ArrayList<NearPlace> =
            mapData.getActivityPlaces(coord, type, metaKey, step)

        if (places.size > 0) {
            for (idx in 0 until places.size) {
                if (idx < MAX_ACTIVITY) {
                    activityPlaces.add(places[idx])
                }
            }
        }
    }

    private fun getPlacesAwaitAll() {
        val app = context.packageManager.getApplicationInfo(
            context.packageName, PackageManager.GET_META_DATA
        )
        val bundle = app.metaData
        val metaKey = bundle.getString("com.google.android.geo.API_KEY")

        MainScope().launch(Dispatchers.IO) {
            val tasks = ArrayList<Deferred<Unit>>()
            val steps = appGlobal.curDestination.steps
            if (steps != null) {

                for (idx in 0 until steps.count()) {
                    var step = steps[idx]

                    var coord0: ICoord
                    if (idx == 0) {
                        coord0 = Coord(step.start!!.coord!!.latitude, step.start!!.coord!!.longitude)

                        tasks.add(async(Dispatchers.IO) {
                            getPlace(coord0, appGlobal.ACTIVITY_MANGER_TYPE, metaKey!!,0)
                        })
                        tasks.add(async(Dispatchers.IO) {
                            getPlace(coord0, appGlobal.ACTIVITY_DORMIR_TYPE, metaKey!!,0)
                        })
                        tasks.add(async(Dispatchers.IO) {
                            getPlace(coord0, appGlobal.ACTIVITY_ESSENCE_TYPE, metaKey!!,0)
                        })
                        tasks.add(async(Dispatchers.IO) {
                            getPlace(coord0, appGlobal.ACTIVITY_RECHARGE_TYPE, metaKey!!,0)
                        })
                    }

                    var coord: ICoord
                    if (step != null && step.end != null) {
                        coord = Coord(step.end!!.coord!!.latitude, step.end!!.coord!!.longitude)

                        var id = step.step
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
                                    getPlace(coord, type, metaKey!!, id)
                                })
                            }
                        }
                    }
                }
            }

            tasks.awaitAll()

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
            }

            addCitiesToSteps(activityPlaces)

            btnSeeSteps.setOnClickListener {
                this@MapsActivity.runOnUiThread(java.lang.Runnable {
                    modalDialogSteps()
                })
            }

            btnSaveData.setOnClickListener {
                if (!appGlobal.isCurDestinationSaved)
                {
                    saveUserData()
                    appGlobal.isCurDestinationSaved = true
                    btnSaveData.isEnabled = false
                } else {
                    btnSaveData.isEnabled = true
                }
            }

            this@MapsActivity.runOnUiThread(java.lang.Runnable {
                progressDialog.dismiss()
                modalDialogSteps()
            })
        }
    }

    private fun addCitiesToSteps(ap: ArrayList<INearPlace>) {

        var cityName = "vide"
        for (place in ap) {
            if (place.step == 0) {
                val tmp  = place.vicinity.split(",")
                cityName = tmp[tmp.size - 1]
            }
        }
        this.appGlobal.curDestination.steps!![0].start!!.name = cityName.trim()

        for (step in this.appGlobal.curDestination.steps!!) {
            for (place in ap) {
                if (place.step == step.step) {
                    val tmp  = place.vicinity.split(",")
                    cityName = tmp[tmp.size - 1]
                }
            }

            if (step.step != 1) {
                this.appGlobal.curDestination.steps!![step.step - 1].start!!.name = this.appGlobal.curDestination.steps!![step.step - 2].end!!.name
            }

            this.appGlobal.curDestination.steps!![step.step - 1].end!!.name = cityName.trim()
        }
    }

    private fun modalDialogSteps() {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_display_steps_layout)

        val dialogTitle = dialog.findViewById<TextView>(R.id.estimation_title)
        val lvDialogDestinations = dialog.findViewById<ListView>(R.id.lv_dialog_destinations)

        val btnVoirCarte = dialog.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        if (appGlobal.isCurDestinationSaved)
            btnOk.text = "Ok"
        else
            btnOk.text = "Sauvegarder"

        dialogTitle.text = "${appGlobal.curDestination.name} : Les ??tapes"

        val adapterDialogDestinations = AdapterDialogDestinations(this@MapsActivity,appGlobal.curDestination.steps!!)
        lvDialogDestinations.adapter = adapterDialogDestinations

        btnVoirCarte.setOnClickListener {
            dialog.dismiss()
        }

        btnOk.setOnClickListener {
            if (!appGlobal.isCurDestinationSaved)
            {
                saveUserData()
                appGlobal.isCurDestinationSaved = true
                btnSaveData.isEnabled = false
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun saveUserData() {
        MainScope().launch(Dispatchers.IO) {
            val user = async { db.userGetCurrent() }.await()

            user!!.destinations!!.add(appGlobal.curDestination)

            val result = async { db.userUpdateCurrent(user) }.await()

            if (result.isSuccess) {
                showMessage(result.successMessage)
            } else {
                showMessage(result.errorMessage)
            }
        }
    }

    private fun showMessage(message : String) {
        this@MapsActivity.runOnUiThread(java.lang.Runnable {
            Snackbar.make(context, binding.root, message, Snackbar.LENGTH_SHORT).show()
        })
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
