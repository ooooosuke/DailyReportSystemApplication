package com.techacademy.constants;

// エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR,
    // 半角英数字チェックエラー
    HALFSIZE_ERROR,
    // 桁数(8桁~16桁以外)チェックエラー
    RANGECHECK_ERROR,
    // 重複チェックエラー(例外あり)
    DUPLICATE_EXCEPTION_ERROR,
    // 重複チェックエラー(例外なし)
    DUPLICATE_ERROR,
    // ログイン中削除チェックエラー
    LOGINCHECK_ERROR,
    // 日付チェックエラー
    DATECHECK_ERROR,
    // チェックOK
    CHECK_OK,
    // 正常終了
    SUCCESS,
    // 氏名空白チェックエラー
    NAME_BLANK_ERROR,
    // 氏名桁数チェックエラー
    NAME_RANGECHECK_ERROR,
    // パスワード空白チェックエラー
    PASSWORD_BLANK_ERROR,
    // パスワード桁数チェックエラー
    PASSWORD_RANGECHECK_ERROR,
    // パスワード形式チェックエラー
    PASSWORD_FORMAT_ERROR;

}