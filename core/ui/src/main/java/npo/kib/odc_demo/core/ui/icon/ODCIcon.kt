package npo.kib.odc_demo.core.ui.icon


import npo.kib.odc_demo.core.ui.R


enum class ODCIcon(val resId: Int) {
    HomeIcon(R.drawable.home_icon),
    HomeIconSelected(R.drawable.home_icon_selected),
    SettingsIcon(R.drawable.settings_icon),
    SettingsIconSelected(R.drawable.settings_icon_selected),

    P2PSendIcon(R.drawable.send_money),
    P2PReceiveIcon(R.drawable.receive_money),
    P2PATMIcon(R.drawable.atm_top_up_icon),

    BluetoothIcon(R.drawable.baseline_bluetooth_24),
    BluetoothSearchingIcon(R.drawable.baseline_bluetooth_searching_24),

    RefreshIcon(R.drawable.baseline_refresh_24)
}