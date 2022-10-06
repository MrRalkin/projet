package classes.settings

import android.os.Parcelable
import dbfirestore.FsAddress
import dbfirestore.FsCoord
import interfaces.user.IAddress
import interfaces.user.ICoord
import interfaces.user.IDestination
import interfaces.user.IStep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class Destination(
    override var destinationId: String = "",
    override var name: String = "",
    override var coordDepart: @RawValue ICoord? = Coord(),
    override var coordDestination: @RawValue ICoord? = Coord(),
    override var addressDepart: @RawValue IAddress? = Address(),
    override var addressDestination: @RawValue IAddress? = Address(),
    override var image: String = "",
    override var trip_time: Int = 0,
    override var steps: @RawValue ArrayList<IStep>? = ArrayList<IStep>(),

    ) : IDestination(), Parcelable