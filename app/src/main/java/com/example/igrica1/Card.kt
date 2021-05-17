package com.example.igrica1

import android.content.DialogInterface
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlin.random.Random

class Card {
    var number: Int = 0
    var sign: String = ""
    var drawebleId: Int? = null

    override fun toString(): String {
        return "$number[$sign]"
    }



}