package interfaces.user

abstract class IPredefinedDestination(
    open var name: String = "",
    open var description:String="",
    open var coord:  ICoord? = null,
    open var address:IAddress? = null,
    open var image:String=""

)