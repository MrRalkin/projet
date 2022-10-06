package dbfirestore

import classes.ActionResult
import classes.AppGlobal
import classes.settings.Activity
import classes.settings.Destination
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import interfaces.auth.ILogin
import interfaces.auth.IRegister
import interfaces.user.*
import kotlinx.coroutines.tasks.await

class FsDataManager : IDataManager {
    private fun dbInit() {

    }

    override suspend fun userIsAuthenticated(): Boolean {

        if (mAuth.currentUser != null) {
            return true
        }
        return false
    }

    override suspend fun userRegister(user: IRegister): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_REGISTERED, "")
        try {
            var roles = rolesGet()
            var roleId = 0
            if (roles.count() > 1) {
                roleId = roles.get(roles.count() - 1).id
            }

            var rsp: AuthResult =
                mAuth.createUserWithEmailAndPassword(user.email, user.password).await()

            val newUser = FsUser(
                rsp.user!!.uid.toString(), user.name, FsAddress(), user.email, roleId,
                ArrayList<IDestination>(), getSettingsDefault()
            )
            db.collection(FsContract.TbUser.COLLECTION_NAME).document(newUser.id).set(newUser)
            curUser = newUser
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun userLogin(user: ILogin): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_REGISTERED, "")
        try {
            var rsp: AuthResult =
                mAuth.signInWithEmailAndPassword(user.email, user.password).await()

            if (rsp.user != null) {
                return result
            } else {
                result.isSuccess = false
                result.errorMessage = MESSAGE_USER_LOGIN_ERROR
            }


        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun userLogout(): ActionResult {
        mAuth.signOut()
        return ActionResult(true, MESSAGE_USER_SIGNOUT, "")
    }

    override suspend fun userGetByEmail(email: String): IUser? {
        try {
            var snp: QuerySnapshot = db.collection(FsContract.TbUser.COLLECTION_NAME)
                .whereEqualTo(FsContract.TbUser.FD_EMAIL, email).get().await()

            for (item in snp.documents) {
                if (item != null) {
                    item.toObject(FsUser::class.java)
                        ?.let {
                            curUser = it
                            return curUser
                        }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }

    override suspend fun userGetById(id: String): IUser? {
        try {

            var snp: DocumentSnapshot? =
                db.collection(FsContract.TbUser.COLLECTION_NAME).document(id).get().await()
            //.whereEqualTo(FsContract.FsUser.COLUMN_EMAIL,email).get().await()
            val user: IUser = FsUser()
            //for (item in snp.documents) {
            if (snp != null) {

                user.id = id
                user.email = snp[FsContract.TbUser.FD_EMAIL].toString()
                user.name = snp[FsContract.TbUser.FD_NAME].toString()
                user.address = getAddress(snp[FsContract.TbAddress.TB_NAME] as HashMap<String, Any>)

                val items =
                    snp[FsContract.TbUser.FD_DESTINATIONS] as ArrayList<HashMap<String, Destination>>

                val destinations = ArrayList<IDestination>()
                for (dest in items) {
                    destinations.add(getDestination(dest as HashMap<String, Any>))
                }
                user.destinations = destinations
                user.settings =
                    getSettings(snp[FsContract.TbUser.FD_SETTINGS] as HashMap<String, Any>)
            }
            return user

        } catch (e: Exception) {
            println(e.message)
            mAuth.signOut()
        }
        return null
    }

    override suspend fun userGetCurrent(): IUser? {
        val credential = mAuth.currentUser

        if (credential != null) {
            if (curUser != null) {
                return curUser
            } else {
                curUser = userGetById(credential.uid)
                return curUser
            }
        }

        return null
    }

    override suspend fun userUpdateCurrent(user: IUser): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_UPDATED, "")
        try {
            db.collection(FsContract.TbUser.COLLECTION_NAME).document(user.id).set(user)
            curUser = user
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun userUpdate(user: IUser): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_UPDATED, "")
        try {
            db.collection(FsContract.TbUser.COLLECTION_NAME).document(user.id).set(user)
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun userDelete(user: IUser): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_DELETED, "")
        try {
            db.collection(FsContract.TbUser.COLLECTION_NAME).document(user.id).delete()
            curUser = user
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun rolesGet(): ArrayList<IRole> {
        if (roles.count() > 0) {
            return roles
        }

        try {
            var snp: QuerySnapshot =
                db.collection(FsContract.TbRole.COLLECTION_NAME).get().await()
            //.whereEqualTo(FsContract.FsRole.COLUMN_ID, id).get().await()

            for (item in snp.documents) {
                if (item != null) {
                    item.toObject(FsRole::class.java)
                        ?.let {
                            roles.add(it)

                        }
                }
            }
            if (roles.count() > 0) {
                return roles
            }
            val roleSSN: IRole = FsRole(1, "SSN")
            val roleUser: IRole = FsRole(2, "User")
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(roleSSN.id.toString())
                .set(roleSSN)
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(roleUser.id.toString())
                .set(roleUser)
            roles.add(roleSSN)
            roles.add(roleUser)

        } catch (e: Exception) {
            println(e.message)
        }
        return roles
    }

    override suspend fun roleGetByName(name: String): IRole? {
        var role: IRole
        try {
            var snp: QuerySnapshot = db.collection(FsContract.TbRole.COLLECTION_NAME)
                .whereEqualTo(FsContract.TbRole.FD_ROLE, name).get().await()

            for (item in snp.documents) {
                if (item != null) {
                    item.toObject(FsRole::class.java)
                        ?.let {
                            role = it
                            return role
                        }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }

    override suspend fun roleGetById(id: String): IRole? {
        var role: IRole
        try {
            var snp: QuerySnapshot = db.collection(FsContract.TbRole.COLLECTION_NAME)
                .whereEqualTo(FsContract.TbRole.FD_ID, id).get().await()

            for (item in snp.documents) {
                if (item != null) {
                    item.toObject(FsRole::class.java)
                        ?.let {
                            role = it
                            return role
                        }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }

    override suspend fun roleDelete(role: IRole): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_DELETED, "")
        try {
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(role.id.toString())
                .delete()
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun roleAdd(role: IRole): ActionResult {
        var result = ActionResult(true, MESSAGE_ROLE_CREATED, "")
        try {
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(role.id.toString())
                .set(role)
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun roleUpdate(role: IRole): ActionResult {
        var result = ActionResult(true, MESSAGE_ROLE_UPDATED, "")
        try {
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(role.id.toString())
                .set(role)
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun roleAssign(user: IUser, role: IRole): ActionResult {
        var result = ActionResult(true, MESSAGE_ROLE_ASSIGNED, "")
        try {
            user.role_id = role.id
            db.collection(FsContract.TbUser.COLLECTION_NAME).document(user.email).set(user)
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun getPredefinedDestinations(): ArrayList<IPredefinedDestination> {
        var destinations = ArrayList<IPredefinedDestination>()

        try {
            var snp: QuerySnapshot =
                db.collection(FsContract.TbPredefinedDestination.COLLECTION_NAME).get().await()

            for (item in snp.documents) {
                if (item != null) {
                    var dest: IPredefinedDestination = FsPredefinedDestination()
                    dest.name = item[FsContract.TbPredefinedDestination.FD_NAME].toString()
                    dest.description =
                        item[FsContract.TbPredefinedDestination.FD_DESCRIPTION].toString()
                    dest.coord =
                        getCoord(item[FsContract.TbPredefinedDestination.FD_COORD] as HashMap<String, Any>)
                    dest.address =
                        getAddress(item[FsContract.TbPredefinedDestination.FD_ADDRESS] as HashMap<String, Any>)
                    dest.image = item[FsContract.TbPredefinedDestination.FD_IMAGE].toString()
                    destinations.add(dest)
                }
            }

        } catch (e: Exception) {
            println(e.message)
        }
        return destinations
    }

    override suspend fun setPredefinedDestinations(destinations: ArrayList<IPredefinedDestination>): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_UPDATED, "")
        try {

            for (dest in destinations) {
                db.collection(FsContract.TbPredefinedDestination.COLLECTION_NAME)
                    .document(dest.name).set(dest)
            }

        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    private fun getDestination(item: HashMap<String, Any>): FsDestination {
        var result = FsDestination()
        try {
            result.destinationId = item[FsContract.TbDestination.FD_ID].toString()
            result.name = item[FsContract.TbDestination.FD_NAME].toString()
            result.addressDepart =
                getAddress(item[FsContract.TbDestination.FD_COORD_DEPART] as HashMap<String, Any>)
            result.addressDestination =
                getAddress(item[FsContract.TbDestination.FD_COORD_DEST] as HashMap<String, Any>)
            result.coordDepart =
                getCoord(item[FsContract.TbDestination.FD_COORD_DEPART] as HashMap<String, Any>)
            result.coordDestination =
                getCoord(item[FsContract.TbDestination.FD_COORD_DEST] as HashMap<String, Any>)
            result.image = item[FsContract.TbDestination.FD_IMAGE].toString()
            result.trip_time = item[FsContract.TbDestination.FD_TRIP_TIME].toString().toInt()
            val items =
                item[FsContract.TbDestination.FD_STEPS] as ArrayList<HashMap<String, FsStep>>

            var steps = ArrayList<IStep>()
            for (stp in items) {
                steps.add(getStep(stp as HashMap<String, Any>))
            }
            result.steps = steps

        } catch (e: Exception) {
            println(e.message)
        }
        return result
    }

    private fun getStep(item: HashMap<String, Any>): FsStep {
        var result = FsStep()
        try {
            result.step = item[FsContract.TbStep.FD_STEP].toString().toInt()
            result.start = getPoint(item[FsContract.TbStep.FD_START] as HashMap<String, Any>)
            result.end = getPoint(item[FsContract.TbStep.FD_END] as HashMap<String, Any>)
            result.trip_time = item[FsContract.TbStep.FD_TRIP_TIME].toString().toInt()
            val items =
                item[FsContract.TbStep.FD_ACTIVITIES] as ArrayList<HashMap<String, Activity>>

            var activities = ArrayList<IActivity>()
            for (act in items) {
                activities.add(getActivity(act as HashMap<String, Any>))
            }
            result.activities = activities
        } catch (e: Exception) {
            println(e.message)
        }

        return result
    }

    private fun getPoint(item: HashMap<String, Any>): FsPoint {
        var result = FsPoint()
        try {
            result.name = item[FsContract.TbPoint.FD_NAME].toString()
            result.coord = getCoord(item[FsContract.TbPoint.FD_COORD] as HashMap<String, Any>)
            result.address = getAddress(item[FsContract.TbPoint.FD_ADDRESS] as HashMap<String, Any>)

        } catch (e: Exception) {
            println(e.message)
        }

        return result
    }

    private fun getActivity(item: HashMap<String, Any>): FsActivity {
        var result = FsActivity()
        try {
            result.activity = item[FsContract.TbActivity.FD_ACTIVITY].toString().toInt()
            result.name = item[FsContract.TbActivity.FD_NAME].toString()
            result.time = item[FsContract.TbActivity.FD_TIME].toString().toInt()
            result.duration = item[FsContract.TbActivity.FD_DURATION].toString().toInt()

        } catch (e: Exception) {
            println(e.message)
        }

        return result
    }

    private fun getCoord(item: HashMap<String, Any>): FsCoord {
        var result = FsCoord()
        try {
            result.latitude = item[FsContract.TbCoord.FD_LATITUDE].toString()
            result.longitude = item[FsContract.TbCoord.FD_LATITUDE].toString()
        } catch (e: Exception) {
            println(e.message)
        }

        return result
    }

    private fun getAddress(item: HashMap<String, Any>): FsAddress {
        var result = FsAddress()
        try {
            result.address = item[FsContract.TbAddress.FD_ADDRESS].toString()
            result.city = item[FsContract.TbAddress.FD_CITY].toString()
            result.state = item[FsContract.TbAddress.FD_STATE].toString()
            result.zip = item[FsContract.TbAddress.FD_ZIP].toString()
            result.country = item[FsContract.TbAddress.FD_COUNTRY].toString()

        } catch (e: Exception) {
            println(e.message)
        }

        return result
    }

    private fun getVehicle(item: HashMap<String, Any>): FsVehicle {
        var result = FsVehicle()
        try {
            result.type = item[FsContract.TbVehicle.FD_TYPE].toString()
            result.energy = item[FsContract.TbVehicle.FD_ENERGY].toString()
            result.distance = item[FsContract.TbVehicle.FD_DISTANCE].toString().toInt()
            result.mesure = item[FsContract.TbVehicle.FD_MESURE].toString()
            result.capacity = item[FsContract.TbVehicle.FD_CAPACITY].toString().toInt()
            result.unit = item[FsContract.TbVehicle.FD_UNIT].toString()
        } catch (e: Exception) {
            println(e.message)
        }
        return result
    }

    private fun getEnergy(item: HashMap<String, Any>): FsEnergy {
        var result = FsEnergy()
        try {
            result.type = item[FsContract.TbEnergy.FD_TYPE].toString()
            result.price = item[FsContract.TbEnergy.FD_PRICE].toString().toDouble()
            result.unit = item[FsContract.TbEnergy.FD_UNIT].toString()

        } catch (e: Exception) {
            println(e.message)
        }
        return result
    }

    override suspend fun resetSettingsToDefault(): ActionResult {
        var result = ActionResult(true, MESSAGE_RESET_SETTING_SUCCESS, "")

        var settings = getSettingsDefault()
        if (curUser != null) {
            curUser!!.settings = settings
        }

        return userUpdateCurrent(curUser!!)
    }

    override suspend fun setMapRawData(rawData: IMapRawData): ActionResult {
        var result = ActionResult(true, MESSAGE_SET_MAP_RAW_DATA, "")
        try {
            db.collection(FsContract.TbMapRawData.COLLECTION_NAME).document(rawData.destinationId)
                .set(rawData)
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun getMapRawData(id: String): IMapRawData {
        var result: IMapRawData = FsMapRawData("", 0, "")
        try {
            var snp: DocumentSnapshot? =
                db.collection(FsContract.TbMapRawData.COLLECTION_NAME).document(id).get().await()

            if (snp != null) {
                result.destinationId = id
                result.rawData = snp[FsContract.TbMapRawData.FD_RAW_DATA].toString()
                result.created = snp[FsContract.TbMapRawData.FD_CREATED].toString().toLong()
            }

        } catch (e: Exception) {
            println(e.message)
        }
        return result
    }

    override suspend fun delMapRawData(id: String): ActionResult {
        var result = ActionResult(true, MESSAGE_DELETE_MAP_RAW_DATA, "")
        try {
            db.collection(FsContract.TbMapRawData.COLLECTION_NAME).document(id).delete()
        } catch (e: Exception) {
            println(e.message)
            result.isSuccess = false
            result.errorMessage = e.message.toString()
        }
        return result
    }

    override suspend fun delExpiredRawData() {
        TODO("Not yet implemented")
    }

    private fun getSettings(item: HashMap<String, Any>): FsSettings {
        var result = FsSettings()
        result.vehicles = ArrayList<IVehicle>()
        result.activities = ArrayList<IActivity>()
        result.energies = ArrayList<IEnergy>()
        try {
            if (item[FsContract.TbSettings.FD_VEHICLES] != null) {
                for (vcl in item[FsContract.TbSettings.FD_VEHICLES] as ArrayList<HashMap<String, FsVehicle>>) {
                    result.vehicles.add(getVehicle(vcl as HashMap<String, Any>))
                }
            }
            if (item[FsContract.TbSettings.FD_ENERGIES] != null) {
                for (enr in item[FsContract.TbSettings.FD_ENERGIES] as ArrayList<HashMap<String, FsEnergy>>) {
                    result.energies.add(getEnergy(enr as HashMap<String, Any>))
                }
            }
            if (item[FsContract.TbSettings.FD_ACTIVITIES] != null) {
                for (act in item[FsContract.TbSettings.FD_ACTIVITIES] as ArrayList<HashMap<String, FsActivity>>) {
                    result.activities.add(getActivity(act as HashMap<String, Any>))
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return result
    }

    private fun getSettingsDefault(): ISettings {
        val appGlobal = AppGlobal.instance
        var result: ISettings = FsSettings()

        result.energies = ArrayList<IEnergy>()
        result.activities = ArrayList<IActivity>()
        result.vehicles = ArrayList<IVehicle>()

        result.vehicles.add(FsVehicle("Auto gas", appGlobal.VEHICLE_ESSENCE, 600, "km", 55, "litre"))
        result.vehicles.add(FsVehicle("Auto electric", appGlobal.VEHICLE_ELECTRIQUE, 600, "km", 100, "kWh"))
//        result.vehicles.add(FsVehicle("Vélo", "nourriture", 50, "km", 2, ""))

        result.activities.add(FsActivity(1, appGlobal.ACTIVITY_ESSENCE, 14400, 900))
        result.activities.add(FsActivity(2, appGlobal.ACTIVITY_RECHARGE, 14400,7200))
        result.activities.add(FsActivity(3, appGlobal.ACTIVITY_DORMIR, 28800, 21600))
        result.activities.add(FsActivity(4, appGlobal.ACTIVITY_MANGER, 14400,3600))
//        result.activities.add(FsActivity(5, "Touristique", 14400,3600))

        result.energies.add(FsEnergy(appGlobal.ENERGY_ESSENCE, 1.55, "litre"))
        result.energies.add(FsEnergy(appGlobal.ENERGY_ELECTRICITE, 0.047, "kWh"))
//        result.energies.add(FsEnergy("nourriture", 25.0, "repas"))
        return result
    }

    companion object {
        val db = Firebase.firestore
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        private var roles = ArrayList<IRole>()
        private var curUser: IUser? = null
        private val MESSAGE_USER_REGISTERED = "L'utilisateur a été créé."
        private val MESSAGE_USER_SIGNIN = "L'utilisateur est connecté."
        private val MESSAGE_USER_SIGNOUT = "L'utilisateur est déconnecté."
        private val MESSAGE_USER_LOGIN_ERROR = "Connexion impossible"
        private val MESSAGE_USER_UPDATED = "L'utilisateur a été mis-à-jour."
        private val MESSAGE_USER_DELETED = "L'utilisateur a été supprimé."
        private val MESSAGE_ROLE_CREATED = "Le rôle a été créé."
        private val MESSAGE_ROLE_DELETED = "Le rôle a été supprimé."
        private val MESSAGE_ROLE_UPDATED = "Le rôle a été mis-à-jour."
        private val MESSAGE_ROLE_ASSIGNED = "Le rôle a été assigné."
        private val MESSAGE_RESET_SETTING_SUCCESS = "Configuration par défaut mis-à-jour."
        private val MESSAGE_SET_MAP_RAW_DATA = "Le map data a été suvgardé"
        private val MESSAGE_DELETE_MAP_RAW_DATA = "Le map data a été supprimé"
    }
}