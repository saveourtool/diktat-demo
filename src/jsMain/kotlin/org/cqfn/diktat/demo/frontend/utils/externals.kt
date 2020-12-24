package org.cqfn.diktat.demo.frontend.utils

@JsModule("ace-builds")
@JsNonModule
@JsName("ace")
external object Ace {
    @JsName("edit")
    fun edit(editorName: String): Editor
}

@JsModule("ace-code-editor")
@JsNonModule
external class Editor {
    /**
     * @param path path to ace theme
     */
    fun setTheme(path: String)

    fun getSession(): Session

    fun setReadOnly(readOnly: Boolean)

    class Session {
        fun setMode(mode: String)

        fun getValue(): String

        fun setValue(text: String)

        fun on(eventName: String, handler: () -> Unit)
    }
}
