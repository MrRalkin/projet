package classes.map

import android.util.Log
import classes.settings.Coord
import com.google.android.gms.maps.model.LatLng
import interfaces.user.ICoord
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MapData {
    companion object {
        var mapLegData = MapLegData()
    }

    fun getMapLegData(): MapLegData {
        return mapLegData
    }

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude  */
    fun parse(jObject: JSONObject): ArrayList<ArrayList<HashMap<String, String>>> {
        val routes: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
        var jRoutes: JSONArray? = null
        var jLegs: JSONArray? = null
        var jSteps: JSONArray? = null
        try {
            jRoutes = jObject.getJSONArray("routes")
            /** Traversing all routes  */
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")

                val path = ArrayList<HashMap<String, String>>()
                /** Traversing all legs  */
                for (j in 0 until jLegs.length()) {

                    val duration: JSONObject = (jLegs[j] as JSONObject).getJSONObject("duration")
                    val distance: JSONObject = (jLegs[j] as JSONObject).getJSONObject("distance")
                    mapLegData.legDuration = duration.getInt("value")
                    mapLegData.legDistance = distance.getInt("value")

                    jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                    /** Traversing all steps  */
                    for (k in 0 until jSteps.length()) {
                        var polyline = ""
                        polyline =
                            ((jSteps[k] as JSONObject)["polyline"] as JSONObject)["points"] as String
                        val list = decodePoly(polyline)
                        /** Traversing all points  */
                        for (l in list.indices) {
                            val hm = HashMap<String, String>()

                            hm["lat"] = java.lang.Double.toString((list[l] as LatLng).latitude)
                            hm["lng"] = java.lang.Double.toString((list[l] as LatLng).longitude)
                            mapLegData.legPoints.add(
                                Coord(
                                    hm["lat"].toString(),
                                    hm["lng"].toString()
                                )
                            )
                            path.add(hm)
                        }
                    }
                    routes.add(path)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return routes
    }

    suspend fun getActivityPlaces(coord: Coord, type: String, key: String, step:Int): ArrayList<NearPlace> {
        val result = ArrayList<NearPlace>()

        val uLocation = "location=" + coord.latitude + "," + coord.longitude
        val uKey = "key=$key"
        val uRadius = "radius=30000"
        val uType = "type=$type"
        val uOutput = "json"
        val uParams =
            "$uLocation&$uRadius&$uType&$uKey"

        val link = "https://maps.googleapis.com/maps/api/place/nearbysearch/$uOutput?$uParams"

        try {
            val data = downloadUrl(link)
            val t = JSONObject(data)
            if (t["status"] != null && t["status"] == "OK") {
                val results = t.getJSONArray("results")
                for (idx in 0 until results.length()) {
                    val line: JSONObject = results[idx] as JSONObject
                    val nearPlace: NearPlace = NearPlace()
                    nearPlace.step=step
                    nearPlace.name = line.getString("name")
                    nearPlace.business_status = line.getString("business_status")
                    nearPlace.icon = line.getString("icon")
                    nearPlace.vicinity = line.getString("vicinity")
                    nearPlace.location!!.latitude =
                        line.getJSONObject("geometry").getJSONObject("location").getString("lat")
                    nearPlace.location!!.longitude =
                        line.getJSONObject("geometry").getJSONObject("location").getString("lng")
                    nearPlace.northeast!!.latitude =
                        line.getJSONObject("geometry").getJSONObject("viewport")
                            .getJSONObject("northeast").getString("lat")
                    nearPlace.northeast!!.longitude =
                        line.getJSONObject("geometry").getJSONObject("viewport")
                            .getJSONObject("northeast").getString("lng")
                    nearPlace.northeast!!.latitude =
                        line.getJSONObject("geometry").getJSONObject("viewport")
                            .getJSONObject("southwest").getString("lat")
                    nearPlace.northeast!!.longitude =
                        line.getJSONObject("geometry").getJSONObject("viewport")
                            .getJSONObject("southwest").getString("lng")
                    nearPlace.type = type
                    val i: Int = getDistance(coord, nearPlace.location!!, uKey)
                    nearPlace.distance = i
                    result.add(nearPlace)

                }
            }
        } catch (e: Exception) {
            println(e.message)
        }

        return result

    }

    private suspend fun getDistance(origins: ICoord, destinations: ICoord, apiKey: String): Int {
        var result = 0

        val org = "origins=${origins.latitude},${origins.longitude}"
        val dest = "destinations=${destinations.latitude},${destinations.longitude}"
        val output = "json"
        val parameters = "$org&$dest&&$apiKey"
        val link = "https://maps.googleapis.com/maps/api/distancematrix/$output?$parameters"
        try {
            val data = downloadUrl(link)
            val rows = JSONObject(data).getJSONArray("rows")
                for(row in 0 until rows.length()){

                    var elements= (rows[row] as JSONObject ).getJSONArray("elements")
                    for(el in 0 until elements.length()){
                        var i = (elements[el] as JSONObject ).getJSONObject("distance").getInt("value")
                        result+=i
                    }

                }
        } catch (e: Exception) {
            println(e.message)
        }

        return result
    }

    suspend fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection!!.connect()
            iStream = urlConnection!!.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data!!
    }

    private fun decodePoly(encoded: String): ArrayList<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }
}