package dbfirestore

import android.os.Parcelable
import interfaces.user.ICoord
import interfaces.user.IDestination
import interfaces.user.IStep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class FsDestination(
    override var name: String = "",
    override var coord: @RawValue ICoord = FsCoord(),
    override var adresse: String = "",
    override var city: String = "",
    override var province: String = "",
    override var postal_code: String = "",
    override var country: String = "",
    override var image: String = "",
    override var tripTime: Int = 0,
    override var steps: @RawValue ArrayList<IStep> = ArrayList<IStep>()
) : IDestination, Parcelable