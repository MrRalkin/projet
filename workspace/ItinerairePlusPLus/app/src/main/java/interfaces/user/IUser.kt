package interfaces.user

interface IUser {
    var id: String
    var name: String
    var address: String
    var city: String
    var state: String
    var postalCode: String
    var country: String
    var email: String
    var role_id: Int
    var destinations:ArrayList<IDestination>?
}