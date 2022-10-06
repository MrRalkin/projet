package dbfirestore

import android.os.Parcelable
import classes.settings.Coord
import interfaces.user.ICoord
import interfaces.user.INearPlace
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
@Parcelize
class FsNearPlace (
    override var business_status: String = "",
    override var location: @RawValue ICoord?= Coord(),
    override var northeast:@RawValue ICoord?= Coord(),
    override var southwest:@RawValue ICoord?= Coord(),
    override var icon: String = "",
    override var name: String = "",
    override var type: String = "",
    override var vicinity: String = "",
    override var distance: Int = 0,
    override var step: Int = 0
    ): INearPlace(), Parcelable