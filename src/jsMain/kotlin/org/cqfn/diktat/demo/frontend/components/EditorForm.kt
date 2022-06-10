/**
 * Main parts of frontend UI
 */

package org.cqfn.diktat.demo.frontend.components

import org.cqfn.diktat.demo.frontend.uploadCodeForm
import org.cqfn.diktat.demo.frontend.utils.Ace
import org.cqfn.diktat.demo.views.CodeForm
import org.cqfn.diktat.demo.views.RulesSetTypes

import generated.DIKTAT_VERSION
import generated.KTLINT_VERSION
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.dom.get
import org.w3c.files.FileReader
import react.Props
import react.PropsWithChildren
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import react.dom.br
import react.dom.button
import react.dom.div
import react.dom.form
import react.dom.input
import react.dom.label
import react.dom.option
import react.dom.select
import react.dom.setProp
import react.dom.span
import react.dom.textarea
import react.setState

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction

/**
 * [RProps] implementation to store [CodeForm]
 */
external interface CodeFormProps : PropsWithChildren {
    /**
     * A [CodeForm]
     */
    var codeForm: CodeForm
}

/**
 * [State] implementation to store [CodeForm]
 */
external interface CodeFormState : State {
    /**
     * A [CodeForm]
     */
    var codeForm: CodeForm
}

/**
 * A component for a form, where initial and fixed code are displayed and linter settings are set.
 */
class EditorForm : RComponent<Props, CodeFormState>() {
    init {
        state.codeForm = CodeForm()
    }

    @Suppress("TOO_LONG_FUNCTION", "EMPTY_BLOCK_STRUCTURE_ERROR")
    override fun RBuilder.render() {
        form {
            div {
                attrs.id = "warnings-pane"
                child(WarningsPane::class) {
                    attrs {
                        codeForm = state.codeForm
                    }
                }
            }
            div("row try-wrapper") {
                span("arrow glyphicon glyphicon-arrow-right") {}
                div("col-md-6") {
                    div("form-group") {
                        div("form-group") {
                            div("ace_editor ace-monokai ace_dark") {
                                attrs.id = "editor"
                            }
                            textarea(classes = "source") {
                                attrs.id = "source"
                                attrs.name = "source"
                            }
                        }
                    }
                }
                div("col-md-6") {
                    div("ace_editor ace-monokai ace_dark") {
                        attrs.id = "result"
                    }
                }
            }
            div("row") {
                setProp("align", "center")
                div("col-sm") {
                    input(InputType.checkBox, classes = "form-check-input", name = "check") {
                        attrs.id = "check"
                        attrs.value = "radioChk"
                    }
                    label("form-check-label") {
                        attrs.htmlFor = "check"
                        +"Check"
                    }

                    input(InputType.checkBox, classes = "form-check-input", name = "fix") {
                        attrs.id = "fix"
                        attrs.value = "radioChk"
                    }
                    label("form-check-label") {
                        attrs.htmlFor = "fix"
                        +"Fix"
                    }
                }
                div("rulSet") {
                    setProp("align", "center")
                    label {
                        +"Choose rules set provider:"
                    }
                    select {
                        attrs.name = "rulSet-select"
                        attrs.id = "rulSet-select"
                        option {
                            attrs.value = RulesSetTypes.KTLINT.name
                            +"ktlint ($KTLINT_VERSION)"
                        }
                        option {
                            attrs {
                                value = RulesSetTypes.DIKTAT.name
                                selected = true
                            }
                            +"diKTat ($DIKTAT_VERSION)"
                        }
                    }
                }
            }
            br {}
            div("row") {
                setProp("align", "center")
                div("upload-btn-wrapper") {
                    button(classes = "btn") {
                        +"Upload config"
                        input(type = InputType.file, name = "diktat-analysis.yml") {
                            attrs.accept = ".yml,.yaml"
                            attrs.onChangeFunction = { event ->
                                val target = event.target as HTMLInputElement
                                target.files?.asList()?.firstOrNull()?.let { file ->
                                    val reader = FileReader().apply {
                                        onload = { event: Event ->
                                            val text = event.target.asDynamic().result.toString()
                                            setState {
                                                codeForm = codeForm.copy(diktatConfig = text)
                                            }
                                        }
                                    }
                                    reader.readAsText(file)
                                }
                            }
                        }
                    }
                }
            }
            div("row") {
                setProp("align", "center")
                div("row") {
                    setProp("align", "center")
                    br {}
                    button(classes = "btn btn-primary", type = ButtonType.submit) {
                        +"Submit"
                        // todo: loading animation while results are calculated
                    }
                }
            }
            attrs {
                id = "main-form"
                onSubmitFunction = { event: Event ->
                    console.log(state.codeForm.diktatConfig)
                    event.preventDefault()
                    GlobalScope.launch {
                        val form = document.getElementById("main-form") as HTMLFormElement
                        val textarea = document.getElementById("source") as HTMLTextAreaElement
                        val resultSession = Ace.edit("result").getSession()
                        uploadCodeForm("${window.location.origin}/demo",
                            CodeForm(
                                initialCode = textarea.value,
                                check = (form.elements["check"] as HTMLInputElement).checked,
                                fix = (form.elements["fix"] as HTMLInputElement).checked,
                                ruleSet = (form.elements["rulSet-select"] as HTMLSelectElement)
                                    .selectedOptions
                                    .asList()
                                    .map { (it as HTMLOptionElement).value.uppercase() }
                                    .map(RulesSetTypes::valueOf),
                                diktatConfig = state.codeForm.diktatConfig,
                            ),
                        )
                            .apply {
                                fixedCode?.let { resultSession.setValue(it) }
                                setState {
                                    codeForm = this@apply
                                }
                            }
                    }
                }
            }
        }
    }
}
