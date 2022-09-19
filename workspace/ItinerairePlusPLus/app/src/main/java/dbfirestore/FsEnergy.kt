package dbfirestore

import android.os.Parcelable
import interfaces.user.IEnergy

import kotlinx.parcelize.Parcelize

@Parcelize
class FsEnergy(
    override var type: String = "",
    override var price: Double = .0,
    override var unit: String = "",
) : IEnergy(), Parcelable
