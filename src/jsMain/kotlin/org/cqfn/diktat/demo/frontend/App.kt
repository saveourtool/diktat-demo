/**
 * Main entrypoint for diktat-demo frontend
 */

package org.cqfn.diktat.demo.frontend

import org.cqfn.diktat.demo.frontend.components.EditorForm
import org.cqfn.diktat.demo.frontend.utils.setupAceEditor
import org.cqfn.diktat.demo.views.CodeForm

import generated.PROJECT_REVISION
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.dom.a
import react.dom.br
import react.dom.p
import react.dom.render

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Suppress("EMPTY_BLOCK_STRUCTURE_ERROR")
fun main() {
    render(document.getElementById("main-form-div")) {
        child(EditorForm::class) {}
    }

    render(document.getElementById("footer")) {
        p("text-center") {
            +"This demo is used to show how KTlint framework is powerful and how diKTat rule set can fix your code. Sources are "
            a(href = "https://github.com/akuleshov7/diKTat-demo") { +"here" }
            br { }
            +"Inspired by https://yapf.now.sh/"
        }
        p("text-center") {
            +"Revision $PROJECT_REVISION"
        }
    }

    setupAceEditor()
}

/**
 * Send data from CodeForm to backend and asynchronously receive response
 *
 * @param url url to POST data to
 * @param codeForm a [CodeForm] with data from frontend, that will be sent
 */
suspend fun uploadCodeForm(url: String, codeForm: CodeForm) = window
    .fetch(
        url,
        RequestInit(
            "POST",
            headers = Headers().also {
                it.set("Content-Type", "application/json")
            },
            body = Json.encodeToString(codeForm)
        )
    )
    .await()
    .text()
    .await()
    .let { Json.decodeFromString<CodeForm>(it) }
