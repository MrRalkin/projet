package dbfirestore

import android.provider.BaseColumns


object FsContract {
    /* Inner class that defines the tables contents */
    class TbUser : BaseColumns {
        companion object {
            val COLLECTION_NAME = "users"
            val FD_ID = "id"
            val FD_NAME = "name"
            val FD_EMAIL = "email"
            val FD_ADDRESS = "address"
            val FD_DESTINATIONS = "destinations"
            val FD_ROLE = "role"
            val FD_SETTINGS = "settings"

        }
    }

    class TbRole : BaseColumns {
        companion object {
            val COLLECTION_NAME = "roles"
            val FD_ID = "id"
            val FD_ROLE = "role"
        }
    }

    class TbAddress : BaseColumns {
        companion object {
            val TB_NAME = "address"
            val FD_ADDRESS = "address"
            val FD_CITY = "city"
            val FD_STATE = "state"
            val FD_ZIP = "zip"
            val FD_COUNTRY = "country"
            val FD_EMAIL = "email"
            val FD_ROLE_ID = "role"
        }
    }

    class TbCoord : BaseColumns {
        companion object {
            val FD_LONGITUDE = "longitude"
            val FD_LATITUDE = "latitude"
        }
    }

    class TbPoint : BaseColumns {
        companion object {
            val FD_NAME = "name"
            val FD_COORD = "coord"
            val FD_ADDRESS = "address"
        }
    }

    class TbActivity : BaseColumns {
        companion object {
            val FD_ACTIVITY = "activity"
            val FD_NAME = "name"
            val FD_TIME = "time"
            val FD_DURATION = "duration"
            val FD_NEARPLACES="nearPlaces"
        }
    }


    class TbNearPlace:BaseColumns{
        companion object{
            val FD_BUSINESS_STATUS="business_status"
            val FD_LOCATION="location"
            val FD_NORTHEAST="northeast"
            val FD_SOUTHWEST="southwest"
            val FD_ICON="icon"
            val FD_NAME="name"
            val FD_TYPE="type"
            val FD_VICINITY="vicinity"
            val FD_DISTANCE="distance"
            val FD_STEP="step"
        }
    }

    class TbStep : BaseColumns {
        companion object {
            val FD_STEP = "step"
            val FD_START = "start"
            val FD_END = "end"
            val FD_TRIP_TIME = "trip_time"
            val FD_ACTIVITIES = "activities"
        }
    }

    class TbDestination : BaseColumns {
        companion object {
            val FD_ID = "destinationId"
            val FD_NAME = "name"
            val FD_COORD_DEPART = "coordDepart"
            val FD_COORD_DEST = "coordDestination"
            val FD_ADDRESS_DEPART = "addressDepart"
            val FD_ADDRESS_DEST = "addressDestination"
            val FD_IMAGE = "image"
            val FD_TRIP_TIME = "trip_time"
            val FD_TRIP_METERS = "trip_meters"
            val FD_STEPS = "steps"
            val FD_SETTINGS = "settings"
        }
    }

    class TbVehicle : BaseColumns {
        companion object {
            val FD_TYPE = "type"
            val FD_ENERGY = "energy"
            val FD_DISTANCE = "distance"
            val FD_MESURE = "mesure"
            val FD_CAPACITY = "capacity"
            val FD_UNIT = "unit"
        }
    }

    class TbEnergy : BaseColumns {
        companion object {
            val FD_TYPE = "type"
            val FD_PRICE = "price"
            val FD_UNIT = "unit"
        }
    }

    class TbSettings : BaseColumns {
        companion object {
            val FD_VEHICLES = "vehicles"
            val FD_ACTIVITIES = "activities"
            val FD_ENERGIES = "energies"
        }
    }

    class TbPredefinedDestination : BaseColumns {
        companion object {
            val COLLECTION_NAME = "pred_destinations"
            val FD_NAME = "name"
            val FD_DESCRIPTION = "description"
            val FD_COORD = "coord"
            val FD_ADDRESS = "address"
            val FD_IMAGE = "image"
        }
    }

    class TbMapRawData : BaseColumns {
        companion object {
            val COLLECTION_NAME = "map_row_datas"
            val FD_ID = "destinationId"
            val FD_CREATED = "created"
            val FD_RAW_DATA = "raw_data"
        }
    }
}