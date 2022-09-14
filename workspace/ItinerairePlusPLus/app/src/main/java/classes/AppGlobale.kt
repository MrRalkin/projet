package classes

import dbfirestore.FsOperations
import interfaces.IDBOperation

class AppGlobal private constructor() {

    val database: IDBOperation = FsOperations()

    companion object {
        val instance = AppGlobal()
    }
}