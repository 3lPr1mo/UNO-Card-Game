package com.example.uno_card_game

class WildCard(var type: String?, imgUrl: String?, sendUid: String?) : Card(imgUrl, sendUid) {

    init {
        super.imgUrl = imgUrl
        super.sendUid = sendUid
    }

}