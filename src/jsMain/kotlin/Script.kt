import kotlin.browser.document
import kotlin.js.*
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.HTMLElement

@JsModule("ace")
@JsNonModule
@JsName("ace")
external object Ace {
    @JsName("edit")
    fun edit(s: String): Editor
}

@JsModule("ace")
@JsNonModule
external class Editor {
    fun setTheme(path: String)
    fun getSession(): Session
    fun setReadOnly(b: Boolean)

    class Session {
        fun setMode(mode: String)
        fun getValue(): String
        fun setValue(s: String)
        fun on(eventName: String, f: () -> Unit)
    }
}

@JsName("$")
external fun jQuery(s: String): JQuery

external class JQuery {
    fun ready(f: () -> Unit)
}

fun getFile() = (document.getElementById("upfile") as HTMLElement).click()

// todo unused?
//fun sub(obj: Any) {
//    val file = obj.value
//    val filePathParts = file.split("\\")
//    document.getElementById("yourBtn").innerHTML = filePathParts.last()
//    document.myForm.submit()
//}

fun main() {
    jQuery("document").ready {
        val editor = Ace.edit("editor")
        editor.setTheme("ace/theme/monokai")
        editor.getSession().setMode("ace/mode/kotlin")

        val result = Ace.edit("result");
        result.setTheme("ace/theme/monokai");
        result.getSession().setMode("ace/mode/kotlin");
        result.setReadOnly(true);

        val textarea = document.getElementById("source") as HTMLTextAreaElement
        editor.getSession().setValue(textarea.value)
        editor.getSession().on("change") {
            textarea.value = editor.getSession().getValue()
        }
    }
}