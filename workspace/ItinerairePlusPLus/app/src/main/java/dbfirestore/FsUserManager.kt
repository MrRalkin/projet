package dbfirestore

import classes.ActionResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import interfaces.auth.ILogin
import interfaces.auth.IRegister
import interfaces.user.IDestination
import interfaces.user.IRole
import interfaces.user.IUser
import interfaces.user.IUserManager
import kotlinx.coroutines.tasks.await

class FsUserManager : IUserManager {
    private fun dbInit() {

    }

    override suspend fun userRegister(user: IRegister): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_REGISTERED, "")
        try {
            var roles=rolesGet()
            var roleId=0
            if(roles.count()>1){
                roleId=roles.get(roles.count()-1).id
            }

            var rsp: AuthResult =
                mAuth.createUserWithEmailAndPassword(user.email, user.password).await()

            val newUser = FsUser(rsp.user!!.uid.toString(), user.name,FsAddress(),user.email,roleId,
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

            var snp: QuerySnapshot = db.collection(FsContract.TbUser.COLLECTION_NAME)
                .whereEqualTo(FsContract.TbUser.FD_ID, rsp.user!!.uid.toString()).get().await()

            for (item in snp.documents) {
                if (item != null) {
                    item.toObject(FsUser::class.java)
                        ?.let {
                            curUser = it
                            return result
                        }
                } else {
                    result.isSuccess = false
                    result.errorMessage = MESSAGE_USER_LOGIN_ERROR
                }
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

    override suspend fun userGetByEmail(email: String):  IUser? {
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

    override suspend fun  userGetById(id: String):IUser? {
        try {

            var snp: DocumentSnapshot? =
                db.collection(FsContract.TbUser.COLLECTION_NAME).document(id).get().await()
            //.whereEqualTo(FsContract.FsUser.COLUMN_EMAIL,email).get().await()

            //for (item in snp.documents) {
            if (snp != null) {
                snp.toObject(FsUser::class.java)
                    ?.let {
                      var user:FsUser = it
                        return user
                    }
            }
            //  }
        } catch (e: Exception) {
            println(e.message)
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
            curUser =user
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
            var snp: QuerySnapshot = db.collection(FsContract.TbRole.COLLECTION_NAME).get().await()
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
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(roleSSN.id.toString()).set(roleSSN)
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(roleUser.id.toString()).set(roleUser)
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
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(role.id.toString()).delete()
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
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(role.id.toString()).set(role)
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
            db.collection(FsContract.TbRole.COLLECTION_NAME).document(role.id.toString()).set(role)
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
            user.role_id=role.id
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