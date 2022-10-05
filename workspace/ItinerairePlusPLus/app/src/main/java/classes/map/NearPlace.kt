package classes.map

import classes.settings.Coord

class NearPlace(
    var business_status: String = "",
    var location: Coord = Coord(),
    var northeast: Coord = Coord(),
    var southwest: Coord = Coord(),
    var icon: String = "",
    var name: String = "",
    var type: String = "",
    var vicinity: String = "",
    var distance: Int = 0,
    var step: Int = 0
)