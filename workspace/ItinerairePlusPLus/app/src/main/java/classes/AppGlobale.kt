package classes

import android.media.audiofx.Equalizer.Settings
import classes.settings.Destination
import dbfirestore.FsDataManager
import interfaces.user.IDataManager
import interfaces.user.IDestination
import interfaces.user.ISettings
import classes.settings.Settings as destSettings

class AppGlobal private constructor() {

    val ACTIVITY_ESSENCE = "Essence"
    val ACTIVITY_DORMIR = "Dormir"
    val ACTIVITY_MANGER = "Manger"
    val ACTIVITY_RECHARGE = "Recharge"

    val ENERGY_ESSENCE = "essence"
    val ENERGY_ELECTRICITE = "electricite"

    val VEHICLE_ESSENCE = ENERGY_ESSENCE
    val VEHICLE_ELECTRIQUE = ENERGY_ELECTRICITE


    val userManager: IDataManager = FsDataManager()
    var curSetting: ISettings = destSettings()
    var curDestanation: IDestination = Destination()
    var departAddress: String = ""
    var destAddress: String = ""

    companion object {
        val instance = AppGlobal()

    }
}