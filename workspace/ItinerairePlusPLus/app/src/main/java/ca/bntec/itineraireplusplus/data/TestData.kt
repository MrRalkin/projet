package ca.bntec.itineraireplusplus.data

import ca.bntec.itineraireplusplus.tools.Tools
import ca.bntec.itineraireplusplus.tools.Tools.Companion.convertSecondsToTime
import classes.*
import classes.settings.*
import interfaces.user.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TestData {
    private val db = AppGlobal.instance.userManager

    fun setTestData(user: IUser) {

        val destinations = ArrayList<IDestination>()

//        println(convertSecondsToTime(54))
//        println(convertSecondsToTime(60))
//        println(convertSecondsToTime(120))
//        println(convertSecondsToTime(480))
//        println(convertSecondsToTime(1440))
//        println(convertSecondsToTime(2880))
//        println(convertSecondsToTime(86400))
//        println(convertSecondsToTime(86400 * 8))
//        println(convertSecondsToTime(86400 * 8 + 584))
//        println(convertSecondsToTime(86400 * 18))

        
        /**
         * activities
         * */
        val activities1 = ArrayList<IActivity>()

        activities1.add(addActivity(1, "Manger", 2 * Tools.HOUR))

        val activties2 = ArrayList<IActivity>()
        activties2.add(addActivity(1, "Manger", 2 * Tools.HOUR))
        activties2.add(addActivity(2, "Essence", Tools.HOUR / 2))

        val activties3 = ArrayList<IActivity>()
        activties3.add(addActivity(1, "Manger", 2 * Tools.HOUR))
        activties3.add(addActivity(2, "Essence", Tools.HOUR / 2))
        activties3.add(addActivity(3, "Dormir", 6 * Tools.HOUR))

        /**
         * vehicles
         * */
        val vehicles1 = ArrayList<IVehicle>()
        vehicles1.add(addVehicle("Auto", "essence", 700, "km", 55, "litre"))
        val vehicles2 = ArrayList<IVehicle>()
        vehicles2.add(addVehicle("Auto", "électricité", 600, "km", 100, "kwh"))

        /**
         * energies
         * */
        val energies1 = ArrayList<IEnergy>()
        energies1.add(addEnergy("essence", 1.50, "litre"))
        val energies2 = ArrayList<IEnergy>()
        energies2.add(addEnergy("électricité", 0.047, "kwh"))


        /**
         * steps
         * */
        val steps = ArrayList<IStep>()

        steps.add(addStep(1,
            addPoint("Montréal",
                addCoord("-12.000909", "-12.000909"),
                addAddress("", "Montreal", "Qc", "", "Canada")
            ),
            addPoint("Ottawa",
                addCoord("-12.000909", "-12.000909"),
                addAddress("", "Ottawa", "On", "", "Canada")
            ),
            1 * Tools.HOUR + 34 * Tools.MINUTES,
            activities1
        ))

        steps.add(addStep(2,
            addPoint("Ottawa",
                addCoord("-12.000909", "-12.000909"),
                addAddress("", "Ottawa", "Qc", "", "Canada")
            ),
            addPoint("Toronto",
                addCoord("-12.000909", "-12.000909"),
                addAddress("", "Toronto", "On", "", "Canada")
            ),
            45 * Tools.MINUTES,
            activties2
        ))

        /**
         * destinations
         * */
        destinations.add(
            addDestinations("First destination", "", 2 * Tools.HOUR + 57 * Tools.MINUTES,
                addAddress("123 la rue", "Ottawa", "On", "", "Canada"),
                addCoord("43.2487862", "-76.3606792"),
                steps,
                addSettings(vehicles1, activities1, energies1)
        ))

        destinations.add(
            addDestinations("Deuxième destination", "", 1 * Tools.HOUR + 9 * Tools.MINUTES,
                addAddress("456 the street", "Toronto", "On", "", "Canada"),
                addCoord("45.2487862", "-76.3606792"),
                steps,
                addSettings(vehicles2, activities1, energies2)
            ))

        destinations.add(
            addDestinations("Troisième destination", "", 5 * Tools.HOUR + 21 * Tools.MINUTES,
                addAddress("456 the street", "Toronto", "On", "", "Canada"),
                addCoord("45.2487862", "-76.3606792"),
                steps,
                addSettings(vehicles2, activities1, energies2)
            ))

        destinations.add(
            addDestinations("Quatrième destination", "", 2 * Tools.HOUR + 12 * Tools.MINUTES,
                addAddress("456 the street", "Toronto", "On", "", "Canada"),
                addCoord("45.2487862", "-76.3606792"),
                steps,
                addSettings(vehicles2, activities1, energies2)
            ))

        destinations.add(
            addDestinations("Cinquième destination", "", 4 * Tools.HOUR,
                addAddress("456 the street", "Toronto", "On", "", "Canada"),
                addCoord("45.2487862", "-76.3606792"),
                steps,
                addSettings(vehicles2, activities1, energies2)
            ))

        /**
         * user section
         * */
        user.address = addAddress("829 rue paris", "Paris", "Qc", "J4J1A5", "Canada")
        user.destinations = destinations

        /**
         * user settings
         * */
        val energiesSettings = ArrayList<IEnergy>()
        energiesSettings.add(addEnergy("essence", 1.50, "litre"))
        energiesSettings.add(addEnergy("électricité", 0.047, "kwh"))
        user.settings.energies = energiesSettings


        val activitiesSettings = ArrayList<IActivity>()
        activitiesSettings.add(addActivity(1, "Manger", 2 * Tools.HOUR))
        activitiesSettings.add(addActivity(2, "Essence", Tools.HOUR / 2))
        activitiesSettings.add(addActivity(3, "Recharge", 1 * Tools.HOUR))
        activitiesSettings.add(addActivity(4, "Dormir", 8 * Tools.HOUR))
        user.settings.activities = activitiesSettings

        val vehiclesSettings = ArrayList<IVehicle>()
        vehiclesSettings.add(addVehicle("Auto", "électricité", 625, "km", 100, "kwh" ))
        user.settings.vehicles = vehiclesSettings

        /**
         * SAVE/UPDATE user
         */
        MainScope().launch(Dispatchers.IO) {
            val result = async { db.userUpdateCurrent(user) }.await()

            println("********************* SAVE/UPDATE user *********************")
            println("*** >>> " + if (result.isSuccess) {result.successMessage} else {result.errorMessage})
            println("************************************************************")

//            Snackbar.make(ctx, view, "Can't delete last record", Snackbar.LENGTH_SHORT).show()
        }
    }

    /** -------------------------------------------------------------------------------------------
     */
    private fun addDestinations(
        name: String,
        image: String,
        trip_time: Int,
        address: Address,
        coord: Coord,
        steps: ArrayList<IStep>,
        settings: Settings,
    ) : Destination {
        val d = Destination()

        d.name = name
        d.image = image
        d.trip_time = trip_time
        d.addressDepart = address
        d.addressDestination = address
        d.coordDepart = coord
        d.coordDestination = coord
        d.steps = steps
        d.settings = settings

        return d
    }

    private fun addStep(
        step: Int,
        start: Point,
        end: Point,
        trip_time: Int,
        activities: ArrayList<IActivity>
    ) : Step {
        val s = Step()

        s.step = step
        s.start = start
        s.end = end
        s.trip_time = trip_time
        s.activities = activities

        return s
    }

    private fun addActivity(
        activity: Int,
        name: String,
        time: Int
    ) : Activity {
        val a = Activity()

        a.activity = activity
        a.name = name
        a.time = time

        return a
    }

    private fun addPoint(
        name: String,
        coord: Coord,
        address: Address,

    ) : Point {
        val p = Point()

        p.name = name
        p.coord = coord
        p.address = address

        return p
    }

    private fun addCoord(
        longitude: String,
        latitude: String
    ) : Coord {
        val c = Coord()

        c.longitude = longitude
        c.latitude = latitude

        return c
    }

    private fun addAddress(
        address: String,
        city: String,
        state: String,
        zip: String,
        country: String
    ) : Address {
        val a = Address()

        a.address = address
        a.city = city
        a.state = state
        a.zip = zip
        a.country = country

        return a
    }

    private fun addEnergy(
        type: String,
        price: Double,
        unit: String
    ) : Energy {
        val e = Energy()

        e.type = type
        e.price = price
        e.unit = unit

        return e
    }

    private fun addVehicle(
        type: String,
        energy: String,
        distance: Int,
        mesure: String,
        capacity: Int,
        unit: String
    ) : Vehicle {
        val v = Vehicle()

        v.type = type
        v.energy = energy
        v.distance = distance
        v.mesure = mesure
        v.capacity = capacity
        v.unit = unit

        return v
    }

    private fun addSettings(
        vehicles: ArrayList<IVehicle>,
        activities: ArrayList<IActivity>,
        energies: ArrayList<IEnergy>
    ) : Settings {
        val s = Settings()

        s.vehicles = vehicles
        s.activities = activities
        s.energies = energies

        return s
    }
}


/** -----------------------------------------------------------------------------------------------

        "step" : 1,
        "start": {
            "name" : "Montreal",
            "coord" : {"longitude" : "-12.000909", "latitude" : "-12.000909"},
            "adresse" : {
            "no_civic" : "123",
            "street" : "rue paris",
            "city" : "Paris",
            "province" : "oups",
            "postal_code" : "J4J 3K5",
            "country" : "France",
        },
        },
        "end" : {
            "name" : "New York",
            "coord" : {"longitude" : "-12.000909", "latitude" : "-12.000909"},
            "adresse" : {
            "no_civic" : "123",
            "street" : "rue paris",
            "city" : "Paris",
            "province" : "oups",
            "postal_code" : "J4J 3K5",
            "country" : "France",
        },
        },
        "trip_time_minutes" : 360,
        "activities": [
        {"activity" : 1, "name" : "Manger", "time_minutes" : "120"},
        {"activity" : 2, "name" : "Essence", "time_minutes" : "15"}]

 */