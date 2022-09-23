package classes.settings

import android.os.Parcelable
import dbfirestore.FsAddress
import dbfirestore.FsCoord
import interfaces.user.IAddress
import interfaces.user.ICoord
import interfaces.user.IPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class Point(
    override var name: String = "",
    override var coord: @RawValue ICoord ?= FsCoord(),
    override var address: @RawValue  IAddress? = FsAddress(),

    ) : IPoint(), Parcelable