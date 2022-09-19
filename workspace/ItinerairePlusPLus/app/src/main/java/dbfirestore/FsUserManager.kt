package dbfirestore

import classes.ActionResult
import classes.Activity
import classes.Destination
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

class FsUserManager : IUserManager {
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
                ArrayList<IDestination>()
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
//                val sf=s
//                if (snp[FsContract.TbUser.FD_DESTINATIONS] != null) {
//                  var destList= snp.get(FsContract.TbUser.FD_DESTINATIONS) as HashMap<String,Any>
//               var t=destList
//
//                }


            }
            return user

        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }

    fun getDestination(item: HashMap<String, Any>): FsDestination {
        var result = FsDestination()
        try {
            result.name = item[FsContract.TbDestination.FD_NAME].toString()
            result.address =
                getAddress(item[FsContract.TbDestination.FD_ADDRESS] as HashMap<String, Any>)
            result.coord = getCoord(item[FsContract.TbDestination.FD_COORD] as HashMap<String, Any>)
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

    fun getStep(item: HashMap<String, Any>): FsStep {
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

    fun getPoint(item: HashMap<String, Any>): FsPoint {
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

    fun getActivity(item: HashMap<String, Any>): FsActivity {
        var result = FsActivity()
        try {
            result.activity = item[FsContract.TbActivity.FD_ACTIVITY].toString().toInt()
            result.name = item[FsContract.TbActivity.FD_NAME].toString()
            result.time = item[FsContract.TbActivity.FD_TIME].toString().toInt()

        } catch (e: Exception) {
            println(e.message)
        }

        return result
    }

    fun getCoord(item: HashMap<String, Any>): FsCoord {
        var result = FsCoord()
        try {
            result.latitude = item[FsContract.TbCoord.FD_LATITUDE].toString()
            result.longitude = item[FsContract.TbCoord.FD_LATITUDE].toString()
        } catch (e: Exception) {
            println(e.message)
        }

        return result
    }

    fun getAddress(item: HashMap<String, Any>): FsAddress {
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

    companion object {
        val db = Firebase.firestore
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        private var roles = ArrayList<IRole>()
        private var curUser: IUser? = null
        private val MESSAGE_USER_REGISTERED = "User created"
        private val MESSAGE_USER_SIGNIN = "User signedin"
        private val MESSAGE_USER_SIGNOUT = "User logged out"
        private val MESSAGE_USER_LOGIN_ERROR = "Can't login"
        private val MESSAGE_USER_UPDATED = "user updated"
        private val MESSAGE_USER_DELETED = "user deleted"
        private val MESSAGE_ROLE_CREATED = "role created"
        private val MESSAGE_ROLE_DELETED = "role deleted"
        private val MESSAGE_ROLE_UPDATED = "role updated"
        private val MESSAGE_ROLE_ASSIGNED = "role assigned"
    }
}