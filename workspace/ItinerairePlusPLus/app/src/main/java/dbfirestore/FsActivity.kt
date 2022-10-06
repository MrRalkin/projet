package dbfirestore

import android.os.Parcelable
import interfaces.user.IActivity
import interfaces.user.INearPlace
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class FsActivity(
    override var activity: Int = 0,
    override var name: String = "",
    override var time: Int = 0,
    override var duration:Int=0,
    override var nearPlaces: @RawValue ArrayList<INearPlace>? = null
) : IActivity(), Parcelable