package npo.kib.odc_demo.data.api

import npo.kib.odc_demo.data.models.*
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST

interface BankApi {
    @GET("/credentials")
    suspend fun getCredentials(): CredentialsResponse

    @POST("/register-wallet")
    suspend fun registerWallet(@Body wr: WalletRequest): WalletResponse

    @POST("/issue-banknotes")
    suspend fun issueBanknotes(@Body ir: IssueRequest): IssueResponse

    @POST("/receive-banknote")
    suspend fun receiveBanknote(@Body ir: ReceiveRequest): ReceiveResponse
}