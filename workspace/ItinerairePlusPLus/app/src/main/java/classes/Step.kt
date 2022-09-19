package classes

import android.os.Parcelable
import dbfirestore.FsPoint
import interfaces.user.IActivity
import interfaces.user.IPoint
import interfaces.user.IStep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class Step(
    override var step: Int = 0,
    override var start: @RawValue IPoint?= FsPoint(),
    override var end: @RawValue IPoint? = FsPoint(),
    override var trip_time: Int = 0,
    override var activities: @RawValue ArrayList<IActivity>? = ArrayList<IActivity>()
) : IStep(), Parcelable
