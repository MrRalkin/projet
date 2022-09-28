package classes

import dbfirestore.FsDataManager
import interfaces.user.IDataManager

class AppGlobal private constructor() {

    val userManager: IDataManager = FsDataManager()

    companion object {
        val instance = AppGlobal()
    }
}