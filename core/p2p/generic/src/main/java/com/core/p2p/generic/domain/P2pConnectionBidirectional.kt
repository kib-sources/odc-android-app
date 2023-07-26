package com.core.p2p.generic.domain

// Расширенный интерфейс для p2p соеденений с возможностью отправки банкнот
interface P2pConnectionBidirectional : P2pConnection {
    fun startAdvertising()
    fun stopAdvertising()
}