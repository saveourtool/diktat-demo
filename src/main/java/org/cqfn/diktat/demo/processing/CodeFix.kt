package org.cqfn.diktat.demo.processing

import com.pinterest.ktlint.core.KtLint
import org.cqfn.diktat.ruleset.rules.DiktatRuleSetProvider
import com.pinterest.ktlint.core.LintError
import org.cqfn.diktat.common.config.rules.RulesConfig
import java.util.ArrayList

/**
 * Entity that will store the information about warnings that will appear after the run of diktat
 * It's method fixCode will return the result of formatting, indents and newlines will be processed correctly ('\n')
 * @param code - initial code that should be formatted
 * @param rulesConfigList - list of customization for rules that are read from rules-config.json
 */
class CodeFix(private val code: String, private val rulesConfigList: List<RulesConfig>?) {
    var listOfWarnings: List<LintError> = emptyList()
    private val ruleSets = listOf(DiktatRuleSetProvider().get())

    fun fix(absoluteFilePath: String): String {
        val res = ArrayList<LintError>()
        val formattedResult = KtLint.format(
                KtLint.Params(
                        fileName = absoluteFilePath,
                        text = code,
                        ruleSets = ruleSets,
                        cb = { e, _ -> res.add(e) }
                )
        )
        listOfWarnings = res
        return formattedResult
    }

    fun check(absoluteFilePath: String) {
        val res = ArrayList<LintError>()
        KtLint.lint(
                KtLint.Params(
                        fileName = absoluteFilePath,
                        text = code,
                        ruleSets = ruleSets,
                        cb = { e, _ -> res.add(e) }
                )
        )
        listOfWarnings = res
    }
}
