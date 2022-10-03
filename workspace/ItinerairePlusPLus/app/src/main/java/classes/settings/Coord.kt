package classes.settings

import android.os.Parcelable
import interfaces.user.ICoord
import kotlinx.parcelize.Parcelize

@Parcelize
class Coord(
    override var latitude: String = "",
    override var longitude: String = ""
) : ICoord(), Parcelable