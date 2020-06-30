package org.cqfn.diktat.demo.processing

import com.pinterest.ktlint.core.KtLint
import org.cqfn.diktat.ruleset.rules.DiktatRuleSetProvider
import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import org.cqfn.diktat.common.config.rules.RulesConfig
import java.util.ArrayList

/**
 * Entity that will store the information about warnings that will appear after the run of diktat
 * It's method fixCode will return the result of formatting, indents and newlines will be processed correctly ('\n')
 * @param code - initial code that should be formatted
 * @param rulesConfigList - list of customization for rules that are read from rules-config.json
 */
class CodeFix(private val code: String, private val typeRule: String?, private val rulesConfigList: List<RulesConfig>?) {
    var listOfWarnings: List<LintError> = emptyList()
    private var ruleSets:Iterable<RuleSet> = when(typeRule){
        "ktlint" -> listOf(StandardRuleSetProvider().get())
        else -> listOf(DiktatRuleSetProvider().get())
    }

    fun fix(absoluteFilePath: String): String {
        val res = ArrayList<LintError>()
        println(rulesConfigList)
        val formattedResult = KtLint.format(
                KtLint.Params(
                        fileName = absoluteFilePath,
                        text = code,
                        ruleSets = ruleSets,
                        cb = { e, _ -> res.add(e) },
                        rulesConfigList = rulesConfigList

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
                        cb = { e, _ -> res.add(e) },
                        rulesConfigList = rulesConfigList
                )
        )
        listOfWarnings = res
    }
}
