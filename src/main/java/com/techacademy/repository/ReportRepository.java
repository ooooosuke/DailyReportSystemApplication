package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReportDateAndEmployeeCode(LocalDate reportDate, String employeeCode);

 // 一般ユーザーのリポートを取得
    List<Report> findByEmployeeCode(String employeeCode);

    // 全リポートを取得
    List<Report> findAll();

 // 指定した従業員に紐づいた日報を取得するメソッドを追加
    List<Report> findByEmployee(Employee employee);

}
