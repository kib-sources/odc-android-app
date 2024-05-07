package npo.kib.odc_demo.core.wallet.model.data_packet.variants

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.core.wallet.model.data_packet.DataPacket
import npo.kib.odc_demo.core.wallet.model.data_packet.DataPacketType


/**
 * __Marker interface for__ [DataPacket] __types that can be serialized and deserialized.__
 *
 * (com.upokecenter.cbor + kotlinx.serialization are used for serialization and deserialization)
 *
 *  __The current implementing types__:
 *  1. [UserInfo]
 *  1. [AmountRequest]
 *  1. [BanknotesList]
 *  1. [AcceptanceBlocks]
 *  1. [Block]
 *  1. [TransactionResult]
 * */
@Serializable
sealed class DataPacketVariant(var packetType: DataPacketType)
//have to use var for packet type or ksp (and kapt) give "Cannot find setter for field" errors for some reason.
//ksp error during compilation:
//e: [ksp] ...odc-android-app/app/src/main/kotlin/npo/kib/odc_demo/feature_app/domain/model
// /serialization/serializable/data_packet/variants/DataPacketVariant.kt:22: Cannot find setter for field.