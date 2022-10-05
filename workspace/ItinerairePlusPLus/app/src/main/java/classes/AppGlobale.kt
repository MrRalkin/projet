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

    val ACTIVITY_ESSENCE_TYPE = "gas_station"
    val ACTIVITY_DORMIR_TYPE = "lodging"
    val ACTIVITY_MANGER_TYPE  = "restaurant"
    val ACTIVITY_RECHARGE_TYPE  = "gas_station"

    val ENERGY_ESSENCE = "essence"
    val ENERGY_ELECTRICITE = "electricite"
    val VEHICLE_ESSENCE = ENERGY_ESSENCE
    val VEHICLE_ELECTRIQUE = ENERGY_ELECTRICITE


    val userManager: IDataManager = FsDataManager()
    var curSetting: ISettings = destSettings()
    var curDestination: IDestination = Destination()
    var departAddress: String = ""
    var destAddress: String = ""

    companion object {
        val instance = AppGlobal()
    }
}