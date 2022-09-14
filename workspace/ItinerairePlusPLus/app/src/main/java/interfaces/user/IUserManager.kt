package interfaces.user

import classes.ActionResult
import interfaces.auth.ILogin
import interfaces.auth.IRegister

interface IUserManager {
    suspend fun register(user: IRegister): ActionResult
    suspend fun login(user: ILogin): ActionResult
    suspend fun logout(): ActionResult
    suspend fun getUserById(id: String): IUser?
    suspend fun getUserByEmail(email: String): IUser?
    suspend fun setUser(user: IUser)
    fun getCurrentUser(): IUser?
}