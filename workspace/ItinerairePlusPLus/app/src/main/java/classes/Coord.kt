package classes

import android.os.Parcelable
import interfaces.user.ICoord
import kotlinx.parcelize.Parcelize

@Parcelize
class Coord(
    override var longitude: String = "",
    override var latitude: String = ""
) : ICoord(), Parcelable