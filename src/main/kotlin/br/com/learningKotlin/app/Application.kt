@file:Suppress("UNUSED_PARAMETER", "EXPERIMENTAL_API_USAGE", "unused")

package br.com.learningKotlin.app

import br.com.learningKotlin.app.customer.registerCustomerRoutes
import br.com.learningKotlin.app.defaults.registerDefaultRoutes
import br.com.learningKotlin.app.order.registerOrderRoutes
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.content.CachingOptions
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.serialization.json
import io.ktor.util.date.GMTDate
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@kotlin.jvm.JvmOverloads
fun Application.main(testing: Boolean = false) {

    install(Locations) {}

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    install(AutoHeadResponse)

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ConditionalHeaders)

    install(ContentNegotiation) { json() }

    install(CachingHeaders) {
        options { outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(
                    CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60),
                    expires = null as? GMTDate?
                )
                else -> null
            }
        }
    }

    install(DataConversion)

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    registerCustomerRoutes()
    registerOrderRoutes()
    registerDefaultRoutes()

}


