package org.cqfn.diktat.demo.controller

import org.cqfn.diktat.demo.processing.CodeFix
import org.cqfn.diktat.demo.views.CodeForm
import org.cqfn.diktat.demo.views.RulesSetTypes

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.ParseException
import org.slf4j.LoggerFactory

import java.io.File
import java.util.UUID

/**
 * Main [Controller] for spring boot
 */
class DemoController {
    @Synchronized
    private fun generateFileName(): String = UUID.randomUUID().toString()

    private fun getDemoFile(): File = File("demo-${generateFileName()}.kt")

    /**
     * @param codeFormHtml
     * @return a page name
     */
    fun checkAndFixCode(codeFormHtml: CodeForm): CodeForm {
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
            else -> {
                val issueLink = if (RulesSetTypes.KTLINT in codeForm.ruleSet) "https://github.com/pinterest/ktlint/issues" else "https://github.com/cqfn/diktat/issues"
                codeForm.warnings = listOf(
                    """
                        |Unhandled exception during tool execution, please create a ticket at $issueLink:
                        |${result.exceptionOrNull()!!.stackTraceToString()}
                    """.trimMargin())
                log.error("Running formatter returned unexpected exception", result.exceptionOrNull())
            }
        }
        file.delete()
        return codeForm
    }

    private fun LintError.prettyFormat(file: File) = "Warn ($line:$col) $detail"
        .replace(file.absolutePath, "\"example_file_name\"")

    companion object {
        private val log = LoggerFactory.getLogger(DemoController::class.java)
        private const val PAGE_NAME = "demo"
    }
}
