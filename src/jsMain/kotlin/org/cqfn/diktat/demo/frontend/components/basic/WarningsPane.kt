package org.cqfn.diktat.demo.frontend.components.basic

import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import react.dom.br
import react.dom.button
import react.dom.div
import react.dom.render
import react.dom.span

import kotlinx.browser.document
import kotlinx.html.ButtonType
import kotlinx.html.id
import org.cqfn.diktat.demo.frontend.components.views.CodeFormProps

/**
 * A UI component to display warnings from [CodeForm.warnings]
 */
@Suppress("EMPTY_BLOCK_STRUCTURE_ERROR")
class WarningsPane : RComponent<CodeFormProps, State>() {
    override fun RBuilder.render() {
        div("row") {
            div("col-md-12") {
                div("alert alert-danger alert-dismissible") {
                    button(classes = "close", type = ButtonType.button) {
                        attrs {
                            attributes["data-dismiss"] = "alert"
                        }
                        span { +"×" }
                    }
                    div {
                        attrs.id = "warnings-list"
                    }
                }
            }
        }
    }

    override fun componentDidUpdate(prevProps: CodeFormProps,
                                    prevState: State,
                                    snapshot: Any,
    ) {
        if (props.codeForm == prevProps.codeForm) {
            return
        }
        render(document.getElementById("warnings-list")) {
            span {
                props.codeForm.warnings?.map { warning ->
                    warning.lines().take(10).map {
                        span {
                            +it
                        }
                        br { }
                    }
                }
            }
        }
    }
}
