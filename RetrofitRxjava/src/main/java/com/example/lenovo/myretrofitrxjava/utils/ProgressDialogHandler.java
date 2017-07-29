package com.example.lenovo.myretrofitrxjava.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.example.lenovo.myretrofitrxjava.interf.ProgressCancelListener;

/**
 * Handler接收两个消息来控制显示Dialog还是关闭Dialog。
 * 创建Handler的时候我们需要传入ProgressCancelListener的对象实例
 */

public class ProgressDialogHandler extends Handler{

    public static final int SHOW_PROGRESS_DIALOG = 1;//显示Dialog
    public static final int DISMISS_PROGRESS_DIALOG = 2;//关闭Dialog

    private ProgressDialog mProgressDialog;

    private Context mContext;
    private Boolean cancelable;
    private ProgressCancelListener mProgressCancelListener;

    //构造函数
    public ProgressDialogHandler(Context context, ProgressCancelListener mProgressCancelListener,
                                 boolean cancelable){
        super();
        this.mContext = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.cancelable = cancelable;
    }//Constructor


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }//handleMessage


    /**
     * 初始化Handle
     */
    private void initProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setCancelable(cancelable);

            if(cancelable){
               mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                   @Override
                   public void onCancel(DialogInterface dialog) {
                    mProgressCancelListener.onCancelProgress();//将实现ProgressCancelListener的Subscriber解绑
                   }
               });
            }//if

            if(!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }//if

        }//if
    }//initProgressDialog


    /**
     * 关闭Dialog
     */
    private void dismissProgressDialog() {
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }//dismissProgressDialog



}//End
