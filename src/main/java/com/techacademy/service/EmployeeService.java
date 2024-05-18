package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 従業員保存
    @Transactional
    public ErrorKinds save(Employee employee) {

        // パスワードチェック
        ErrorKinds result = employeePasswordCheck(employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        // 従業員番号重複チェック
        if (findByCode(employee.getCode()) != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        employee.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

 // 従業員更新
    @Transactional // トランザクション管理のアノテーション。メソッドがトランザクション内で実行されることを示します。
    public ErrorKinds updateEmployee(String code, Employee updatedEmployee, Employee employee) {
        Employee existingEmployee = findByCode(code); // 指定された従業員コードで既存の従業員データを取得
        if (existingEmployee == null) { // 該当する従業員が見つからない場合の処理
            return ErrorKinds.DUPLICATE_ERROR; // 重複エラーを返す（エラー種別が適切でない場合は修正が必要）
        }

        if (updatedEmployee.getPassword() != null && !updatedEmployee.getPassword().isEmpty()) { // パスワードが空でない場合のチェック
            // パスワードチェック
            ErrorKinds result = employeePasswordCheck(updatedEmployee); // パスワードのバリデーションを行う
            if (ErrorKinds.CHECK_OK != result) { // バリデーションに失敗した場合の処理
                return result; // エラー種別を返す
            }
            existingEmployee.setPassword(passwordEncoder.encode(updatedEmployee.getPassword())); // エンコードしたパスワードを設定
        }

        existingEmployee.setName(employee.getName()); // 従業員の名前を更新
        existingEmployee.setRole(updatedEmployee.getRole()); // 従業員の役割を更新
        existingEmployee.setDeleteFlg(false); // 削除フラグをfalseに設定

        LocalDateTime now = LocalDateTime.now(); // 現在の日時を取得
        existingEmployee.setCreatedAt(existingEmployee.getCreatedAt()); // 元の作成日時を保持
        existingEmployee.setUpdatedAt(now); // 更新日時を現在の日時に設定

        employeeRepository.save(existingEmployee); // 既存の従業員データを更新して保存
        return ErrorKinds.SUCCESS; // 更新成功を示す種別を返す
    }

    // 従業員削除
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {

        // 自分を削除しようとした場合はエラーメッセージを表示
        if (code.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }
        Employee employee = findByCode(code);
        LocalDateTime now = LocalDateTime.now();
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 従業員一覧表示処理
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // 1件を検索
    public Employee findByCode(String code) {
        // findByIdで検索
        Optional<Employee> option = employeeRepository.findById(code);
        // 取得できなかった場合はnullを返す
        Employee employee = option.orElse(null);
        return employee;
    }

    // 従業員パスワードチェック
    private ErrorKinds employeePasswordCheck(Employee employee) {

        // 従業員パスワードの半角英数字チェック処理
        if (isHalfSizeCheckError(employee)) {

            return ErrorKinds.HALFSIZE_ERROR;
        }

        // 従業員パスワードの8文字～16文字チェック処理
        if (isOutOfRangePassword(employee)) {

            return ErrorKinds.RANGECHECK_ERROR;
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        return ErrorKinds.CHECK_OK;
    }

    // 従業員パスワードの半角英数字チェック処理
    private boolean isHalfSizeCheckError(Employee employee) {

        // 半角英数字チェック
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(employee.getPassword());
        return !matcher.matches();
    }

    // 従業員パスワードの8文字～16文字チェック処理
    public boolean isOutOfRangePassword(Employee employee) {

        // 桁数チェック
        int passwordLength = employee.getPassword().length();
        return passwordLength < 8 || 16 < passwordLength;
    }

}
