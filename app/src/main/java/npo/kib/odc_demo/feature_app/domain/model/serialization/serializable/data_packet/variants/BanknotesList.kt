package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.variants

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.BanknoteWithBlockchain
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.data_packet.DataPacketType.BANKNOTES_LIST

@Serializable
data class BanknotesList(val list : List<BanknoteWithBlockchain>) : DataPacketVariant(packetType = BANKNOTES_LIST)
