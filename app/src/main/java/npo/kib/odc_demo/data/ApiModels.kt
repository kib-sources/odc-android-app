package npo.kib.odc_demo.data

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Deferred
import java.security.PublicKey

data class BokResponse(
    @SerializedName("bok")
    val bok: String,

    @SerializedName("code")
    val code: Int
)

data class WalletResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("sok_signature")
    val sokSignature: String,

    @SerializedName("wid")
    val wid: String
)
