@file:Suppress("EXPERIMENTAL_API_USAGE")

package br.com.learningKotlin.app.defaults

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.registerDefaultRoutes() {
    routing {
        defaultRoutes()
    }
}

fun Route.defaultRoutes() {
    get("/") { call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain) }

    get("/*") {
        call.respond(
            HttpStatusCode.NotFound,
            TextContent("Page not found", ContentType.Text.Plain.withCharset(Charsets.UTF_8))
        )
    }

    get<MyLocation> { call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}") }

    get<Type.Edit> { call.respondText("Inside $it") }
    get<Type.List> { call.respondText("Inside $it") }

    install(StatusPages) {
        exception<AuthenticationException> { call.respond(HttpStatusCode.Unauthorized) }
        exception<AuthorizationException> { call.respond(HttpStatusCode.Forbidden) }
    }
}

@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")

@Location("/type/{name}")
data class Type(val name: String) {
    @Location("/edit")
    data class Edit(val type: Type)

    @Location("/list/{page}")
    data class List(val type: Type, val page: Int)
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
