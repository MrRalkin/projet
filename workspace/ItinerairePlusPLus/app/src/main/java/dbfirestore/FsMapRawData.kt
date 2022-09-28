package dbfirestore

import android.os.Parcelable
import interfaces.user.IMapRawData
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
class FsMapRawData(
    override var destinationId: String,
    override var created: Long,
    override var rawData: String
):IMapRawData , Parcelable{
}