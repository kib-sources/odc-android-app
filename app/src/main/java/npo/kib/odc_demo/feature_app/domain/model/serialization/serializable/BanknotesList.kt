package npo.kib.odc_demo.feature_app.domain.model.serialization.serializable

import kotlinx.serialization.Serializable

@Serializable
data class BanknotesList(val list : List<BanknoteWithBlockchain>) : CustomType
