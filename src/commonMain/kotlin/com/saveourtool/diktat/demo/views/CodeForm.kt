package com.saveourtool.diktat.demo.views

import kotlinx.serialization.Serializable

/**
 * A class containing data associated with text form on frontend
 * @property initialCode
 * @property fix do not rename it to isFix or isCheck as it will break thymeleaf as getters are autogenerated
 * @property check
 * @property ruleSet
 * @property fixedCode
 * @property warnings
 * @property diktatConfig
 */
@Serializable data class CodeForm(
    var initialCode: String? = null,
    var fix: Boolean = false,
    var check: Boolean = false,
    var ruleSet: List<RulesSetTypes> = listOf(RulesSetTypes.DIKTAT, RulesSetTypes.KTLINT),
    var fixedCode: String? = null,
    var warnings: List<String>? = null,
    var diktatConfig: String? = null,
)