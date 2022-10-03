package classes.map

import classes.settings.Coord
import interfaces.user.ICoord

class MapLegData (
    var legDistance:Int=0,
    var legDuration:Int=0,
    var legPoints: ArrayList<Coord> =ArrayList<Coord>()
)