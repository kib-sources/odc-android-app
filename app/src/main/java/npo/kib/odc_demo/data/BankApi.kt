package npo.kib.odc_demo.data

import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST

import java.security.PublicKey

interface BankApi {

    @GET("/bok")
    suspend fun getBok(): BokResponse

    @POST("/register-wallet")
    suspend fun registerWallet(@Body sok: String): WalletResponse
}