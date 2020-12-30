package org.cqfn.diktat.demo.frontend

import org.cqfn.diktat.demo.frontend.components.WarningsPane
import org.cqfn.diktat.demo.frontend.utils.Ace
import org.cqfn.diktat.demo.frontend.utils.setupAceEditor
import org.cqfn.diktat.demo.views.CodeForm
import org.cqfn.diktat.demo.views.RulesSetTypes

import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.dom.get

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.RProps
import react.dom.button
import react.dom.render
import react.useState

@Suppress("TOO_LONG_FUNCTION")
fun main() {
    val form = document.getElementById("main-form") as HTMLFormElement
    val textarea = document.getElementById("source") as HTMLTextAreaElement
    val resultSession = Ace.edit("result").getSession()

    render(document.getElementById("warnings-pane")) {
        child(WarningsPane::class) {}
    }

    render(document.getElementById("submit-codeform-btn")) {
        button(classes = "btn btn-primary", type = ButtonType.submit) {
            +"Submit"
            // className(codeFormStore.tracker.map {
            // if (it) "spinner-border spinner-border-sm mr-2" else ""
            // })
        }
    }

    form.onsubmit = { event: Event ->
        GlobalScope.launch {
            uploadCodeForm("http://localhost:8082/demo",
                CodeForm(
                    initialCode = textarea.value,
                    check = (form.elements["check"] as HTMLInputElement).checked,
                    fix = (form.elements["fix"] as HTMLInputElement).checked,
                    ruleSet = (form.elements["rulSet-select"] as HTMLSelectElement)
                        .selectedOptions
                        .asList()
                        .map { (it as HTMLOptionElement).value.toUpperCase() }
                        .map(RulesSetTypes::valueOf)
                )
            )
                .apply {
                    fixedCode?.let { resultSession.setValue(it) }

                }
        }
        event.preventDefault()
    }

    setupAceEditor()
}

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
    .json()
    .await()
    .unsafeCast<CodeForm>()

external class CodeFormProps : RProps {
    var codeForm: CodeForm
}
