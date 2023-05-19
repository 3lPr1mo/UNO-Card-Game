package com.example.uno_card_game

class WildCard : Card {

    var type: String? = null

    constructor() : super() {
    }

    constructor(type: String?, imgUrl: String?, sendUid: String?) : super(imgUrl, sendUid) {
        this.type = type
    }

    // Resto de la implementación de la clase
}