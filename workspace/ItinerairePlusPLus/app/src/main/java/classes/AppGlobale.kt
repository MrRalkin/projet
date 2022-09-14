package classes

import dbfirestore.FsUserManager
import interfaces.IDBOperation
import interfaces.user.IUserManager

class AppGlobal private constructor() {

    val userManager: IUserManager = FsUserManager()

    companion object {
        val instance = AppGlobal()
    }
}