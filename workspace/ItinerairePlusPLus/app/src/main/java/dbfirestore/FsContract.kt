package dbfirestore

import android.provider.BaseColumns


object FsContract {
    /* Inner class that defines the tables contents */
    class FsUser : BaseColumns {
        companion object {
            val TABLE_USER= "user"
            val COLUMN_ID = "id"
            val COLUMN_NAME = "name"
            val COLUMN_ADDRESS = "address"
            val COLUMN_CITY = "city"
            val COLUMN_STATE = "state"
            val COLUMN_ZIP = "zip"
            val COLUMN_COUNTRY = "country"
            val COLUMN_ROLE_ID= "role"
        }
    }
    class FsRole : BaseColumns {
        companion object {
            val TABLE_ROLE= "role"
            val COLUMN_ID = "id"
            val COLUMN_ROLE = "role"
        }
    }
}