package classes.map

import android.os.Parcelable
import interfaces.user.IMapRawData
import kotlinx.parcelize.Parcelize

@Parcelize
class MapRawData(
    override var destinationId: String,
    override var created: Long,
    override var rawData: String
):IMapRawData,Parcelable