package org.cqfn.diktat.demo.frontend

import dev.fritz2.binding.RootStore
import dev.fritz2.dom.html.render
import dev.fritz2.dom.mount
import dev.fritz2.remote.Request
import dev.fritz2.remote.getBody
import dev.fritz2.tracking.tracker
import kotlinx.browser.document
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

@ExperimentalCoroutinesApi
@FlowPreview
fun main() {
    val form = document.getElementById("main-form") as HTMLFormElement
    val textarea = document.getElementById("source") as HTMLTextAreaElement
    val resultSession = Ace.edit("result").getSession()

    val codeFormStore = object : RootStore<CodeForm>(CodeForm(), "codeFormStore") {
        val tracker = tracker()

        val api = Request("http://localhost:8082/demo")
            .acceptJson()
            .contentType("application/json")

        val uploadCodeForm = handle<CodeForm> { _, codeForm ->
            tracker.track("codeFormUploading") {
                api.body(Json.encodeToString(codeForm))
                    .post()
                    .getBody()
                    .let<String, CodeForm> { Json.decodeFromString(it) }
                    .also { newCodeForm ->
                        // todo do it proper way
                        newCodeForm.fixedCode?.let {
                            resultSession.setValue(it)
                        }
                    }
            }
        }
    }

    WarningsPane(codeFormStore).warningsPane.mount("warnings-pane")

    render {
        button("btn btn-primary") {
            type("submit")
            +"Submit"
//            className(codeFormStore.tracker.map {
//                if (it) "spinner-border spinner-border-sm mr-2" else ""
//            })
        }
    }.mount("submit-codeform-btn")

    form.onsubmit = { event: Event ->
        codeFormStore.uploadCodeForm(
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
        event.preventDefault()
    }

    setupAceEditor()
}