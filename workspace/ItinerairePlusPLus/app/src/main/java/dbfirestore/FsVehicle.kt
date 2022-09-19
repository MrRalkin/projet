package dbfirestore

import android.os.Parcelable
import interfaces.user.IVehicle
import kotlinx.parcelize.Parcelize

@Parcelize
class FsVehicle(
    override var type: String = "",
    override var energy: String = "",
    override var distance: Int = 0,
    override var mesure: String = "",
    override var capacity: Int = 0,
    override var unit: String = ""
) : IVehicle(), Parcelable