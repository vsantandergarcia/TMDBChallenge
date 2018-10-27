package com.vsantander.tmdbchallenge.utils.factory

import java.util.*

class DataFactory {

    companion object Factory {

        fun randomInt(): Int = Random().nextInt()

        fun randomUuid(): String = java.util.UUID.randomUUID().toString()

        fun randomLong(): Long = Random().nextLong()

        fun randomFloat(): Float = Random().nextFloat()

        fun randomBoolean(): Boolean = Random().nextBoolean()

    }

}