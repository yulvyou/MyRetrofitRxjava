package com.example.lenovo.myretrofitrxjava.subscriber;

import android.content.Context;
import android.widget.Toast;

import com.example.lenovo.myretrofitrxjava.interf.ProgressCancelListener;
import com.example.lenovo.myretrofitrxjava.interf.SubscriberOnNextListener;
import com.example.lenovo.myretrofitrxjava.utils.ProgressDialogHandler;

import rx.Subscriber;

/**
 * Progress 观察者
 */

public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener{
    //处理onNext的接口
    private SubscriberOnNextListener mSubscriberOnNextListener;
    //处理Dialog的Handle
    private ProgressDialogHandler mProgressDialogHandler;
    private Context mContext;

    public ProgressSubscriber(SubscriberOnNextListener subscriberOnNextListener, Context context){
        this.mSubscriberOnNextListener = subscriberOnNextListener;
        this.mContext = context;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    //此方法中启动ProgressDialog
    @Override
    public void onStart() {
        //显示Dialog
        showProgressDialog();
    }

    //处理数据相关的逻辑
    @Override
    public void onNext(T t) {
        mSubscriberOnNextListener.onNext(t);
    }//onNext

    @Override
    public void onCompleted() {
        //关闭Dialog
        dismissProgressDialog();
        Toast.makeText(mContext, "获取Top Movie 数据完成", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable e) {
        //关闭Dialog
        dismissProgressDialog();
        Toast.makeText(mContext, "获取Top Movie 数据发生错误："+e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示ProgressDialog
     */
    private void showProgressDialog(){
        if(mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }//showProgressDialog


    /**
     * 关闭ProgressDialog
     */
    private void dismissProgressDialog(){
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }//dismissProgressDialog

    /**
     * 实现ProgressCancelListener接口必须完成的方法
     */
    @Override
    public void onCancelProgress() {
        if(!this.isUnsubscribed()){//如果本Subscriber未解绑
            this.unsubscribe();//解绑
        }
    }

}//End
