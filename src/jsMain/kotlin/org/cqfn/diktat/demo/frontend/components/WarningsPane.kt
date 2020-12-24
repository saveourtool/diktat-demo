package org.cqfn.diktat.demo.frontend.components

import dev.fritz2.binding.RootStore
import dev.fritz2.dom.html.render
import org.cqfn.diktat.demo.views.CodeForm

class WarningsPane(codeFormStore: RootStore<CodeForm>) {
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