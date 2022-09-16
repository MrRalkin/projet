package dbfirestore

import android.os.Parcelable
import interfaces.user.IDestination
import interfaces.user.IUser
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class FsUser(
    override var id: String = "",
    override var name: String = "",
    override var address: String = "",
    override var city: String = "",
    override var state: String = "",
    override var postalCode: String = "",
    override var country: String = "",
    override var email: String = "",
    override var role_id: Int = 0,
    override var destinations: @RawValue ArrayList<IDestination> ? = null
) : IUser, Parcelable {
}