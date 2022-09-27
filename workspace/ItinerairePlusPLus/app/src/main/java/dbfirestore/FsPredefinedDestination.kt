package dbfirestore

import android.os.Parcelable
import interfaces.user.IAddress
import interfaces.user.ICoord
import interfaces.user.IPredefinedDestination
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class FsPredefinedDestination(
    override var name: String = "",
    override var description: String = "",
    override var coord:@RawValue ICoord? = null,
    override var address:@RawValue IAddress? = null,
    override var image: String = ""
) : IPredefinedDestination(), Parcelable