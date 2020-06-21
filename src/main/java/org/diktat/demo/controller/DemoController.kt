package org.diktat.demo.controller

import org.diktat.demo.processing.CodeFix
import org.diktat.demo.views.CodeForm
import com.pinterest.ktlint.core.ParseException
import config.rules.RulesConfig
import config.rules.RulesConfigReader
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.servlet.http.HttpServletRequest


/**
 * Important - that controller is not thread safe, because it saves the code into the file and stored locally on server
 */
@Controller
class DemoController {
    companion object {
        private var codeForm = CodeForm()
        private var codeFix = CodeFix("", emptyList())
        private val log = LoggerFactory.getLogger(DemoController::class.java)
        private const val PAGE_NAME = "index"
    }

    @RequestMapping(value = ["/$PAGE_NAME"], method = [RequestMethod.POST])
    fun saveCode(request: HttpServletRequest, model: Model?, @ModelAttribute("codeForm") codeFormHtml: CodeForm): String {
        val configFile = codeFormHtml.diktatConfigFile
        val kotlinRuleSetConfig = if (configFile != null) {
            loadConfigRules(configFile, request)
        } else {
            emptyList()
        }

        codeForm = codeFormHtml
        codeFix = CodeFix(codeForm.initialCode!!, kotlinRuleSetConfig)
        getDemoFile().writeText(codeForm.initialCode!!)
        try {
            if (codeForm.fix) {
                codeForm.fixedCode = codeFix.checkAndFixCode(getDemoFile().absolutePath)
            } else if (codeForm.check) {
                codeForm.fixedCode = codeForm.initialCode
                codeFix.checkAndFixCode(getDemoFile().absolutePath)
            }
        } catch (e: ParseException) {
            codeForm.warnings = listOf(e.toString())
        }
        codeForm.warnings = codeFix.listOfWarnings.map { "Warn (${it.line}:${it.col}) ${it.detail}" }

        return PAGE_NAME
    }

    @RequestMapping(value = ["/$PAGE_NAME"], method = [RequestMethod.GET])
    fun indexPage(model: Model): String {
        model.addAttribute("codeForm", codeForm)
        model.addAttribute("codeFix", codeFix)
        model.addAttribute("result", getDemoFile().readText())
        return PAGE_NAME
    }

    private fun getDemoFile(): File {
        val fileURL = javaClass.classLoader.getResource("demos/DemoTestFile.kt")
        return File(fileURL!!.file)
    }

    /**
     * Method for uploading json configuration with rules and parsing it.
     */
    private fun loadConfigRules(fileDatas: Array<MultipartFile>, request: HttpServletRequest): List<RulesConfig> {
        val uploadRootPath: String = request.servletContext.getRealPath("upload")
        val uploadRootDir = File(uploadRootPath)
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs()
        }

        val fileData = fileDatas[0]

        val name = fileData.originalFilename
        val serverFile = File(uploadRootDir.absolutePath + File.separator + name)
        try {
            val stream = BufferedOutputStream(FileOutputStream(serverFile))
            stream.write(fileData.bytes)
            stream.close()
            log.info("Writing result to file: $serverFile")
        } catch (e: IOException) {
            log.error("Error processing the file: $name", e)
        }

        return RulesConfigReader().parseResource(serverFile)
    }
}
