package com.example.uno_card_game

class NormalCard : Card {

    var number: Int? = null
    var color: String? = null

    constructor() : super() {
    }

    constructor(number: Int?, color: String?, imgUrl: String?, sendUid: String?) : super(imgUrl, sendUid) {
        this.number = number
        this.color = color
    }

    override fun canSend(card: Card): Boolean {
        if (card is NormalCard) {
            return this.color == card.color || this.number == card.number
        }
        return false
    }
}