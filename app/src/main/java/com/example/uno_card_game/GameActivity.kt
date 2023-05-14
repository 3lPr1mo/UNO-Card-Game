package com.example.uno_card_game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.uno_card_game.databinding.ActivityGameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    //Binding
    private lateinit var binding: ActivityGameBinding

    //Cards
    private lateinit var userCards: MutableList<Card>
    private lateinit var dock: MutableList<Card>
    private lateinit var playedCards: MutableList<Card>
    private lateinit var imageButtonCards: MutableList<ImageButton>
    private lateinit var allCards: MutableList<MutableList<StorageReference>>
    private lateinit var blueCards: MutableList<StorageReference>
    private lateinit var redCards: MutableList<StorageReference>
    private lateinit var yellowCards: MutableList<StorageReference>
    private lateinit var greenCards: MutableList<StorageReference>
    private lateinit var powerCards: MutableList<StorageReference>

    //Firebase
    private lateinit var Dbref: DatabaseReference
    private var storage = FirebaseStorage.getInstance()
    private var sendRoom: String? = null
    private var receiveRoom: String? = null
    private var lastSend: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val opponent = intent.getStringExtra("currentUser")
        val receiveUid = intent.getStringExtra("uid")
        val sendUid = FirebaseAuth.getInstance().currentUser?.uid
        supportActionBar?.hide()
        setContentView(binding.root)

        //Variables declaration
        userCards = mutableListOf()
        dock = mutableListOf()

        //Firebase room
        sendRoom = receiveUid + sendUid
        receiveRoom = sendUid + receiveUid
        Dbref = FirebaseDatabase.getInstance().reference

        //Binding
        binding.opponentNameTxt.text = opponent.toString() //Setting the opponent Name

        //Cards
        //firebaseImages() //Saving
        firebaseImages{
            Log.d("Finished","Se finalizó la carga")
            mixCards(sendUid as String)
        }
        //mixCards(sendUid as String) //give cards to the user
        binding.cardsLeftTxt.text = "CARDS LEFT: " + userCards.size.toString()

        lastSend = receiveUid

        //Adding data to firebase
        Dbref.child("rooms").child(sendRoom!!).child("cards")
            .addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    if(!this@GameActivity.isDestroyed){
                        showDialog("Game Finished", "The Connection has been lost")
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun showDialog(title: String, msg: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun mixCards(sendUid: String){
        allCards = mutableListOf(blueCards, redCards, yellowCards, greenCards, powerCards)
        Log.d("allCards", allCards.toString())
        for(i in 0 until 7){
            val typeCard = allCards[Random.nextInt(0, 5)] //red, yellow, green, blue or power
            Log.d("typeCard", typeCard.toString())
            if(typeCard != powerCards){ //it's a normal card
                var randomNumber = Random.nextInt(0,9)  // Card number
                var card: NormalCard? = null
                if(typeCard[randomNumber].path.contains("azul")){
                    card = NormalCard(randomNumber, "azul", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path){}
                }
                if(typeCard[randomNumber].path.contains("red")){
                    card = NormalCard(randomNumber, "red", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path){}
                }
                if(typeCard[randomNumber].path.contains("yellow")){
                    card = NormalCard(randomNumber, "yellow", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path){}
                }
                if(typeCard[randomNumber].path.contains("green")){
                    card = NormalCard(randomNumber, "green", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path){}
                }
                userCards.add(card as Card)

            }else{
                var randomNumber = Random.nextInt(0,3)
                var card: WildCard? = null
                if(typeCard[randomNumber].path.contains("+2")){
                    card = WildCard("+2", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path){}
                }
                if(typeCard[randomNumber].path.contains("+4")){
                    card = WildCard("+4", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path){}
                }
                if(typeCard[randomNumber].path.contains("change")){
                    card = WildCard("change-color", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path){}
                }
                userCards.add(card as Card)
            }

        }
    }

    private fun addCardToView(card: Card, url: String, callback: () -> Unit){

        val imageRef = storage.reference.child(url)


        // Función auxiliar para comprobar si todas las operaciones se han completado
        fun checkOperationsCompleted() {
            // Todas las operaciones se han completado, llamar al callback
            callback()
        }

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageURL = uri.toString()

            //Create an ImageButton instance
            val imageButton = ImageButton(this)
            imageButton.layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.image_width), // Ancho de la imagen
                resources.getDimensionPixelSize(R.dimen.image_height)
            )

            //Load the image in the view with Glide
            Log.d("imageURL", imageURL)
            Glide.with(this)
                .load(imageURL)
                .into(imageButton)

            binding.cardsLayout.addView(imageButton)
            checkOperationsCompleted()
        }
    }

    private fun firebaseImages(callback: () -> Unit){
        //Declaration
        /*val bc = storage.reference.child("blue_cards/")
        bc.listAll().addOnSuccessListener { listResult ->
            for(item in listResult.items){
                blueCards.add(item)
            }
            Log.d("bc", bc.toString())
        }*/
        val blue = storage.reference.child("blue_cards/")
        val red = storage.reference.child("red_cards/")
        val yellow = storage.reference.child("yellow_cards/")
        val green = storage.reference.child("green_cards/")
        val power = storage.reference.child("power_cards/")

        blueCards = mutableListOf()
        yellowCards = mutableListOf()
        redCards = mutableListOf()
        greenCards = mutableListOf()
        powerCards = mutableListOf()

        var operationsCompleted = 0

        // Función auxiliar para comprobar si todas las operaciones se han completado
        fun checkOperationsCompleted() {
            operationsCompleted++
            if (operationsCompleted == 5) {
                // Todas las operaciones se han completado, llamar al callback
                callback()
            }
        }

        //saving into allCards
        blue.listAll().addOnSuccessListener { listResult ->
            for(item in listResult.items){
                blueCards.add(item)
            }
            Log.d("cardBlue", listResult.items.toString())
            Log.d("blue", blueCards.toString())
            checkOperationsCompleted()
        }

        red.listAll().addOnSuccessListener { listResult ->
            for(item in listResult.items){
                redCards.add(item)
            }
            Log.d("red", redCards.toString())
            checkOperationsCompleted()
        }

        yellow.listAll().addOnSuccessListener { listResult ->
            for(item in listResult.items){
                yellowCards.add(item)
            }
            Log.d("yellow", yellowCards.toString())
            checkOperationsCompleted()
        }

        green.listAll().addOnSuccessListener { listResult ->
            for(item in listResult.items){
                greenCards.add(item)
            }
            Log.d("green", greenCards.toString())
            checkOperationsCompleted()
        }

        power.listAll().addOnSuccessListener { listResult ->
            for(item in listResult.items){
                powerCards.add(item)
            }
            Log.d("power", powerCards.toString())
            checkOperationsCompleted()
        }
    }
}