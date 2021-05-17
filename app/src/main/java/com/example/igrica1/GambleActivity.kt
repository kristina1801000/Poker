package com.example.igrica1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import java.util.*


open class GambleActivity : AppCompatActivity() {

    private var leftMargin = 0
    private var novac = 100
    private var numScore = 0
    private var imgflipedCard : ImageView?= null
    private var banka = 1000
    private var timer: Timer? = null
    private val pockerViewModel:PokerViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnHigh.setOnClickListener {
            timer?.cancel()
            imgflipedCard?.visibility = View.INVISIBLE
            getNextCard()?.let { karta ->
                if (karta.number >= 8) {
                    dobitak()
                } else {
                    gubitak()
                }
            }
            Log.d("msg","Broj karata: ${pockerViewModel.deck?.size()}")
        }

        btnLow.setOnClickListener {
            timer?.cancel()
            imgflipedCard?.visibility = View.INVISIBLE
            getNextCard()?.let { karta ->
                if (karta.number < 8) {
                    dobitak()
                } else {
                    gubitak()
                }
            }
            Log.d("msg","Broj karata: ${pockerViewModel.deck?.size()}")
        }
        btnCash.setOnClickListener {
            dodajuKasu()
            timer?.cancel()
            closeGambling()
        }

        intent?.let {
            novac= it.getIntExtra(Const.KEY_POT,100)
            banka = it.getIntExtra(Const.KEY_BANKA, 1000)
        }
        resetGame()
    }

    private fun getNextCard() : Card?{
        val imageView = ImageView(this)
        val params = FrameLayout.LayoutParams(400,400)
        params.setMargins(leftMargin, 0, 0, 0);
        leftMargin += 75
        imageView.layoutParams = params
        val card = pockerViewModel.deck?.pull()
        card?.drawebleId?.let { imageView.setImageResource(it) }
        layCards.addView(imageView)
        return  card
    }

    private fun addFlipCard() {
        imgflipedCard = ImageView(this)
        val params = FrameLayout.LayoutParams(400,400)
        imgflipedCard?.layoutParams = params
        imgflipedCard?.setImageResource(R.drawable.flip)
        layCards.addView(imgflipedCard)
    }

    private fun resetGame() {
        txtMoney.text = novac.toString()
        txtSum.text=banka.toString()
        layCards.removeAllViewsInLayout()
        addFlipCard()
    }

    private fun dobitak() {
        Toast.makeText(this, "you guessed", Toast.LENGTH_LONG).show()
        novac *= 2
        txtMoney.text = novac.toString()
        numScore += 1
        if (numScore == 11){
            Toast.makeText(this, "You WIN", Toast.LENGTH_LONG).show()
        }
    }

    private fun gubitak() {
        novac = 0
            Toast.makeText(this, "you lose", Toast.LENGTH_LONG).show()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        layCards.removeAllViewsInLayout()
                        addFlipCard()
                        closeGambling()
                    }
                }
            }, 2000)
        txtMoney.text = novac.toString()
        txtSum.text=banka.toString()
        leftMargin = 0
    }

    private fun dodajuKasu(){
        banka += novac
        novac = 100
        banka -= novac
        txtSum.text=banka.toString()
        txtMoney.text = novac.toString()
        layCards.removeAllViewsInLayout()
        addFlipCard()
    }

    private fun closeGambling() {
        val intent1 = Intent()
        intent1.putExtra(Const.KEY_POT, novac)
        intent1.putExtra(Const.KEY_BANKA, banka)
        setResult(Const.RC_GAMBLING, intent1)
        Log.d("lala1", "$banka")
        finish()
    }
}



















