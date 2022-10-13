package classes

import classes.settings.Coord
import interfaces.user.ICoord
import interfaces.user.INearPlace

class StepActivities(
    var stepId: Int = 0,
    var stepCoord: ICoord=Coord(),
    var activityId: Int = 0,
    var activityName: String = "",
    var activityDuration: Int = 0,
    var activityPlaces:ArrayList<INearPlace>? =null
)
