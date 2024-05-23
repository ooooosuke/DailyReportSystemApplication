
package com.techacademy.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import jakarta.persistence.OneToMany;

@Data
@Entity
@Table(name = "employees")
@SQLRestriction("delete_flg = false")
public class Employee {

    public enum Role {
        GENERAL("一般"),
        ADMIN("管理者");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // ID
    @Id
    @Column(length = 10)
    @NotEmpty
    @Length(max = 10)
    private String code;

    // 名前
    @Column(length = 20, nullable = false)
    @NotEmpty
    @Length(max = 20)
    private String name;

    // 権限
    @Column(columnDefinition="VARCHAR(10)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // パスワード
    @Column(length = 255, nullable = false)
    private String password;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;

 // 従業員と日報のリレーション
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Report> reportList; // Reportエンティティとの1対多のリレーションを定義

}