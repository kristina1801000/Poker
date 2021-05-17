package com.example.igrica1

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout

open class Deck(private val context: Context) {

    private val cards = ArrayList<Card>()
    private val znakovi = ArrayList<String>()

    init {
        znakovi.add("srce")
        znakovi.add("pik")
        znakovi.add("tref")
        znakovi.add("karo")
    }

    fun reset() {
        cards.clear()
        for (brojKarte in 1 until 15) {
            if (brojKarte == 11) {
                continue
            }
            for (sign in znakovi) {
                val karta = Card()
                karta.number = brojKarte
                karta.sign = sign
                karta.drawebleId = context.getResources().getIdentifier(sign + brojKarte, "drawable", context.getPackageName());
                Log.d("TAG", " $karta")
                cards.add(karta)
            }
        }
    }

    fun shuffle() {
        cards.shuffle()
        for (karta in cards){
            Log.d("TAG","$karta")
        }
    }

    fun pull(): Card {
        val izvucena = cards.random()
        cards.remove(cards.random())
        Log.d("TAG"," Izvucena: $izvucena")
        return izvucena
    }
     fun size(): Int {
       return cards.size

    }
}












