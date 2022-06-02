/**
 * Main entrypoint for diktat-demo frontend
 */

package org.cqfn.diktat.demo.frontend

import org.cqfn.diktat.demo.views.CodeForm

import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.dom.render

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.cqfn.diktat.demo.frontend.components.Footer
import org.cqfn.diktat.demo.frontend.components.basic.renderHeader
import react.*
import react.dom.div
import react.router.Routes
import react.router.dom.HashRouter

//external interface AppState: State {}

class App: RComponent<Props, State>() {

    override fun RBuilder.render() {
        HashRouter {
            renderHeader()
            div("container-fluid") {
                Routes {
//                    Route {
//                        attrs {
//                            path = "/"
//                            element = buildElement {
//                                child(EditorForm::class) {}
//                            }
//                        }
//                    }

//                    Route {
//                        attrs {
//                            path = "/config/edit"
//                            element = buildElement {
//                                child(EditConfigView::class) {}
//                            }
//                        }
//                    }
                }
            }
            child(Footer::class) {}
        }
//        setupAceEditor()
    }
}

@Suppress("EMPTY_BLOCK_STRUCTURE_ERROR")
fun main() {
    render(document.getElementById("main-form-div")) {
        child(App::class) {}
    }
}

/**
 * Send data from CodeForm to backend and asynchronously receive response
 *
 * @param url url to POST data to
 * @param codeForm a [CodeForm] with data from frontend, that will be sent
 * @return CodeForm with corrected data
 */
suspend fun uploadCodeForm(url: String, codeForm: CodeForm) = window
    .fetch(
        url,
        RequestInit(
            "POST",
            headers = Headers().also {
                it.set("Content-Type", "application/json")
            },
            body = Json.encodeToString(codeForm),
        ),
    )
    .await()
    .text()
    .await()
    .let { Json.decodeFromString<CodeForm>(it) }
