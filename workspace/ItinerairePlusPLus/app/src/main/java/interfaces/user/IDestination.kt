package interfaces.user

interface ICoord {
    var longitude: String
    var latitude: String
}

interface IPoint {
    var name: String
    var coord: ICoord
    var adresse: String
    var city: String
    var state: String
    var zip: String
    var country: String
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
    var tripTime: Int
    var activities: ArrayList<IActivity>
}

interface IDestination {
    var name: String
    var coord: ICoord
    var adresse: String
    var city: String
    var province: String
    var postal_code: String
    var country: String
    var image: String
    var tripTime: Int
    var steps: ArrayList<IStep>
}


