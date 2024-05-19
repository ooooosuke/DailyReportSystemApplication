package com.techacademy.controller;

import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

//日報新規登録画面
@GetMapping(value = "/add")
public String create(Model model, @AuthenticationPrincipal UserDetail userDetail) {
    // 新しい Report オブジェクトをモデルに追加
    model.addAttribute("report", new Report());
 // ログイン中の従業員情報をモデルに追加
    model.addAttribute("authorName", userDetail.getEmployee().getName());
    return "reports/new";
}

// 日報新規登録処理
@PostMapping(value = "/add")
public String add(@ModelAttribute Report report, Model model) {
    // 現在の従業員コードを設定（例: ログイン中の従業員コード）
    // ここでは仮に "E001" とします。実際のアプリケーションではログイン情報を使用してください。
    report.setEmployeeCode("E001");

    // 日報を保存
    reportService.save(report);
    return "redirect:/reports";
}

// 日報更新画面
@GetMapping(value = "/{id}/update")
public String showUpdateReportForm(@PathVariable Long id, Model model) {
    Report report = reportService.findById(id);
    if (report != null) {
        model.addAttribute("report", report);
        return "reports/update";
    }
    return "redirect:/reports";
}

// 日報更新処理
@PostMapping(value = "/{id}/update")
public String updateReport(@PathVariable Long id, @ModelAttribute Report report, Model model) {
    reportService.updateReport(id, report);
    return "redirect:/reports";
}

// 日報削除処理
@PostMapping(value = "/{id}/delete")
public String delete(@PathVariable Long id, Model model) {
    reportService.delete(id);
    return "redirect:/reports";
}
}