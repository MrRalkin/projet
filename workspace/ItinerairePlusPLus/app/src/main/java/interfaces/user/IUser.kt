package interfaces.user

interface IUser {
    var id: String
    var name: String
    var address:IAddress
    var email: String
    var role_id: Int
    var destinations:ArrayList<IDestination>?
    var settings:ISettings
}