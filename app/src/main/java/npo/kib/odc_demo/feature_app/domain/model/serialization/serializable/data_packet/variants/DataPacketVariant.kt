package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.domain.model.DataPacket


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
sealed interface DataPacketVariant
