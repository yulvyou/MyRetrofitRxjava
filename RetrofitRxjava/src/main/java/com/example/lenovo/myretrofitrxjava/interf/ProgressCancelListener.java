package com.example.lenovo.myretrofitrxjava.interf;

/**
 * 取消进度提示框接口,主要是用来给Subscriber解绑
 */

public interface ProgressCancelListener {
    void onCancelProgress();
}
