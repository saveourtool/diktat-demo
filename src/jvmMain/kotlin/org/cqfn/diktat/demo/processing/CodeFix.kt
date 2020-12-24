package org.cqfn.diktat.demo.processing

import org.cqfn.diktat.demo.views.RulesSetTypes
import org.cqfn.diktat.ruleset.rules.DiktatRuleSetProvider

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider

import java.util.ArrayList

/**
 * Entity that will store the information about warnings that will appear after the run of diktat
 * It's method fixCode will return the result of formatting, indents and newlines will be processed correctly ('\n')
 *
 * @param code - initial code that should be formatted
 * @param typeRule - type of rules set
 */
class CodeFix(private val code: String, typeRule: RulesSetTypes) {
    /**
     * a list for accumulating lint errors
     */
    var listOfWarnings: List<LintError> = emptyList()
    private val ruleSets = when (typeRule) {
        RulesSetTypes.KTLINT -> listOf(StandardRuleSetProvider().get())
        RulesSetTypes.DIKTAT -> listOf(DiktatRuleSetProvider().get())
    }

    /**
     * @param absoluteFilePath
     * @return corrected input text
     */
    fun fix(absoluteFilePath: String): String {
        val res: ArrayList<LintError> = ArrayList()
        val formattedResult = KtLint.format(
            KtLint.Params(
                fileName = absoluteFilePath,
                text = code,
                ruleSets = ruleSets,
                cb = { lintError, _ -> res.add(lintError) }
            )
        )
        listOfWarnings = res
        return formattedResult
    }

    /**
     * @param absoluteFilePath
     */
    fun check(absoluteFilePath: String) {
        val res: ArrayList<LintError> = ArrayList()
        KtLint.lint(
            KtLint.Params(
                fileName = absoluteFilePath,
                text = code,
                ruleSets = ruleSets,
                cb = { lintError, _ -> res.add(lintError) }
            )
        )
        listOfWarnings = res
    }
}
