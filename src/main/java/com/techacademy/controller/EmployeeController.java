package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 従業員一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", employeeService.findAll().size());
        model.addAttribute("employeeList", employeeService.findAll());

        return "employees/list";
    }

    // 従業員詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable String code, Model model) {

        model.addAttribute("employee", employeeService.findByCode(code));
        return "employees/detail";
    }

    // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Employee employee) {

        return "employees/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Employee employee, BindingResult res, Model model) {
        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         */
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));
            return create(employee);
        }
        // 入力チェック
        if (res.hasErrors()) {
            return create(employee);
        }
        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee);
            }
        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee);
        }
        return "redirect:/employees";
    }
    //従業員更新画面表示
    @GetMapping(value = "/{code}/update")
    public String edit(@ModelAttribute Employee employee, @PathVariable String code, Model model ) {
        //Modelに登録
        model.addAttribute("employee", employeeService.findByCode(code));
        //Employee更新画面に遷移
        return "employees/update";
    }

    //従業員更新処理
    @PostMapping(value = "/{code}/update")
    public String update(@Validated Employee employee,String code, BindingResult res, Model model) {

     // 名前空白チェック
        if ("".equals(employee.getName())) {
            res.rejectValue("name", "error.employee", "名前は必須です");
        }
        // 名前文字制限チェック
        if (employee.getName().length() > 20) {
            res.rejectValue("name", "error.employee", "名前は20文字以下で入力してください");
        }
        // エラーがある場合、再度更新画面を表示
        if (res.hasErrors()) {
            model.addAttribute("employee", employee);
            return "employees/update";
        }

        //Serviceの呼び出し
            ErrorKinds result = employeeService.update(employee);
        //保存処理の結果がErrorKindsに含まれているエラーであるかをチェック
            if (ErrorMessage.contains(result)) {
                //エラーメッセージをモデルに追加
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return edit(employee,code,model);
            }
        //一覧画面にリダイレクト
        return "redirect:/employees";
    }



    // 従業員削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = employeeService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employee", employeeService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/employees";
    }

}
