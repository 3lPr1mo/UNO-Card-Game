package com.example.uno_card_game

class NormalCard(var number: Int?, var color: String?, imgUrl: String?, sendUid: String?) : Card(imgUrl, sendUid) {

    init {
        super.imgUrl = imgUrl
        super.sendUid = sendUid
    }

    override fun canSend(card: Card): Boolean{
        if(card is NormalCard){
            return this.color == card.color || this.number == card.number
        }
        return false
    }
}