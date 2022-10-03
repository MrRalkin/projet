package dbfirestore

import android.os.Parcelable
import interfaces.user.IActivity
import kotlinx.parcelize.Parcelize

@Parcelize
class FsActivity(
    override var activity: Int = 0,
    override var name: String = "",
    override var time: Int = 0,
    override var duration:Int=0
) : IActivity(), Parcelable