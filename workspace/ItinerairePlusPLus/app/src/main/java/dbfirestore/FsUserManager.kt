package dbfirestore

import android.content.Intent
import android.widget.TextView
import ca.bntec.itineraireplusplus.LoginActivity
import ca.bntec.itineraireplusplus.R
import classes.ActionResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import interfaces.IDBOperation
import interfaces.auth.ILogin
import interfaces.auth.IRegister
import interfaces.user.IRole
import interfaces.user.IUser
import interfaces.user.IUserManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FsUserManager : IUserManager {
    private fun dbInit() {

    }

    override suspend fun userRegister(user: IRegister): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_REGISTERED, "")
        try {
            var rsp: AuthResult =
                mAuth.createUserWithEmailAndPassword(user.email, user.password).await()
            val newUser = FsUser(rsp.user!!.uid.toString(), user.name, "", "", "", "", "", "")
            db.collection(FsContract.FsUser.TABLE_USER).document(user.email).set(newUser)
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

            var snp: QuerySnapshot = db.collection(FsContract.FsUser.TABLE_USER)
                .whereEqualTo(FsContract.FsUser.COLUMN_ID, rsp.user!!.uid.toString()).get().await()

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

    override suspend fun userGetById(id: String): IUser? {
        try {
            var snp: QuerySnapshot = db.collection(FsContract.FsUser.TABLE_USER)
                .whereEqualTo(FsContract.FsUser.COLUMN_ID, id).get().await()

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

    override suspend fun userGetByEmail(email: String): IUser? {
        try {

            var snp: DocumentSnapshot? =
                db.collection(FsContract.FsUser.TABLE_USER).document(email).get().await()
            //.whereEqualTo(FsContract.FsUser.COLUMN_EMAIL,email).get().await()

            //for (item in snp.documents) {
            if (snp != null) {
                snp.toObject(FsUser::class.java)
                    ?.let {
                        curUser = it
                        return curUser
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

    override suspend fun userUpdate(user: IUser): ActionResult {
        TODO("Not yet implemented")
    }

    override suspend fun userDelete(user: IUser): ActionResult {
        TODO("Not yet implemented")
    }

    override suspend fun rolesGet(): ArrayList<IRole> {
        if (roles.count() > 0) {
            return roles
        }
        val roleSSN: IRole = FsRole(1, "SSN")
        val roleUser: IRole = FsRole(2, "User")
        db.collection("Roles").document(roleSSN.id.toString()).set(roleSSN)
        db.collection("Roles").document(roleUser.id.toString()).set(roleUser)
        var result=ArrayList<IRole>()
        result.add(roleSSN)
        result.add(roleUser)
        return result
    }

    override suspend fun roleGetByName(name: String): IRole {
        TODO("Not yet implemented")
    }

    override suspend fun roleGetById(id: String): IRole {
        TODO("Not yet implemented")
    }

    override suspend fun roleDelete(role: IRole): ActionResult {
        TODO("Not yet implemented")
    }

    override suspend fun roleAdd(role: IRole): ActionResult {
        TODO("Not yet implemented")
    }

    override suspend fun roleUpdate(role: IRole): ActionResult {
        TODO("Not yet implemented")
    }

    override suspend fun roleAssign(user: IUser, role: IRole) {
        TODO("Not yet implemented")
    }

    companion object {
        val db = Firebase.firestore
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        private var roles = ArrayList<IRole>()
        private var curUser: IUser? = null
        private val MESSAGE_USER_REGISTERED = "User created"
        private val MESSAGE_USER_SIGNIN = "User created"
        private val MESSAGE_USER_SIGNOUT = "User logged out"
        private val MESSAGE_USER_LOGIN_ERROR = "Can't login"
    }
}