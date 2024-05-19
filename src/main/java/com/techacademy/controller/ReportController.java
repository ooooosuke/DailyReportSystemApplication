package com.techacademy.controller;

import com.techacademy.entity.Report;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("reports")
public class ReportController {

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {
        model.addAttribute("reportList", List.of(new Report())); // 実際にはリポジトリからデータを取得
        model.addAttribute("listSize", 1); // 実際のサイズに合わせる
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Long id, Model model) {
        Report report = new Report(); // モックデータ
        report.setId(id);
        model.addAttribute("report", report); // モデルに追加
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(Model model) {
        model.addAttribute("report", new Report()); // モデルに新しいReportオブジェクトを追加
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@ModelAttribute Report report, Model model) {
        // モック処理を追加しておく
        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String showUpdateReportForm(@PathVariable Long id, Model model) {
        Report report = new Report(); // モックデータ
        report.setId(id);
        model.addAttribute("report", report); // モデルに追加
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    public String updateReport(@PathVariable Long id, @ModelAttribute Report report, Model model) {
        // モック処理を追加しておく
        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Long id, Model model) {
        // モック処理を追加しておく
        return "redirect:/reports";
    }
}