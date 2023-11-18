package npo.kib.odc_demo.feature_app.data.api

import npo.kib.odc_demo.feature_app.domain.model.serialization.*
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.CredentialsResponse
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.IssueRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.IssueResponse
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.ReceiveRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.ReceiveResponse
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.WalletRequest
import npo.kib.odc_demo.feature_app.domain.model.serialization.serializable.bank_api.WalletResponse
import retrofit2.http.Body
import retrofit2.http.GET
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

