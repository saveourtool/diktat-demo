/**
 * JS script for diktat-demo main page
 */

package org.cqfn.diktat.demo.frontend.utils

import js.externals.jquery.`$`
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement

import kotlinx.browser.document

@Suppress("MISSING_KDOC_TOP_LEVEL", "MISSING_KDOC_ON_FUNCTION")  // what is this function for? it's unused for a long time...
fun getFile() = (document.getElementById("upfile") as HTMLElement).click()

// todo unused?
// fun sub(obj: Any) {
// val file = obj.value
// val filePathParts = file.split("\\")
// document.getElementById("yourBtn").innerHTML = filePathParts.last()
// document.myForm.submit()
// }

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
