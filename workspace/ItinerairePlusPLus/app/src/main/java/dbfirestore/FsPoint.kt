package dbfirestore

import android.os.Parcelable
import interfaces.user.IAddress
import interfaces.user.ICoord
import interfaces.user.IPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class FsPoint(
    override var name: String = "",
    override var coord: @RawValue ICoord = FsCoord(),
    override var address: @RawValue  IAddress = FsAddress(),

    ) : IPoint, Parcelable