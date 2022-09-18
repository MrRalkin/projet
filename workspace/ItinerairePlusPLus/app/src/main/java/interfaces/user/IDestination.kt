package interfaces.user

interface ICoord {
    var longitude: String
    var latitude: String
}

interface IAddress {
    var address: String
    var city: String
    var state: String
    var zip: String
    var country: String
}

interface IPoint {
    var name: String
    var coord: ICoord
    var address: IAddress
}


interface IActivity {
    var activity: Int
    var name: String
    var time: Int
}

interface IStep {
    var step: Int
    var start: IPoint
    var end: IPoint
    var trip_time: Int
    var activities: ArrayList<IActivity>
}

interface IDestination {
    var name: String
    var coord: ICoord
    var address: IAddress
    var image: String
    var trip_time: Int
    var steps: ArrayList<IStep>
}


