package com.example.uno_card_game

open class Card {
    var imgUrl: String? = null
    var sendUid: String? = null

    constructor()

    constructor(imgUrl: String?, sendUid: String?){
        this.imgUrl = imgUrl
        this.sendUid = sendUid
    }

    open fun canSend(card: Card): Boolean {
        return true
    }
}