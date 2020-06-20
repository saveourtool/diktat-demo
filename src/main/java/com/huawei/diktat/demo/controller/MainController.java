package com.huawei.diktat.demo.controller;

import com.huawei.diktat.demo.processing.CodeFix;
import com.huawei.diktat.demo.views.CodeForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class MainController {
    private static CodeForm codeForm = new CodeForm();
    private static CodeFix codeFix = new CodeFix();

    @RequestMapping(value = {"/index"}, method = RequestMethod.POST)
    public String saveCode(Model model, @ModelAttribute("codeForm") CodeForm codeForm) {
        MainController.codeForm = codeForm;
        System.out.println(codeForm.getCheckFunctionBooleanPrefix());
        System.out.println(codeForm.getCheckEnumValue());
        System.out.println(codeForm.getCheckGenericName());
        System.out.println(codeForm.getCheckIdentifierLength());
        return "index";
    }

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String indexPage(Model model) {
        model.addAttribute("codeForm", codeForm);
        return "index";
    }
}
