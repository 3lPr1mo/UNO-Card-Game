package com.example.uno_card_game

import android.util.Log
import com.google.firebase.storage.StorageReference
import kotlin.random.Random

class Rules {
    fun cardVerification(currentCard: Card, lastCard: Card): Boolean{
        val bool = if (currentCard is NormalCard && lastCard is NormalCard) {
            currentCard.color == lastCard.color || currentCard.number == lastCard.number
        } else {
            true
        }
        Log.d("candVerification", bool.toString())
        return bool
    }

    //Eat cards to the user
    fun eatCards(currentCard: Card, allCards: MutableList<MutableList<StorageReference>>, receiveUid: String): MutableList<Card> {
        var eatingCards: MutableList<StorageReference> = mutableListOf()
        var givingCards: MutableList<Card> = mutableListOf()
        if (currentCard is WildCard){
            if(currentCard.type.equals("+4")){
                for(i in 0 until 4){
                    eatingCards = allCards[Random.nextInt(0,5)]
                    givingCards.add(randomCards(eatingCards, receiveUid, allCards))
                }
            }else if(currentCard.type.equals("+2")){
                for(i in 0 until 2){
                    eatingCards = allCards[Random.nextInt(0,5)]
                    givingCards.add(randomCards(eatingCards, receiveUid, allCards))
                }
            }
        }
        //CARDS LEFT: 7
        return givingCards
    }

    fun randomCards(typeCard: MutableList<StorageReference>, receiveUid: String, allCards: MutableList<MutableList<StorageReference>>): Card{
        if(typeCard != allCards[4]){ //it's a normal card
            val randomNumber = Random.nextInt(0,9)  // Card number
            var card: NormalCard? = null
            if(typeCard[randomNumber].path.contains("azul")){
                card = NormalCard(randomNumber, "azul", typeCard[randomNumber].path, receiveUid)
            }
            if(typeCard[randomNumber].path.contains("red")){
                card = NormalCard(randomNumber, "red", typeCard[randomNumber].path, receiveUid)
            }
            if(typeCard[randomNumber].path.contains("yellow")){
                card = NormalCard(randomNumber, "yellow", typeCard[randomNumber].path, receiveUid)
            }
            if(typeCard[randomNumber].path.contains("green")){
                card = NormalCard(randomNumber, "green", typeCard[randomNumber].path, receiveUid)
            }
            return card as Card
            //userCards[id] = card as Card

        }else {
            val randomNumber = Random.nextInt(0, 3)
            var card: WildCard? = null
            if (typeCard[randomNumber].path.contains("+2")) {
                card = WildCard("+2", typeCard[randomNumber].path, receiveUid)
            }
            if (typeCard[randomNumber].path.contains("+4")) {
                card = WildCard("+4", typeCard[randomNumber].path, receiveUid)
            }
            if (typeCard[randomNumber].path.contains("change")) {
                card = WildCard("change-color", typeCard[randomNumber].path, receiveUid)
            }
            return card as Card
        }
    }
}