package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {

    // ID (主キー、自動生成)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 日付 (必須)
    @Column(name = "report_date", nullable = false)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    // タイトル (必須、最大長100)
    @Column(length = 100, nullable = false)
    @NotEmpty
    @Length(max = 100)
    private String title;

    // 内容 (必須、LONGTEXT)
    @Column(length=600, nullable = false)
    @NotEmpty
    @Length(max = 600)
    private String content;

    // 削除フラグ (論理削除)
    @Column(name = "delete_flg", columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlg = false;

    // 登録日時 (必須)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 更新日時 (必須)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

 // 従業員 (必須、外部キー)
    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee; // 従業員エンティティとのリレーションを定義


    // 従業員名 (ビュー用、エンティティに格納しない)
    @Transient
    private String authorName;

}