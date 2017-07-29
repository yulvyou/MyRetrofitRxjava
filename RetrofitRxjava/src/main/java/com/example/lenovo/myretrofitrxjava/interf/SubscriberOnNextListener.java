package com.example.lenovo.myretrofitrxjava.interf;

/**
 * 专门处理Subscriber中的onNext函数
 */

public interface SubscriberOnNextListener<T> {
    void onNext(T t);
}//End
