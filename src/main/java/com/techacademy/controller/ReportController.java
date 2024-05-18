package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("reports")
public class ReportController {
 // 日報一覧画面
    @GetMapping
    public String list(Model model) {
        // 実際には、レポートリストを取得してモデルに追加するコードが必要
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Long id, Model model) {
        // 実際には、レポートを取得してモデルに追加するコードが必要
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create() {
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add() {
        // 実際には、レポートを保存するコードが必要
        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String showUpdateReportForm(@PathVariable Long id, Model model) {
        // 実際には、レポートを取得してモデルに追加するコードが必要
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    public String updateReport() {
        // 実際には、レポートを更新するコードが必要
        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Long id, Model model) {
        // 実際には、レポートを削除するコードが必要
        return "redirect:/reports";
    }
}


