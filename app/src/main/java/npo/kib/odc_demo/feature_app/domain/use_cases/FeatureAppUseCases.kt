package npo.kib.odc_demo.feature_app.domain.use_cases

data class FeatureAppUseCases(
    val p2pSendUseCase: P2PSendUseCase,
    val p2pReceiveUseCase: P2PReceiveUseCase
//    , val p2pReceiveNFCUseCase: P2PReceiveUseCase
)