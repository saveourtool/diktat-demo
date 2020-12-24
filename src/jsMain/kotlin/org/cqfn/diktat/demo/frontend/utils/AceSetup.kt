/**
 * JS script for diktat-demo main page
 */

// todo fix and usuppress
@file:Suppress("PACKAGE_NAME_MISSING", "MISSING_KDOC_TOP_LEVEL", "MISSING_KDOC_ON_FUNCTION", "MISSING_KDOC_CLASS_ELEMENTS",
        "KDOC_WITHOUT_PARAM_TAG", "KDOC_WITHOUT_RETURN_TAG")

package org.cqfn.diktat.demo.frontend.utils

import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import js.externals.jquery.`$`

import kotlinx.browser.document

fun getFile() = (document.getElementById("upfile") as HTMLElement).click()

// todo unused?
// fun sub(obj: Any) {
// val file = obj.value
// val filePathParts = file.split("\\")
// document.getElementById("yourBtn").innerHTML = filePathParts.last()
// document.myForm.submit()
// }

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
