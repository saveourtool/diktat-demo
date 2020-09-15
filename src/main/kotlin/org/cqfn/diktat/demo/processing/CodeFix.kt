package org.cqfn.diktat.demo.processing

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import java.util.ArrayList
import org.cqfn.diktat.demo.views.RulesSetType
import org.cqfn.diktat.ruleset.rules.DiktatRuleSetProvider

/**
 * Entity that will store the information about warnings that will appear after the run of diktat
 * It's method fixCode will return the result of formatting, indents and newlines will be processed correctly ('\n')
 *
 * @param code - initial code that should be formatted
 * @param typeRule - type of rules set
 */
class CodeFix(private val code: String, typeRule: RulesSetType) {
    var listOfWarnings: List<LintError> = emptyList()
    private val ruleSets = when(typeRule){
        RulesSetType.ktlint -> listOf(StandardRuleSetProvider().get())
        RulesSetType.diKTat -> listOf(DiktatRuleSetProvider().get())
    }

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
