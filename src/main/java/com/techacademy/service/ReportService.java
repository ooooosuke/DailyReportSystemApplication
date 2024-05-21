package com.techacademy.service;

import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    public void save(Report report) {
        // 現在時刻を取得
        LocalDateTime now = LocalDateTime.now();
        // 登録日時が設定されていない場合にのみ設定
        if (report.getCreatedAt() == null) {
            report.setCreatedAt(now);
        }
        // 更新日時を現在時刻に設定
        report.setUpdatedAt(now);
        // 削除フラグを false に設定
        report.setDeleteFlg(false);

        // 日報をリポジトリに保存
        reportRepository.save(report);
    }

    // 日報更新
    @Transactional
    public void updateReport(Long id, Report updatedReport) {
        // IDに対応する既存の日報を検索
        Report existingReport = findById(id);
        if (existingReport == null) {
            // 日報が見つからない場合は例外をスロー
            throw new IllegalArgumentException("Report not found");
        }

        // 更新内容を反映
        existingReport.setReportDate(updatedReport.getReportDate());
        existingReport.setTitle(updatedReport.getTitle());
        existingReport.setContent(updatedReport.getContent());
        existingReport.setEmployeeCode(updatedReport.getEmployeeCode());
        existingReport.setDeleteFlg(false);

        // 現在時刻を取得
        LocalDateTime now = LocalDateTime.now();
        // 更新日時を現在時刻に設定
        existingReport.setUpdatedAt(now);

        // 日報を保存
        reportRepository.save(existingReport);
    }

    // 日報削除
    @Transactional
    public void delete(Long id) {
        // IDに対応する日報を検索
        Report report = findById(id);
        if (report != null) {
            // 現在時刻を取得
            LocalDateTime now = LocalDateTime.now();
            // 更新日時を現在時刻に設定
            report.setUpdatedAt(now);
            // 削除フラグを true に設定
            report.setDeleteFlg(true);

            // 日報を保存
            reportRepository.save(report);
        }
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        // 全ての日報を取得
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findById(Long id) {
        // IDに対応する日報を検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合は null を返す
        return option.orElse(null);
    }

 // 指定した日付と従業員コードで日報を検索
    public Optional<Report> findByReportDateAndEmployeeCode(LocalDate reportDate, String employeeCode) {
        return reportRepository.findByReportDateAndEmployeeCode(reportDate, employeeCode);
    }
}