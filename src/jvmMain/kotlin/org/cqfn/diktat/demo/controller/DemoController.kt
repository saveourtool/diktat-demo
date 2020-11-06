package org.cqfn.diktat.demo.controller

import org.cqfn.diktat.demo.processing.CodeFix
import org.cqfn.diktat.demo.views.CodeForm

import com.pinterest.ktlint.core.ParseException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import java.io.File
import java.util.UUID
import javax.servlet.http.HttpServletRequest

/**
 * Main [Controller] for spring boot
 */
@Controller
class DemoController {
    @Synchronized
    private fun generateFileName(): String = UUID.randomUUID().toString()

    private fun getDemoFile(): File = File("demo-${generateFileName()}.kt")

    /**
     * @param model a [Model] from frontend
     * @return a page name
     */
    @RequestMapping(value = ["/$PAGE_NAME"], method = [RequestMethod.GET])
    fun buildMainPage(model: Model): String {
        model.addAttribute("codeForm", CodeForm())
        return PAGE_NAME
    }

    /**
     * @param request
     * @param model
     * @param codeFormHtml
     * @return a page name
     */
    @RequestMapping(value = ["/$PAGE_NAME"], method = [RequestMethod.POST])
    fun checkAndFixCode(request: HttpServletRequest,
                        model: Model?,
                        @ModelAttribute("codeForm") codeFormHtml: CodeForm): String {
        val codeForm = codeFormHtml
        val codeFix = CodeFix(codeForm.initialCode!!, codeFormHtml.ruleSet[0])
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
        codeForm.warnings = codeFix.listOfWarnings
                .map { "Warn (${it.line}:${it.col}) ${it.detail}" }
                .map { it.replace(file.absolutePath, "\"example_file_name\"") }
        file.delete()
        return PAGE_NAME
    }

    /**
     * @param model
     */
    @RequestMapping(value = ["/"], method = [RequestMethod.GET])
    fun baseUrlRedirect(model: Model?) = "redirect:/$PAGE_NAME"

    companion object {
        private const val PAGE_NAME = "demo"
    }
}
