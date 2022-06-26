package com.saveourtool.diktat.demo.processing

import com.saveourtool.diktat.demo.views.RulesSetTypes

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import org.cqfn.diktat.ruleset.rules.DiktatRuleSetProvider

import java.util.ArrayList

/**
 * Entity that will store the information about warnings that will appear after the run of diktat
 * It's method fixCode will return the result of formatting, indents and newlines will be processed correctly ('\n')
 *
 * @param code - initial code that should be formatted
 * @param typeRule - type of rules set
 * @param diktatConfigFilePath uploaded config file
 */
class CodeFix(
    private val code: String,
    typeRule: RulesSetTypes,
    diktatConfigFilePath: String? = null,
) {
    /**
     * a list for accumulating lint errors
     */
    var listOfWarnings: List<LintError> = emptyList()
    private val ruleSets = when (typeRule) {
        RulesSetTypes.KTLINT -> listOf(StandardRuleSetProvider().get())
        RulesSetTypes.DIKTAT -> diktatConfigFilePath?.let {
            println("Config found")
            listOf(DiktatRuleSetProvider(it).get())
        } ?: listOf(DiktatRuleSetProvider().get())
    }

    /**
     * @param absoluteFilePath
     * @return corrected input text
     */
    fun fix(absoluteFilePath: String): String {
        val res: ArrayList<LintError> = ArrayList()
        val formattedResult = KtLint.format(
            KtLint.ExperimentalParams(
                fileName = absoluteFilePath,
                text = code,
                ruleSets = ruleSets,
                cb = { lintError, _ -> res.add(lintError) },
            ),
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
            KtLint.ExperimentalParams(
                fileName = absoluteFilePath,
                text = code,
                ruleSets = ruleSets,
                cb = { lintError, _ -> res.add(lintError) },
            ),
        )
        listOfWarnings = res
    }
}
