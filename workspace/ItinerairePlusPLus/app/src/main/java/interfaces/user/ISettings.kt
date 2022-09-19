package interfaces.user

abstract class IVehicle(
    open var type: String = "",
    open var energy: String = "",
    open var distance: Int = 0,
    open var mesure: String = "",
    open var capacity: Int = 0,
    open var unit: String = "",
)

abstract class IEnergy(
    open var type: String = "",
    open var price: Double = .0,
    open var unit: String = "",
)

abstract class ISettings(
    open var vehicles: ArrayList<IVehicle> = ArrayList<IVehicle>(),
    open var activities: ArrayList<IActivity> = ArrayList<IActivity>(),
    open var energies: ArrayList<IEnergy> = ArrayList<IEnergy>()
)