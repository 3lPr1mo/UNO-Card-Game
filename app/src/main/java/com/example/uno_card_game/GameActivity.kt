package com.example.uno_card_game

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    //Binding
    private lateinit var binding: ActivityGameBinding

    //Cards
    private lateinit var userCards: MutableMap<Int,Card>
    private lateinit var dock: MutableList<Card>
    private var playedCard: Card? = null
    private lateinit var userImageButtonCards: MutableList<ImageButton>
    private lateinit var allCards: MutableList<MutableList<StorageReference>>
    private lateinit var blueCards: MutableList<StorageReference>
    private lateinit var redCards: MutableList<StorageReference>
    private lateinit var yellowCards: MutableList<StorageReference>
    private lateinit var greenCards: MutableList<StorageReference>
    private lateinit var powerCards: MutableList<StorageReference>
    private var rules: Rules = Rules()

    //Firebase
    private lateinit var Dbref: DatabaseReference
    private var storage = FirebaseStorage.getInstance()
    private var sendRoom: String? = null
    private var receiveRoom: String? = null
    private var lastSend: String? = null

    //Winner username
    private lateinit var winner: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val opponent = intent.getStringExtra("currentUser")
        val receiveUid = intent.getStringExtra("uid")
        val sendUid = FirebaseAuth.getInstance().currentUser?.uid
        supportActionBar?.hide()
        setContentView(binding.root)


        //Variables declaration
        userCards = mutableMapOf()
        dock = mutableListOf()
        userImageButtonCards = mutableListOf()
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
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    // Checking the turn
                    if(lastSend != sendUid){
                        binding.cardsLayout.visibility = View.VISIBLE
                    }else{
                        binding.cardsLayout.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if(!this@GameActivity.isDestroyed){
                        showDialog("Game Finished", "The Connection has been lost")
                    }
                }

            })

        Dbref.child("rooms").child(sendRoom!!).child("cards").addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(userHasWon()){
                    showDialog("Game Over", "$winner has WON")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if(!this@GameActivity.isDestroyed){
                    showDialog("WON by W", "The opponent lost the connection, you lucky")
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

    private fun userHasWon(): Boolean{
        return if(userCards.isEmpty()){
            winner = "YOU"
            true
        }else{
            false
        }
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
            val id = View.generateViewId()
            Log.d("typeCard", typeCard.toString())
            if(typeCard != powerCards){ //it's a normal card
                val randomNumber = Random.nextInt(0,9)  // Card number
                var card: NormalCard? = null
                if(typeCard[randomNumber].path.contains("azul")){
                    card = NormalCard(randomNumber, "azul", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path, id){}
                }
                if(typeCard[randomNumber].path.contains("red")){
                    card = NormalCard(randomNumber, "red", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path, id){}
                }
                if(typeCard[randomNumber].path.contains("yellow")){
                    card = NormalCard(randomNumber, "yellow", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path, id){}
                }
                if(typeCard[randomNumber].path.contains("green")){
                    card = NormalCard(randomNumber, "green", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path, id){}
                }
                userCards[id] = card as Card

            }else{
                val randomNumber = Random.nextInt(0,3)
                var card: WildCard? = null
                if(typeCard[randomNumber].path.contains("+2")){
                    card = WildCard("+2", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path, id){}
                }
                if(typeCard[randomNumber].path.contains("+4")){
                    card = WildCard("+4", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path, id){}
                }
                if(typeCard[randomNumber].path.contains("change")){
                    card = WildCard("change-color", typeCard[randomNumber].path, sendUid)
                    addCardToView(card, typeCard[randomNumber].path, id){}
                    //addCardToDatabase(card, typeCard[randomNumber].path, )
                }
                userCards[id] = card as Card
            }

        }
    }

    private fun addCardToView(card: Card, url: String, id: Int, callback: () -> Unit){

        val imageRef = storage.reference.child(url)


        // Función auxiliar para comprobar si todas las operaciones se han completado
        fun checkOperationsCompleted() {
            // Todas las operaciones se han completado, llamar al callback
            callback()
        }

        var imageButton: ImageButton = ImageButton(this)

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageURL = uri.toString()
            card.imgUrl = imageURL //Setting the valid URL

            fun setImageButtonImage(){
                //Create an ImageButton instance
                //imageButton = ImageButton(this)
                imageButton.layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.image_width), // Ancho de la imagen
                    resources.getDimensionPixelSize(R.dimen.image_height)
                )
                // Image ID
                imageButton.id = id
                //Load the image in the view with Glide
                Log.d("imageURL", imageURL)
                setImageGlide(imageURL, imageButton)
                userImageButtonCards.add(imageButton)
                binding.cardsLayout.addView(imageButton)
            }
            setImageButtonImage()
            binding.cardsLeftTxt.text = "CARDS LEFT: " + userCards.size.toString() // Reload the opponnet cards left
        }

        imageButton.setOnClickListener {
            sendCardToUser(imageButton)
            //Dbref.child("games")
            /*imageRef.downloadUrl.addOnSuccessListener { uri ->
                setImageToCardView(uri.toString())
            }.addOnFailureListener {
                Log.d("FireStorage", "Error to dowload the image to cardView")
            }*/
            //binding.cardView.setImageDrawable(imageButton.drawable)
            // Sending the last played card
            if(canSendToUSer(userCards[imageButton.id])){
                playedCard = userCards[imageButton.id] // Save the last card played
                sendLastCardPlayedToDatabase(sendRoom!!, imageButton)
                val parentLayout: ViewGroup = imageButton.parent as ViewGroup
                parentLayout.removeView(imageButton)
                userCards.remove(imageButton.id)
            }
            //binding.cardsLeftTxt.text = "CARDS LEFT: " + userCards.size.toString()
        }

        //Setting the lastCard in firebase to the sender cardView
        Dbref.child("games").child(sendRoom!!).child("cards").child("lastCard")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(nextSnapshot in snapshot.children){
                            if(nextSnapshot.value is NormalCard){
                                val cardToShow = nextSnapshot.getValue(NormalCard::class.java)
                                setImageToCardView(cardToShow!!.imgUrl!!)
                            }else{
                                val cardToShow = nextSnapshot.getValue(WildCard::class.java)
                                setImageToCardView(cardToShow!!.imgUrl!!)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        //Setting the lastCard in firebase to the receiver cardView
        Dbref.child("games").child(receiveRoom!!).child("cards").child("lastCard")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(nextSnapshot in snapshot.children){
                            if(nextSnapshot.value is NormalCard){
                                val cardToShow = nextSnapshot.getValue(NormalCard::class.java)
                                setImageToCardView(cardToShow!!.imgUrl!!)
                            }else{
                                val cardToShow = nextSnapshot.getValue(WildCard::class.java)
                                setImageToCardView(cardToShow!!.imgUrl!!)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        checkOperationsCompleted()

    }

    private fun setImageToCardView(uri: String){
        Glide.with(this)
            .load(uri)
            .into(binding.cardView)
    }

    private fun setImageGlide(imageURL: String?, imageButton: ImageButton){
        Glide.with(this)
            .load(imageURL)
            .into(imageButton)
    }

    private fun sendCardToUser(imageButton: ImageButton){
        //Getting the card
        val card: Card? = userCards[imageButton.id]
        //Validate that can send the card to the user
        if(canSendToUSer(card)){
            sendRoom?.let {
                sendCardToDatabase(it, imageButton)
            }
        }
    }

    private fun canSendToUSer(card: Card?): Boolean{
        //Validate that the user can send the Card to the opponent
        return if(playedCard == null){ //Is it the first card
            Log.d("playedCard", playedCard.toString())
            true
        }else{
            if(card is NormalCard){
                Log.d("candSendTo", rules.cardVerification(card, playedCard!!).toString())
                rules.cardVerification(card, playedCard!!)
                //card.canSend(card)
            }else{
                card!!.canSend(card)
            }
        }
    }

    private fun sendLastCardPlayedToDatabase(sendRoom: String, imageButton: ImageButton){
        val sendLastCardReference = Dbref.child("games").child(sendRoom).child("lastCard").push()
        val lastCard: Card? = playedCard
        // Save the last card in database
        sendLastCardReference.setValue(lastCard).addOnSuccessListener {
            receiveRoom?.let {
                Dbref.child("games").child(it).child("cards").child("lastCard").push()
                    .setValue(lastCard)
            }
        }
    }

    private fun sendCardToDatabase(sendRoom: String, imageButton: ImageButton){
        val sendCardReference = Dbref.child("games").child(sendRoom).child("cards").push()
        val senderCard: Card? = userCards[imageButton.id]
        sendCardReference.setValue(senderCard).addOnSuccessListener {
            receiveRoom?.let {
                Dbref.child("games").child(it).child("cards").push()
                    .setValue(senderCard)
            }
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