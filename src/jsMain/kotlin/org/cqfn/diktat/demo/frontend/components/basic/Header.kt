package org.cqfn.diktat.demo.frontend.components.basic

import react.RBuilder
import react.dom.*

fun RBuilder.renderHeader() {
    div("row") {
        div("col-sm-12 d-flex justify-content-center") {
            a(href = "https://github.com/cqfn/diKTat") {
                img(src = "img/demo.png", classes = "img-fluid") {}
            }
        }
    }
    div("row") {
        div(classes = "col-md-12") {
            h1 {
                +"KTlint online demo"
            }
            p {
                +"Below you can choose which rule set to use to fix or check your code: "
                a(href = "https://ktlint.github.io/") {
                    +"KTlint standard"
                }
                +" or custom diktat* rule set"
            }
            p {
                +"* "
                a(href = "https://github.com/cqfn/diKTat") {
                    +"diKTat"
                }
                +" is a codestyle and linter for Kotlin based on awesome KTlint (you can create your own style)"
            }
        }
    }
}