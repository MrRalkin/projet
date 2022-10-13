package dbfirestore

import android.provider.BaseColumns


object FsContract {
    class TbUser : BaseColumns {
        companion object {
            const val COLLECTION_NAME = "users"
            const val FD_ID = "id"
            const val FD_NAME = "name"
            const val FD_EMAIL = "email"
            const val FD_ADDRESS = "address"
            const val FD_DESTINATIONS = "destinations"
            const val FD_ROLE = "role"
            const val FD_SETTINGS = "settings"
        }
    }

    class TbRole : BaseColumns {
        companion object {
            const val COLLECTION_NAME = "roles"
            const val FD_ID = "id"
            const val FD_ROLE = "role"
        }
    }

    class TbAddress : BaseColumns {
        companion object {
            const val TB_NAME = "address"
            const val FD_ADDRESS = "address"
            const val FD_CITY = "city"
            const val FD_STATE = "state"
            const val FD_ZIP = "zip"
            const val FD_COUNTRY = "country"
            const val FD_EMAIL = "email"
            const val FD_ROLE_ID = "role"
        }
    }

    class TbCoord : BaseColumns {
        companion object {
            const val FD_LONGITUDE = "longitude"
            const val FD_LATITUDE = "latitude"
        }
    }

    class TbPoint : BaseColumns {
        companion object {
            const val FD_NAME = "name"
            const val FD_COORD = "coord"
            const val FD_ADDRESS = "address"
        }
    }

    class TbActivity : BaseColumns {
        companion object {
            const val FD_ACTIVITY = "activity"
            const val FD_NAME = "name"
            const val FD_TIME = "time"
            const val FD_DURATION = "duration"
            const val FD_NEARPLACES="nearPlaces"
        }
    }

    class TbNearPlace:BaseColumns{
        companion object{
            const val FD_BUSINESS_STATUS="business_status"
            const val FD_LOCATION="location"
            const val FD_NORTHEAST="northeast"
            const val FD_SOUTHWEST="southwest"
            const val FD_ICON="icon"
            const val FD_NAME="name"
            const val FD_TYPE="type"
            const val FD_VICINITY="vicinity"
            const val FD_DISTANCE="distance"
            const val FD_STEP="step"
        }
    }

    class TbStep : BaseColumns {
        companion object {
            const val FD_STEP = "step"
            const val FD_START = "start"
            const val FD_END = "end"
            const val FD_TRIP_TIME = "trip_time"
            const val FD_ACTIVITIES = "activities"
        }
    }

    class TbDestination : BaseColumns {
        companion object {
            const val FD_ID = "destinationId"
            const val FD_NAME = "name"
            const val FD_COORD_DEPART = "coordDepart"
            const val FD_COORD_DEST = "coordDestination"
            const val FD_ADDRESS_DEPART = "addressDepart"
            const val FD_ADDRESS_DEST = "addressDestination"
            const val FD_IMAGE = "image"
            const val FD_TRIP_TIME = "trip_time"
            const val FD_TRIP_METERS = "trip_meters"
            const val FD_STEPS = "steps"
            const val FD_SETTINGS = "settings"
        }
    }

    class TbVehicle : BaseColumns {
        companion object {
            const val FD_TYPE = "type"
            const val FD_ENERGY = "energy"
            const val FD_DISTANCE = "distance"
            const val FD_MESURE = "mesure"
            const val FD_CAPACITY = "capacity"
            const val FD_UNIT = "unit"
        }
    }

    class TbEnergy : BaseColumns {
        companion object {
            const val FD_TYPE = "type"
            const val FD_PRICE = "price"
            const val FD_UNIT = "unit"
        }
    }

    class TbSettings : BaseColumns {
        companion object {
            const val FD_VEHICLES = "vehicles"
            const val FD_ACTIVITIES = "activities"
            const val FD_ENERGIES = "energies"
        }
    }

    class TbPredefinedDestination : BaseColumns {
        companion object {
            const val COLLECTION_NAME = "pred_destinations"
            const val FD_NAME = "name"
            const val FD_DESCRIPTION = "description"
            const val FD_COORD = "coord"
            const val FD_ADDRESS = "address"
            const val FD_IMAGE = "image"
        }
    }

    class TbMapRawData : BaseColumns {
        companion object {
            const val COLLECTION_NAME = "map_row_datas"
            const val FD_ID = "destinationId"
            const val FD_CREATED = "created"
            const val FD_RAW_DATA = "raw_data"
        }
    }
}