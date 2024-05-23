package com.techacademy.controller;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("reports")
public class ReportController {
    private final ReportService reportService;

    //依存性の注入
    @Autowired
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
    }

    /// 日報一覧画面
    @GetMapping
    public String list(Model model) {
        List<Report> reports = reportService.findAll();
        for (Report report : reports) {
            String authorName = report.getEmployee().getName(); // 従業員名を取得して設定
            report.setAuthorName(authorName);
        }
    model.addAttribute("reportList", reports);
    model.addAttribute("listSize", reports.size());
    return "reports/list";
}
    //日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Long id, Model model) {
     Report report = reportService.findById(id);
     // 従業員名を取得して設定
     String authorName = report.getEmployee().getName();
     report.setAuthorName(authorName);
     model.addAttribute("report", report);
     return "reports/detail";
}

 // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        Report report = new Report();
        report.setEmployee(userDetail.getEmployee()); // ログイン中の従業員をセット
        model.addAttribute("report", report);
        model.addAttribute("authorName", userDetail.getEmployee().getName());
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated @ModelAttribute Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        // 入力チェック
        if (res.hasErrors()) {
            model.addAttribute("authorName", userDetail.getEmployee().getName());
            model.addAttribute("report", report);
            return create(model,userDetail); // エラーがある場合は再度新規登録画面を表示
        }
        // ログイン中の従業員コードを再設定
        report.setEmployee(userDetail.getEmployee());

        // 業務チェック：同じ日付と従業員コードの日報が既に存在するか確認
        ErrorKinds result = reportService.save(report);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(model,userDetail); // エラーがある場合は再度新規登録画面を表示
        }
        // 成功した場合、日報一覧画面にリダイレクト
        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String showUpdateReportForm(@PathVariable Long id, Model model,@AuthenticationPrincipal UserDetail userDetail) {
    Report report = reportService.findById(id);
        model.addAttribute("report", report);
        model.addAttribute("authorName", userDetail.getEmployee().getName());
    return "reports/update";
}

    //日報更新処理
    @PostMapping(value = "/{id}/update")
    public String updateReport(@PathVariable Long id, @Valid @ModelAttribute Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        //入力チェック
        if (res.hasErrors()) {
            model.addAttribute("authorName", userDetail.getEmployee().getName());
            model.addAttribute("report", report); // エラーがある場合、既存のレポートオブジェクトを保持
            return "reports/update";
        }

        // 業務チェック：同じ日付と従業員コードの日報が既に存在するか確認
        Optional<Report> existingReport = reportService.findByReportDateAndEmployeeCode(report.getReportDate(), userDetail.getEmployee().getCode());
        if (existingReport.isPresent() && !existingReport.get().getId().equals(id)) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR), ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
            model.addAttribute("authorName", userDetail.getEmployee().getName());
            model.addAttribute("report", report); // エラーがある場合、既存のレポートオブジェクトを保持
            return "reports/update";
        }

        // ログイン中の従業員コードを設定
        report.setEmployee(userDetail.getEmployee()); // ログイン中の従業員を再度セット

        // 日報を更新
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