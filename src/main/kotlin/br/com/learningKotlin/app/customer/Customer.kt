package br.com.learningKotlin.app.customer

import kotlinx.serialization.Serializable

@Serializable
data class Customer(val id: String, val firstName: String, val lastName: String, val email: String)

val customerStorage: MutableList<Customer> = mutableListOf()
