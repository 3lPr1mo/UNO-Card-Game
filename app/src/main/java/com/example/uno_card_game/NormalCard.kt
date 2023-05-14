package com.example.uno_card_game

class NormalCard(var number: Int?, var color: String?, imgUrl: String?, sendUid: String?) : Card(imgUrl, sendUid) {

    init {
        super.imgUrl = imgUrl
        super.sendUid = sendUid
    }
}