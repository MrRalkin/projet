package dbfirestore

import android.content.Intent
import android.widget.TextView
import ca.bntec.itineraireplusplus.LoginActivity
import ca.bntec.itineraireplusplus.R
import classes.ActionResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import interfaces.IDBOperation
import interfaces.auth.ILogin
import interfaces.auth.IRegister
import interfaces.user.IUser
import kotlinx.coroutines.tasks.await

class FsOperations : IDBOperation {


    override suspend fun register(user: IRegister): ActionResult {
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

    override suspend fun login(user: ILogin): ActionResult {
        var result = ActionResult(true, MESSAGE_USER_REGISTERED, "")
        try {
            var rsp: AuthResult =
                mAuth.signInWithEmailAndPassword(user.email, user.password).await()

            var snp: QuerySnapshot = db.collection(FsContract.FsUser.TABLE_USER)
                .whereEqualTo(FsContract.FsUser.COLUMN_ID, rsp.user!!.uid.toString()).get().await()

//            var u = snp.toObjects(FsUser::class.java)
//            curUser = u as FsUser

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

    override suspend fun logout(): ActionResult {
        mAuth.signOut()
        return ActionResult(true, MESSAGE_USER_SIGNOUT, "")
    }

    override suspend fun getUserById(id: String): IUser? {

        TODO("Not yet implemented")
    }

    override suspend fun getUserByEmail(email: String): IUser? {

        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): IUser? {
        val credential = mAuth.currentUser

        if (credential != null) {
            if (curUser != null) return curUser

            
        }
        return null
    }

    override suspend fun setUser(user: IUser) {
        TODO("Not yet implemented")
    }

    companion object {
        val db = Firebase.firestore
        private var curUser: IUser? = null
        private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        private val MESSAGE_USER_REGISTERED = "User created"
        private val MESSAGE_USER_SIGNIN = "User created"
        private val MESSAGE_USER_SIGNOUT = "User logged out"
        private val MESSAGE_USER_LOGIN_ERROR = "Can't login"
    }
}