package com.techacademy.controller;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;
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

    @Autowired
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        List<Report> reports;
        if (userDetail.getEmployee().getRole() == Employee.Role.ADMIN) {
            reports = reportService.findAll();
        } else {
            reports = reportService.findByEmployee(userDetail.getEmployee());
        }
        model.addAttribute("reportList", reports);
        model.addAttribute("listSize", reports.size());
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Long id, Model model) {
        Report report = reportService.findById(id);
        String authorName = report.getEmployee().getName();
        report.setAuthorName(authorName);
        model.addAttribute("report", report);
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        Report report = new Report();
        report.setEmployee(userDetail.getEmployee());
        model.addAttribute("report", report);
        model.addAttribute("authorName", userDetail.getEmployee().getName());
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated @ModelAttribute Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        if (res.hasErrors()) {
            model.addAttribute("authorName", userDetail.getEmployee().getName());
            model.addAttribute("report", report);
            return "reports/new";
        }

        report.setEmployee(userDetail.getEmployee());
        ErrorKinds result = reportService.save(report);
        if (result == ErrorKinds.DATECHECK_ERROR) {
            model.addAttribute("reportDateError", ErrorMessage.getErrorValue(result));
            model.addAttribute("authorName", userDetail.getEmployee().getName());
            model.addAttribute("report", report);
            return "reports/new";
        }

        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String showUpdateReportForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        Report report = reportService.findById(id);
        model.addAttribute("report", report);
        model.addAttribute("authorName", userDetail.getEmployee().getName());
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    public String updateReport(@PathVariable Long id, @Valid @ModelAttribute Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        if (res.hasErrors()) {
            model.addAttribute("authorName", userDetail.getEmployee().getName());
            model.addAttribute("report", report);
            return "reports/update";
        }

        Optional<Report> existingReport = reportService.findByReportDateAndEmployeeCode(report.getReportDate(), userDetail.getEmployee().getCode());
        if (existingReport.isPresent() && !existingReport.get().getId().equals(id)) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR), ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
            model.addAttribute("authorName", userDetail.getEmployee().getName());
            model.addAttribute("report", report);
            return "reports/update";
        }

        report.setEmployee(userDetail.getEmployee());
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