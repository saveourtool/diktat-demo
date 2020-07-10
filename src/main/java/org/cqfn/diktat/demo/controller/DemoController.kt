package org.cqfn.diktat.demo.controller

import com.pinterest.ktlint.core.ParseException

import org.cqfn.diktat.demo.processing.CodeFix
import org.cqfn.diktat.demo.views.CodeForm
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.io.File
import java.util.*
import javax.servlet.http.HttpServletRequest

@Controller
class DemoController {
    companion object {
        private const val PAGE_NAME = "demo"
    }

    @RequestMapping(value = ["/"], method = [RequestMethod.GET])
    fun baseUrlRedirect(model: Model?): String? {
        return "redirect:/$PAGE_NAME"
    }

    @RequestMapping(value = ["/$PAGE_NAME"], method = [RequestMethod.POST])
    fun checkAndFixCode(request: HttpServletRequest, model: Model?, @ModelAttribute("codeForm") codeFormHtml: CodeForm): String {

        val codeForm = codeFormHtml
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
        model.addAttribute("codeForm", CodeForm())
        return PAGE_NAME
    }

    private fun getDemoFile(): File {
        val filePath = javaClass.classLoader.getResource("demos/").path + "${generateFileName()}.kt"
        return File(filePath)
    }

    @Synchronized
    private fun generateFileName():String = UUID.randomUUID().toString()
}