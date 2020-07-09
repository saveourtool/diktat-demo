package org.cqfn.diktat.demo.controller

import com.pinterest.ktlint.core.ParseException

import org.cqfn.diktat.demo.processing.CodeFix
import org.cqfn.diktat.demo.views.CodeForm
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.io.File
import java.util.*
import javax.servlet.http.HttpServletRequest


/**
 * Important - that controller is not thread safe, because it saves the code into the file and stored locally on server
 */
@Controller
class DemoController {
    companion object {
        private var codeForm = CodeForm()
        private const val PAGE_NAME = "demo"
        private val file: File? = null
    }

    /**
     * simple redirect from host name to main page /demo
     */
    @RequestMapping(value = ["/"], method = [RequestMethod.GET])
    fun baseUrlRedirect(model: Model?): String? {
        return "redirect:/$PAGE_NAME"
    }

    @RequestMapping(value = ["/$PAGE_NAME"], method = [RequestMethod.POST])
    fun checkAndFixCode(request: HttpServletRequest, model: Model?, @ModelAttribute("codeForm") codeFormHtml: CodeForm): String {

        codeForm = codeFormHtml
        val codeFix = CodeFix(codeForm.initialCode!!,codeFormHtml.ruleSet[0])
        val file = getDemoFile()
        file.writeText(codeForm.initialCode!!)
        try {
            if (codeForm.fix) {
                codeForm.fixedCode = codeFix.fix(file.absolutePath)
            } else if (codeForm.check) {
                codeForm.fixedCode = codeForm.initialCode
                codeFix.check(file.absolutePath)
            }
        } catch (e: ParseException) {
            codeForm.warnings = listOf(e.toString())
        }
        codeForm.warnings = codeFix.listOfWarnings.map { "Warn (${it.line}:${it.col}) ${it.detail}" }
        file.delete()
        return PAGE_NAME
    }

    @RequestMapping(value = ["/$PAGE_NAME"], method = [RequestMethod.GET])
    fun buildMainPage(model: Model): String {
        model.addAttribute("codeForm", codeForm)
        model.addAttribute("result", file?.readText())
        return PAGE_NAME
    }

    private fun getDemoFile(): File {
        val filePath = javaClass.classLoader.getResource("demos/").path + "${generateFileName()}.kt"
        return File(filePath)
    }

    private @Synchronized fun generateFileName():String = UUID.randomUUID().toString()
}