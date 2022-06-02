package org.cqfn.diktat.demo.frontend.components

import generated.PROJECT_REVISION
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.a
import react.dom.br
import react.dom.footer
import react.dom.p

class Footer: RComponent<Props, State>() {
    override fun RBuilder.render() {
        footer("footer") {
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
    }
}