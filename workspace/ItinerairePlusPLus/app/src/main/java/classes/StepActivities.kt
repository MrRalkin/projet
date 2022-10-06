package classes

import classes.map.NearPlace
import classes.settings.Coord
import interfaces.user.ICoord
import interfaces.user.INearPlace
import kotlinx.parcelize.RawValue

class StepActivities(
    var stepId: Int = 0,
    var stepCoord: ICoord=Coord(),
    var activityId: Int = 0,
    var activityName: String = "",
    var activityDuration: Int = 0,
    var activityPlaces:ArrayList<INearPlace>? =null
)

//class ActivityPlace(
//    var placeType: String = "",
//    var placeName: String = "",
//    var placeIcon: String = "",
//    var placeAddress: String = "",
//    var placeDistance: Int = 0,
//    var placeCoord: ICoord = Coord(),
//)