package org.cqfn.diktat.demo.frontend.components

import org.cqfn.diktat.demo.views.CodeForm

import dev.fritz2.binding.RootStore
import dev.fritz2.dom.html.render

/**
 * A UI component to display warnings from [CodeForm.warnings]
 */
@Suppress("USE_DATA_CLASS", "EMPTY_BLOCK_STRUCTURE_ERROR")
class WarningsPane(codeFormStore: RootStore<CodeForm>) {
    /**
     * A List of Tags that can be mounted into DOM.
     */
    val warningsPane = render {
        div("row") {
            div("col-md-12") {
                div("alert alert-danger alert-dismissible") {
                    button(baseClass = "close") {
                        type("button")
                        attr("data-dismiss", "alert")
                        span { +"×" }
                    }
                    codeFormStore.data.renderElement { codeForm ->
                        span {
                            codeForm.warnings?.map {
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
    }
}
