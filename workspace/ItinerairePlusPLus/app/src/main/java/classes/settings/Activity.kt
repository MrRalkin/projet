package classes.settings

import android.os.Parcelable
import interfaces.user.IActivity
import kotlinx.parcelize.Parcelize

@Parcelize
class Activity(
    override var activity: Int = 0,
    override var name: String = "",
    override var time: Int = 0
) : IActivity(), Parcelable