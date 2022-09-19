package classes

import android.os.Parcelable
import interfaces.user.IAddress
import kotlinx.parcelize.Parcelize

@Parcelize
class Address(
    override var address: String = "",
    override var city: String = "",
    override var state: String = "",
    override var zip: String = "",
    override var country: String = ""
) : IAddress(), Parcelable {
}