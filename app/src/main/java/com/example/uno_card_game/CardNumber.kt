package com.example.uno_card_game

class CardNumber {
    var number: Int? = null
    var color: String? = null
    var imgUrl: String? = null

    constructor(){}

    constructor(number: Int?, color: String?, imgUrl: String?){
        this.number = number
        this.color = color
        this.imgUrl = imgUrl
    }
}