package classes.settings

import android.os.Parcelable
import interfaces.user.IActivity
import interfaces.user.IEnergy
import interfaces.user.ISettings
import interfaces.user.IVehicle
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class Settings(
    override var vehicles: @RawValue ArrayList<IVehicle> = ArrayList<IVehicle>(),
    override var activities: @RawValue ArrayList<IActivity> = ArrayList<IActivity>(),
    override var energies: @RawValue ArrayList<IEnergy> = ArrayList<IEnergy>()
) : ISettings(), Parcelable