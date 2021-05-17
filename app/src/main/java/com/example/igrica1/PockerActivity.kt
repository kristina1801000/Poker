package com.example.igrica1


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_pocker.*
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min
import kotlin.math.sign


class PockerActivity : AppCompatActivity() {


    private var hand = ArrayList<Card>()
    private var novac = 100
    private var banka = 1000
    private val selectedCards = ArrayList<ImageView>()
    private var isFirstDeal = true
    private val pockerViewModel:PokerViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocker)
        pockerViewModel.deck = Deck(this)
        pockerViewModel.deck?.reset()
        pockerViewModel.deck?.shuffle()
        prepareUIForFirstDeal()
        setSelectionButtonsEnable(false)
        btnDeal.setOnClickListener {
            if (isFirstDeal) {
                firstDeal()
            } else {
                secondDeal()
            }
            isFirstDeal = !isFirstDeal
        }


        btnOne.setOnCheckedChangeListener { buttonView, isChecked ->
            (buttonView.tag as? String)?.toInt()?.let {
                flipCard(fristFlipCards, isChecked, it)
            }
        }
        btnTwo.setOnCheckedChangeListener { buttonView, isChecked ->
            (buttonView.tag as? String)?.toInt()?.let {
                flipCard(secondFlipCards, isChecked, it)
            }
        }
        btnThree.setOnCheckedChangeListener { buttonView, isChecked ->
            (buttonView.tag as? String)?.toInt()?.let {
                flipCard(thirdFlipCards, isChecked, it)
            }
        }
        btnFour.setOnCheckedChangeListener { buttonView, isChecked ->
            (buttonView.tag as? String)?.toInt()?.let {
                flipCard(fourthFlipCards, isChecked, it)
            }
        }
        btnFive.setOnCheckedChangeListener { buttonView, isChecked ->
            (buttonView.tag as? String)?.toInt()?.let {
                flipCard(fiftFlipCards, isChecked, it)
            }
        }

        btnCasa.setOnClickListener {
            addCash()
        }

        btnGamble.setOnClickListener {
            povezi()
        }
    }

    private fun secondDeal() {
        for (btn in layButtons.children) {
            if ((btn as? AppCompatToggleButton)?.isChecked == true) {
                val idx = (btn.tag as String).toInt()
                hand.removeAt(idx)
                        pockerViewModel.deck?.pull()?.let { it1 ->
                    hand.add(idx, it1)
                }
            }
        }


        drawHand()
        setSelectionButtonsEnable(false)
        btnOne.isChecked = false
        btnTwo.isChecked = false
        btnThree.isChecked = false
        btnFour.isChecked = false
        btnFive.isChecked = false
        enableDealButton(enable = false)
        if (winCombinations()){
            enableCasaButton(enable = true)
            enableGambleButton(enable = true)
            txtPot.text= novac.toString()
        }else{
            Toast.makeText(this,"You lose",Toast.LENGTH_LONG ).show()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        prepareUIForFirstDeal()
                    }
                }
            }, 2000)

        }
    }


    private fun firstDeal() {
        pockerViewModel.deck?.reset()
        pockerViewModel.deck?.shuffle()
        hand.clear()
        novac = 100
        banka -= novac
        txtBank.text = banka.toString()
        txtPot.text = novac.toString()
        setSelectionButtonsEnable(true)
        for (index in 0 until 5) {
            pockerViewModel.deck?.pull()?.let { card ->
                hand.add(card)
            }
            Log.d("sklj", "showCards $index, ${hand.size}")
        }
        drawHand()
    }

    private fun drawHand() {
        hand[0].drawebleId?.let {
            flipAnimationCard(fristFlipCards, it)
        }
        hand[1].drawebleId?.let {
            flipAnimationCard(secondFlipCards, it)
        }
        hand[2].drawebleId?.let {
            flipAnimationCard(thirdFlipCards, it)
        }
        hand[3].drawebleId?.let {
            flipAnimationCard(fourthFlipCards, it)
        }
        hand[4].drawebleId?.let {
            flipAnimationCard(fiftFlipCards, it)
        }
    }

    private fun addCash() {
        banka += novac
        novac = 100
        txtBank?.text = banka.toString()
        txtPot?.text = novac.toString()
        prepareUIForFirstDeal()
    }

    fun prepareUIForFirstDeal() {
        txtPot?.text = novac.toString()
        txtBank?.text = banka.toString()
        btnOne.isChecked = false
        btnTwo.isChecked = false
        btnThree.isChecked = false
        btnFour.isChecked = false
        btnFive.isChecked = false
        if (banka <= 0) {
            alertFunkcija()
        } else {
            startNewHand()
        }
    }

    private fun povezi() {
        val intent = Intent(this, GambleActivity::class.java)
        intent.putExtra(Const.KEY_POT, novac)
        intent.putExtra(Const.KEY_BANKA, banka)
        startActivityForResult(intent, Const.RC_GAMBLING)
    }

    private fun flipCard(imgCard: ImageView, isChecked: Boolean, tag: Int) {
        if (isChecked) {
            imgCard.setImageResource(R.drawable.flip)
        } else {
            val card = hand.get(tag)
            card.drawebleId?.let { it1 -> imgCard.setImageResource(it1) }
        }
    }

    private fun setSelectionButtonsEnable(setTo: Boolean) {
        btnOne.isEnabled = setTo
        btnTwo.isEnabled = setTo
        btnThree.isEnabled = setTo
        btnFour.isEnabled = setTo
        btnFive.isEnabled = setTo
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.RC_GAMBLING) {
            data?.let {
                novac = it.getIntExtra(Const.KEY_POT, 100)
                banka = it.getIntExtra(Const.KEY_BANKA, 1000)
                Log.d("lala2", "$banka")
                prepareUIForFirstDeal()
            }
        }
    }

    private fun alertFunkcija() {
        val builder = AlertDialog.Builder(this@PockerActivity)
        builder.setTitle("You lose")
        builder.setCancelable(false)
        builder.setMessage("You lose, do you want to try again")
        builder.setPositiveButton("YES") { dialog, which ->
            Toast.makeText(this, "Good luck", Toast.LENGTH_LONG).show()
            resetGame()
        }
        builder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(this, "Bye bye", Toast.LENGTH_LONG).show()
            finishAffinity()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun resetGame() {
        banka = 1000
        novac = 100
        banka -= novac
        prepareUIForFirstDeal()
    }

    private fun startNewHand() {
        fristFlipCards?.setImageResource(R.drawable.flip)
        secondFlipCards?.setImageResource(R.drawable.flip)
        thirdFlipCards?.setImageResource(R.drawable.flip)
        fourthFlipCards?.setImageResource(R.drawable.flip)
        fiftFlipCards?.setImageResource(R.drawable.flip)
        enableDealButton(enable = true)
        enableCasaButton(enable = false)
        enableGambleButton(enable = false)
    }

    private fun enableDealButton(enable: Boolean) {
        btnDeal.isEnabled = enable
    }
    private fun enableGambleButton(enable: Boolean) {
        btnGamble.isEnabled = enable
    }
    private fun enableCasaButton(enable: Boolean) {
        btnCasa.isEnabled = enable
    }

    private fun flipAnimationCard(imgCard: ImageView, draweble: Int) {
        val oa1 = ObjectAnimator.ofFloat(imgCard, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(imgCard, "scaleX", 0f, 1f)
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                imgCard.setImageResource(draweble)
                oa2.start()
            }
        })
        oa1.start()
    }

    private fun winCombinations(): Boolean {
        var numberOfSameCards = 0
        var numberOfPairs = 0
        val signCard: String = hand.get(0).sign
        var numberOfSameSign = 0
        Log.d("lala1", "winCombinations ${hand.size}")
        for (cardNumber in 1 until 15) {
            val x = hand.count { it.number == cardNumber }
            if (x > numberOfSameCards) {
                numberOfSameCards = x
            }
            if (x == 2) {
                numberOfPairs++
            }
            Log.d("lala1", "CardNumber $cardNumber is repeated $x,max Number of same cards $numberOfSameCards, Number of pairs $numberOfPairs")
        }
        for (signCards in hand) {
            numberOfSameSign = hand.count { it.sign == signCard }
        }
        if(isRoyalFlush(numberOfSameSign)){return true}
        if (isKenta(numberOfSameSign)){return true}
        if (isPoker(numberOfSameCards)){return true}
        if(isFullHouse(numberOfPairs,numberOfSameCards)){return true}
        if ((isThreeOfKind(numberOfSameCards))){return true}
        if (isTwoPairs(numberOfPairs)){return true}
        if (isPair(numberOfSameCards)){return true}
       return false
    }

    private fun checkIfCardExist(wantedNumber: Int): Boolean {
        var isCard = false
        for (card in hand) {
            if (card.number == wantedNumber) {
                isCard = true
            }
        }
        return isCard
    }

    private fun isRoyalFlush(numberOfSameSign: Int): Boolean {
        if (numberOfSameSign == 5) {
            if (
                    hand.count { it.number == 1 } == 1 &&
                    hand.count { it.number == 10 } == 1 &&
                    hand.count { it.number == 12 } == 1 &&
                    hand.count { it.number == 13 } == 1 &&
                    hand.count { it.number == 14 } == 1) {

                Toast.makeText(this, "You get a Royal flush)", Toast.LENGTH_LONG).show()
                novac *= 80
            } else {
                Toast.makeText(this, "You get a Flush", Toast.LENGTH_LONG).show()
                novac *= 6
            }
            return true
        }
        return false
    }

    private fun isKenta(numberOfSameSign: Int): Boolean {
        var minCard = hand[0].number
        for (i in 1 until 5) {
            if (minCard > hand[i].number) {
                minCard = hand[i].number
            }
        }
        var isKenta = true
        for (i in 1 until 5) {
            if (!checkIfCardExist(minCard + i)) {
                isKenta = false
            }
        }
        if (isKenta && numberOfSameSign == 5) {
            Toast.makeText(this, "You get a Straight flush", Toast.LENGTH_LONG).show()
            novac *= 50
            return true

        } else if (isKenta) {
            Toast.makeText(this, "You get a Straight", Toast.LENGTH_LONG).show()
            novac *= 4
            return true
        }
        return false
    }

    private fun isPoker(numberOfSameCards: Int): Boolean {
        if (numberOfSameCards == 4) {
            Toast.makeText(this, "You get a FOUR OF KIND ", Toast.LENGTH_LONG).show()
            novac *= 25
            return true
        }
        return false
    }

    private fun isFullHouse(numberOfSameCards: Int, numberOfPairs: Int): Boolean {
        var maxCard = hand[0].number
        for (i in 1 until 5) {
            if (maxCard < hand[i].number) {
                maxCard = hand[i].number
            }
        }
        if (numberOfSameCards == 3) {
            if (numberOfPairs == 1) {
                Toast.makeText(this, "You get a Full house", Toast.LENGTH_LONG).show()
                novac *= 9
            }
            return true
        }
       return false
    }
    private fun isThreeOfKind(numberOfSameCards: Int) : Boolean {
        if (numberOfSameCards == 3) {
            Toast.makeText(this, "You get a THREE OF A KIND", Toast.LENGTH_LONG).show()
            novac *=3
            return true
        }
        return false
    }
    private fun isTwoPairs(numberOfPairs:Int): Boolean {
        if (numberOfPairs > 1) {
            Toast.makeText(this, "You get a TWO PAIR", Toast.LENGTH_LONG).show()
            novac *= 2
            return true
        }
        return false
    }
    private fun isPair(numberOfSameCards:Int): Boolean {
        if (numberOfSameCards == 2) {
            Toast.makeText(this, "You get a PAIR", Toast.LENGTH_LONG).show()
            novac *= 1
            return true
        }
       return false
    }
}























