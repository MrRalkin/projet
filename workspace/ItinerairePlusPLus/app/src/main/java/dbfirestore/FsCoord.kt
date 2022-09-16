package dbfirestore

import android.os.Parcelable
import interfaces.user.ICoord
import kotlinx.parcelize.Parcelize

@Parcelize
class FsCoord(
    override var longitude: String = "",
    override var latitude: String = ""
) : ICoord, Parcelable