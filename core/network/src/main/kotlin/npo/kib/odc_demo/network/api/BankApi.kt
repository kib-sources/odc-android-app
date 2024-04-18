package npo.kib.odc_demo.network.api



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

