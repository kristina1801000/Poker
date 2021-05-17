package com.example.igrica1

import org.koin.dsl.module

val koinModule = module {

    single {
        PokerViewModel()
    }
}