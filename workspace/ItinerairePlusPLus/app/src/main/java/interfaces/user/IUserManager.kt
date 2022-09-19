package interfaces.user

import classes.ActionResult
import interfaces.auth.ILogin
import interfaces.auth.IRegister

interface IUserManager {
    suspend fun userRegister(user: IRegister): ActionResult
    suspend fun userLogin(user: ILogin): ActionResult
    suspend fun userLogout(): ActionResult
    suspend fun userGetById(id: String): IUser?
    suspend fun userGetByEmail(email: String): IUser?
    suspend fun userGetCurrent(): IUser?
    suspend fun userUpdate(user:IUser): ActionResult
    suspend fun userUpdateCurrent(user:IUser): ActionResult
    suspend fun userDelete(user:IUser):ActionResult
    suspend fun userIsAuthenticated():Boolean

    suspend fun rolesGet():ArrayList<IRole>
    suspend fun roleGetByName(name:String):IRole?
    suspend fun roleGetById(id:String):IRole?
    suspend fun roleDelete(role:IRole):ActionResult
    suspend fun roleAdd(role:IRole):ActionResult
    suspend fun roleUpdate(role:IRole):ActionResult
    suspend fun roleAssign(user:IUser,role:IRole):ActionResult
    suspend fun resetSettingsToDefault():ActionResult

}