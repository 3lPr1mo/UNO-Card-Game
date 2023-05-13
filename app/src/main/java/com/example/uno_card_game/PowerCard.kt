package com.example.uno_card_game

class PowerCard {
    var power: String? = null
    var imgUrl: String? = null

    constructor(){}

    constructor(power: String?, imgUrl: String?){
        this.power = power
        this.imgUrl = imgUrl
    }
}