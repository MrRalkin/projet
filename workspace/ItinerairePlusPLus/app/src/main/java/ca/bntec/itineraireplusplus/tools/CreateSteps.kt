package ca.bntec.itineraireplusplus.tools

import classes.AppGlobal
import classes.map.MapLegData
import classes.settings.*
import interfaces.user.IActivity
import interfaces.user.IDestination

class CreateSteps {
    companion object {

        private var listOfCoord = ArrayList<Coord>()

        private var metersByNbCoord: Double = 0.0
        private var secondsByNbCoord: Double = 0.0
        private var nbCoordOneKm: Double = 0.0
        private var nbCoordOneMinute: Double = 0.0
        private val appGlobal = AppGlobal.instance

        private fun setLegData(mapLegData: MapLegData) {

            listOfCoord = mapLegData.legPoints

            metersByNbCoord = mapLegData.legDistance.toDouble() / listOfCoord.size
            secondsByNbCoord = mapLegData.legDuration.toDouble() / listOfCoord.size
            println(String.format("metres/coord : %5.2f m/c\nseconds/coord: %5.2f s/c", metersByNbCoord, secondsByNbCoord))
            nbCoordOneKm = 1000 / metersByNbCoord
            nbCoordOneMinute = 60 / secondsByNbCoord

        }

        fun createSteps(
            dest: IDestination,
            mapLegData: MapLegData
        ): IDestination {

            setLegData(mapLegData)

            val settings = dest.settings
            var userKm = 0
            var userManger = 0
            var userDormir = 0
            var currentUserKm = -1
            var currentUserManger = -1
            var currentUserDormir = -1
            var indiceKm = 0
            var indiceManger = 0
            var indiceDormir = 0
            val travelTime:Int = (listOfCoord.size / nbCoordOneMinute).toInt()
            val travelKm:Int = (listOfCoord.size / nbCoordOneKm).toInt()

            val listOfCoordToKeep = ArrayList<Boolean>()

            indiceKm = 0
            userKm = settings!!.vehicles[indiceKm].distance - 50
            currentUserKm = userKm
            listOfCoordToKeep.add(false)

            var cpt = indiceKm + 1
            for (index in 0 until settings.activities.size) {
                when (settings.activities[index].name) {
                    appGlobal.ACTIVITY_MANGER -> {
                        userManger = settings.activities[index].time / 60
                        currentUserManger = userManger
                        indiceManger = cpt++
                        listOfCoordToKeep.add(false)
                    }
                    appGlobal.ACTIVITY_DORMIR -> {
                        userDormir = settings.activities[index].time / 60
                        currentUserDormir = userDormir
                        indiceDormir = cpt++
                        listOfCoordToKeep.add(false)
                    }
                }
            }

            var fromCoord: Coord = listOfCoord[0]
            var toCoord: Coord = listOfCoord[listOfCoord.size - 1]

            dest.coordDepart = fromCoord
            dest.coordDestination = toCoord
            dest.trip_time = 0

            dest.steps = ArrayList()

            var stepCount = 0

            var currentKm = 0
            var currentTime = 0

//    for (indexForCoord in 1 until listOfCoord.size) {
            var indexForCoord = 0
            while (indexForCoord < listOfCoord.size) {

                indexForCoord++

                if ((indexForCoord % nbCoordOneKm).toInt() == 0) {

                    currentKm++
                }
                if ((indexForCoord % nbCoordOneMinute).toInt() == 0) {

                    currentTime++
                }

                if (currentKm == currentUserKm) {

                    currentUserKm += userKm
                    listOfCoordToKeep[indiceKm] = true
                }

                if (currentTime == currentUserDormir) {

                    currentUserDormir += userDormir
                    currentUserKm = (currentKm + userKm)
                    listOfCoordToKeep[indiceDormir] = true
                    listOfCoordToKeep[indiceKm] = true
                }

                if (currentTime == currentUserManger) {

                    currentUserManger += userManger
                    currentUserKm = (currentKm + userKm)
                    listOfCoordToKeep[indiceManger] = true
                    listOfCoordToKeep[indiceKm] = true
                }

                if (listOfCoordToKeep.contains(true)) {

                    if ((currentTime + 60) >= travelTime || (currentKm + 30) >= travelKm) {
                        currentTime = travelTime
                        currentKm = travelKm
                        toCoord = listOfCoord[listOfCoord.size - 1]

                        listOfCoordToKeep.fill(true)
                    } else {

                        toCoord = listOfCoord[indexForCoord]
                    }

                    val step = addCoordToStep(++stepCount, fromCoord, toCoord)

                    step.trip_time =  (currentTime * 60)
                    step.activities = addActivityToStep(settings as Settings, listOfCoordToKeep)

                    dest.steps!!.add(step)

                    for (i in 0 until step.activities!!.size) {
                        dest.trip_time += step.activities!![i].duration
                    }

                    fromCoord = toCoord

                    listOfCoordToKeep.fill(false)
                }
            }

            if (currentTime == travelTime) {
                listOfCoordToKeep.fill(true)

                toCoord = listOfCoord[listOfCoord.size - 1]

                val step = addCoordToStep(++stepCount, fromCoord, toCoord)
                step.trip_time = ((listOfCoord.size / nbCoordOneMinute) * 60).toInt()
                step.activities = addActivityToStep(settings as Settings, listOfCoordToKeep)

                dest.steps!!.add(step)
            }

            return dest
        }

        private fun addActivityToStep(
            s: Settings,
            keepCoord: ArrayList<Boolean>
        ): ArrayList<IActivity> {

            val listActivity = ArrayList<IActivity>()
            var cpt = 0
            for (i in 0 until keepCoord.size) {
                if (keepCoord[i]) {
                    val a = Activity()
                    a.activity = ++cpt
                    a.name = s.activities[i].name
                    a.time = s.activities[i].time
                    a.duration = s.activities[i].duration
                    listActivity.add(a)
                }
            }

            return listActivity
        }

        private fun addCoordToStep(stepCount: Int, from: Coord, to: Coord): Step {

            val aStep = Step()
            val fromPoint = Point()
            val toPoint = Point()
            val fromAddress = Address()
            val toAddress = Address()

            aStep.step = stepCount

            fromPoint.coord = from
            fromPoint.name = "Étape ${stepCount - 1}"
            fromPoint.address = fromAddress
            aStep.start = fromPoint

            toPoint.coord = to
            toPoint.name = "Étape $stepCount"
            toPoint.address = toAddress
            aStep.end = toPoint

            return aStep
        }
    }
}
