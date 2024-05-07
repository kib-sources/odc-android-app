package npo.kib.odc_demo.core.wallet.model.data_packet.variants

import kotlinx.serialization.Serializable
import npo.kib.odc_demo.core.wallet.model.BanknoteWithBlockchain
import npo.kib.odc_demo.core.wallet.model.data_packet.DataPacketType.BANKNOTES_LIST

@Serializable
data class BanknotesList(val list: List<BanknoteWithBlockchain>) :
    DataPacketVariant(packetType = BANKNOTES_LIST)
