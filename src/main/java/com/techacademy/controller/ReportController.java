package com.techacademy.controller;

import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final ReportService reportService;
    private final EmployeeService employeeService;

    //依存性の注入
    @Autowired
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    /// 日報一覧画面
@GetMapping
public String list(Model model) {
    List<Report> reports = reportService.findAll();
    for (Report report : reports) {
        // 従業員名を取得して設定
        String authorName = employeeService.findByCode(report.getEmployeeCode()).getName();
        report.setAuthorName(authorName);  // 修正箇所: setAuthorName メソッドを使用
    }
    model.addAttribute("reportList", reports);
    model.addAttribute("listSize", reports.size());
    return "reports/list";
}
//日報詳細画面
@GetMapping(value = "/{id}/")
public String detail(@PathVariable Long id, Model model) {
 Report report = reportService.findById(id);
 if (report != null) {
     String authorName = employeeService.findByCode(report.getEmployeeCode()).getName();
     report.setAuthorName(authorName);  // 修正箇所: setAuthorName メソッドを使用
     model.addAttribute("report", report);
     return "reports/detail";
 }
 return "redirect:/reports";
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