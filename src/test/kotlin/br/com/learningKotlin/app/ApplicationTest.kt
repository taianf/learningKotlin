package br.com.learningKotlin.app

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }

    @Test
    fun testLocation() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/location/taian?arg1=11&arg2=test").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Location: name=taian, arg1=11, arg2=test", response.content)
            }
        }
    }

    @Test
    fun testTypeEdit() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/type/taian/edit").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Inside Edit(type=Type(name=taian))", response.content)
            }
        }
    }

    @Test
    fun testTypeList() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/type/taian/list/1").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Inside List(type=Type(name=taian), page=1)", response.content)
            }
        }
    }

    @Test
    fun test404() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/aaa").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("Page not found", response.content)
            }
        }
    }

    @Test
    fun testCustomer() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Post, "/customer") {
                addHeader(HttpHeaders.ContentType, "application/json")
                setBody("""{"id":"100","firstName":"Jane","lastName":"Smith","email":"jane.smith@company.com"}""")
            }.apply {
                assertEquals(HttpStatusCode.Accepted, response.status())
                assertEquals("Customer stored correctly", response.content)
            }

            handleRequest(HttpMethod.Post, "/customer") {
                addHeader(HttpHeaders.ContentType, "application/json")
                setBody("""{"id":"200","firstName":"John","lastName":"Smith","email":"john.smith@company.com"}""")
            }.apply {
                assertEquals(HttpStatusCode.Accepted, response.status())
                assertEquals("Customer stored correctly", response.content)
            }

            handleRequest(HttpMethod.Post, "/customer") {
                addHeader(HttpHeaders.ContentType, "application/json")
                setBody("""{"id":"300","firstName":"Mary","lastName":"Smith","email":"mary.smith@company.com"}""")
            }.apply {
                assertEquals(HttpStatusCode.Accepted, response.status())
                assertEquals("Customer stored correctly", response.content)
            }

            handleRequest(HttpMethod.Get, "/customer") {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(
                    """[{"id":"100","firstName":"Jane","lastName":"Smith","email":"jane.smith@company.com"},{"id":"200","firstName":"John","lastName":"Smith","email":"john.smith@company.com"},{"id":"300","firstName":"Mary","lastName":"Smith","email":"mary.smith@company.com"}]""",
                    response.content
                )
            }

            handleRequest(HttpMethod.Get, "/customer/200") {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(
                    """{"id":"200","firstName":"John","lastName":"Smith","email":"john.smith@company.com"}""",
                    response.content
                )
            }

            handleRequest(HttpMethod.Get, "/customer/500") {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("""No customer with id 500""", response.content)
            }

            handleRequest(HttpMethod.Delete, "/customer/100") {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.Accepted, response.status())
                assertEquals("""Customer removed correctly""", response.content)
            }

            handleRequest(HttpMethod.Delete, "/customer/500") {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("""Not Found""", response.content)
            }

            handleRequest(HttpMethod.Get, "/customer") {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(
                    """[{"id":"200","firstName":"John","lastName":"Smith","email":"john.smith@company.com"},{"id":"300","firstName":"Mary","lastName":"Smith","email":"mary.smith@company.com"}]""",
                    response.content
                )
            }
        }
    }

    @Test
    fun testGetOrder() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/order").apply {
                assertEquals(
                    """[{"number":"2020-04-06-01","contents":[{"item":"Ham Sandwich","amount":2,"price":5.5},{"item":"Water","amount":1,"price":1.5},{"item":"Beer","amount":3,"price":2.3},{"item":"Cheesecake","amount":1,"price":3.75}]},{"number":"2020-04-03-01","contents":[{"item":"Cheeseburger","amount":1,"price":8.5},{"item":"Water","amount":2,"price":1.5},{"item":"Coke","amount":2,"price":1.76},{"item":"Ice Cream","amount":1,"price":2.35}]}]""",
                    response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Get, "/order/2020-04-06-01").apply {
                assertEquals(
                    """{"number":"2020-04-06-01","contents":[{"item":"Ham Sandwich","amount":2,"price":5.5},{"item":"Water","amount":1,"price":1.5},{"item":"Beer","amount":3,"price":2.3},{"item":"Cheesecake","amount":1,"price":3.75}]}""",
                    response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Get, "/order/2020-04-06-01/total").apply {
                assertEquals(
                    """23.15""",
                    response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}
