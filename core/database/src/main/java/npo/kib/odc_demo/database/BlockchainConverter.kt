package npo.kib.odc_demo.database

import androidx.room.TypeConverter
import npo.kib.odc_demo.feature_app.domain.core.getString
import npo.kib.odc_demo.feature_app.domain.core.loadPublicKey
import java.security.PublicKey
import java.util.*

class BlockchainConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID?) = uuid.toString()

    @TypeConverter
    fun toUUID(str: String): UUID? {
        return if (str == "null") null
        else UUID.fromString(str)
    }

    @TypeConverter
    fun fromPublicKey(key: PublicKey?) = key?.getString()

    @TypeConverter
    fun toPublicKey(str: String?): PublicKey? {
        return if (str.isNullOrEmpty()) null
        else str.loadPublicKey()
    }
}