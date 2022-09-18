package dbfirestore

import android.os.Parcelable
import interfaces.user.IAddress
import interfaces.user.ICoord
import interfaces.user.IDestination
import interfaces.user.IStep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class FsDestination(
    override var name: String = "",
    override var coord: @RawValue ICoord = FsCoord(),
    override var address:@RawValue IAddress=FsAddress(),
    override var image: String = "",
    override var trip_time: Int = 0,
    override var steps: @RawValue ArrayList<IStep> = ArrayList<IStep>(),

    ) : IDestination, Parcelable