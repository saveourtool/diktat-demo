package org.cqfn.diktat.demo.controller

import org.cqfn.diktat.demo.processing.CodeFix
import org.cqfn.diktat.demo.views.CodeForm

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.ParseException
import org.slf4j.LoggerFactory
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
        val result = runCatching {
            if (codeForm.fix) {
                codeForm.fixedCode = codeFix.fix(file.absolutePath)
            } else if (codeForm.check) {
                codeForm.fixedCode = codeForm.initialCode
                codeFix.check(file.absolutePath)
            }
        }
        when {
            result.isSuccess -> codeForm.warnings = codeFix.listOfWarnings.map { it.prettyFormat(file) }
            result.exceptionOrNull() is ParseException -> codeForm.warnings = listOf(result.exceptionOrNull().toString())
            else -> log.error("Running formatter returned unexpected exception", result.exceptionOrNull())
        }
        file.delete()
        return PAGE_NAME
    }

    /**
     * @param model
     */
    @RequestMapping(value = ["/"], method = [RequestMethod.GET])
    fun baseUrlRedirect(model: Model?) = "redirect:/$PAGE_NAME"

    private fun LintError.prettyFormat(file: File) = "Warn ($line:$col) $detail"
            .replace(file.absolutePath, "\"example_file_name\"")

    companion object {
        private val log = LoggerFactory.getLogger(DemoController::class.java)
        private const val PAGE_NAME = "demo"
    }
}
