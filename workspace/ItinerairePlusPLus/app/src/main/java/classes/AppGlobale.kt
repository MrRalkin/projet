package classes

import android.media.audiofx.Equalizer.Settings
import dbfirestore.FsDataManager
import interfaces.user.IDataManager
import interfaces.user.ISettings
import classes.settings.Settings as destSettings

class AppGlobal private constructor() {

    val userManager: IDataManager = FsDataManager()
    var curSetting: ISettings = destSettings()
    var departAddress: String = ""
    var destAddress: String = ""

    companion object {
        val instance = AppGlobal()

    }
}