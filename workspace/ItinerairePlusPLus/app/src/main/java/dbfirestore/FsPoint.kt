package dbfirestore

import android.os.Parcelable
import interfaces.user.ICoord
import interfaces.user.IPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class FsPoint(
    override var name: String = "",
    override var coord: @RawValue ICoord = FsCoord(),
    override var adresse: String = "",
    override var city: String = "",
    override var state: String = "",
    override var zip: String = "",
    override var country: String = ""
) : IPoint, Parcelable