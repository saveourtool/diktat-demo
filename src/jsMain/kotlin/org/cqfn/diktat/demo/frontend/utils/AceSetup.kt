/**
 * JS script for diktat-demo main page
 */

package org.cqfn.diktat.demo.frontend.utils

import org.w3c.dom.HTMLTextAreaElement

import kotlinx.browser.document

/**
 * Setup Ace editor by applying it to textareas for source and result.
 */
fun setupAceEditor() {
    `$`("document").ready {
        val editor = Ace.edit("editor")
        editor.setTheme("ace/theme/monokai")
        editor.getSession().setMode("ace/mode/kotlin")

        val result = Ace.edit("result")
        result.setTheme("ace/theme/monokai")
        result.getSession().setMode("ace/mode/kotlin")
        result.setReadOnly(true)

        val textarea = document.getElementById("source") as HTMLTextAreaElement
        editor.getSession().setValue(textarea.value)
        editor.getSession().on("change") {
            textarea.value = editor.getSession().getValue()
        }
    }
}
