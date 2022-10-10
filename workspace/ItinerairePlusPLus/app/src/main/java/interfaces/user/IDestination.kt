package interfaces.user

abstract class ICoord(
    open var longitude: String = "",
    open var latitude: String = ""
)

abstract class IAddress(
    open var address: String = "",
    open var city: String = "",
    open var state: String = "",
    open var zip: String = "",
    open var country: String = "",
)

abstract class IPoint(
    open var name: String = "",
    open var coord: ICoord? = null,
    open var address: IAddress? = null
)

abstract class INearPlace(
    open var business_status: String = "",
    open var location: ICoord? = null,
    open var northeast: ICoord? = null,
    open var southwest: ICoord? = null,
    open var icon: String = "",
    open var name: String = "",
    open var type: String = "",
    open var vicinity: String = "",
    open var distance: Int = 0,
    open var step: Int = 0
)

abstract class IActivity(
    open var activity: Int = 0,
    open var name: String = "",
    open var time: Int = 0,
    open var duration: Int = 0,
    open var nearPlaces: ArrayList<INearPlace>? = null
)

abstract class IStep(
    open var step: Int = 0,
    open var start: IPoint? = null,
    open var end: IPoint? = null,
    open var trip_time: Int = 0,
    open var activities: ArrayList<IActivity>? = null
)

abstract class IDestination(
    open var destinationId: String = "",
    open var name: String = "",
    open var coordDepart: ICoord? = null,
    open var coordDestination: ICoord? = null,
    open var addressDepart: IAddress? = null,
    open var addressDestination: IAddress? = null,
    open var image: String = "",
    open var trip_time: Int = 0,
    open var trip_meters: Int = 0,
    open var steps: ArrayList<IStep>? = null,
    open var settings: ISettings? = null
)


