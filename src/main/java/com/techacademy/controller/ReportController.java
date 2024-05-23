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
     model.addAttribute("report", reportService.findById(id));
     return "reports/detail";
}

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        model.addAttribute("report", new Report()); // 新しいReportオブジェクトをモデルに追加
        model.addAttribute("authorName", userDetail.getEmployee().getName()); // ログイン中の従業員の名前をモデルに追加
        return "reports/new"; // 新規登録画面のテンプレートを返す
    }
 // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated @ModelAttribute Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        System.out.println("step1");
        // 入力チェック
        if (res.hasErrors()) {
            System.out.println("step2");
            System.out.println(res.toString());
            model.addAttribute("authorName", userDetail.getEmployee().getName());
            model.addAttribute("report", report); // エラー時にreportオブジェクトをモデルに追加
            return create(model, userDetail);
        }



     // 業務チェック：同じ日付と従業員コードの日報が既に存在するか確認
        ErrorKinds result = reportService.save(report);
        if (result == ErrorKinds.DATECHECK_ERROR) {
            System.out.println("step3");
            model.addAttribute("reportDateError", ErrorMessage.getErrorValue(result));
            model.addAttribute("authorName", userDetail.getEmployee().getName());
            model.addAttribute("report", report); // エラー時にreportオブジェクトを再度モデルに追加
            return create(model, userDetail); // 新規登録画面を再表示
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
        report.setEmployeeCode(userDetail.getEmployee().getCode());

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