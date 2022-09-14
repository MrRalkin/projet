package dbfirestore

import android.os.Parcelable
import interfaces.user.IRole
import kotlinx.parcelize.Parcelize

@Parcelize
class FsRole(
    override var id: Int=-1,
    override var role: String="") :IRole,Parcelable {
}