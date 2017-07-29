package com.example.lenovo.myretrofitrxjava.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lenovo.myretrofitrxjava.R;
import com.example.lenovo.myretrofitrxjava.entity.MovieEntity;
import com.example.lenovo.myretrofitrxjava.http.HttpMethods;
import com.example.lenovo.myretrofitrxjava.interf.SubscriberOnNextListener;
import com.example.lenovo.myretrofitrxjava.service.MovieService;
import com.example.lenovo.myretrofitrxjava.subscriber.ProgressSubscriber;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {
    

    Subscriber<MovieEntity> subscriber;
    Subscriber<String> subscriberMap;
    //观察者中的onNext 方法中的接口
    private SubscriberOnNextListener getTopMovieOnNext;

    @Bind(R.id.btn_retr)
    Button mBtnRetr;
    @Bind(R.id.btn_rxjava_retr)
    Button mBtnRxjavaRetr;
    @Bind(R.id.btn_map)
    Button mBtnMap;
    @Bind(R.id.result_TV)
    TextView mResultTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();

    }//onCreate

    /**
     * 初始化
     */
    private void init() {
        //初始化获取电影观察者的onNext接口
        getTopMovieOnNext = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                mResultTV.setText(result);
            }
        };

    }//init

    @OnClick({R.id.btn_retr, R.id.btn_rxjava_retr, R.id.btn_map})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retr:
                getMovie();
                break;
            case R.id.btn_rxjava_retr:
                getMovieWithRx();
                break;
            case R.id.btn_map:
                getMovieWithMap();
                break;
        }
    }//Onclick

    /**
     * 仅仅使用Retrofit进行网络请求
     */
    public void getMovie() {
        String baseUrl = "https://api.douban.com/v2/movie/";

        //创建一个Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //实例网络请求服务
        MovieService movieService = retrofit.create(MovieService.class);

        //调用请求方法，并得到call
        Call<MovieEntity> call = movieService.getTopMovie(0, 10);//MovieEntity为网络请求返回的数据

        //发起网络请求
        call.enqueue(new Callback<MovieEntity>() {
            @Override
            public void onResponse(Call<MovieEntity> call, Response<MovieEntity> response) {
                //TODO
                Log.i("TAG", "response:" + response.body().toString());
                mResultTV.setText(response.body().getTotal() + "");
            }

            @Override
            public void onFailure(Call<MovieEntity> call, Throwable t) {
                mResultTV.setText(t.getMessage());
            }
        });

    }//getMovieWithRx

    /**
     * 使用Retrofit+Rxjava进行网络请求
     * PS:使用了单例的封装类
     */
    public void getMovieWithRx() {

        subscriber = new Subscriber<MovieEntity>() {
            @Override
            public void onCompleted() {
                Log.i("TAG", "处理数据完毕");
            }

            @Override
            public void onError(Throwable e) {
                mResultTV.setText(e.toString());
            }

            @Override
            public void onNext(MovieEntity movieEntity) {
                Log.i("TAG", "结果为::" + movieEntity.toString());
                mResultTV.setText("结果为:："+movieEntity.toString());
            }
        };

        HttpMethods.getInstance().getTopMovie(subscriber, 0,10);

    }//getMovieWithRx


    /**
     * 使用Retrofit+Rxjava进行网络请求
     * PS:使用了单例的封装类
     * 对返回的数据进行了处理
     */
    public void getMovieWithMap() {

//        subscriberMap = new Subscriber<String>() {
//            @Override
//            public void onCompleted() {
//                Log.i("TAG", "处理数据完毕");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                mResultTV.setText(e.toString());
//            }
//
//            @Override
//            public void onNext(String count) {
//                Log.i("TAG", "结果为:" + count);
//                mResultTV.setText("结过为：" + count);
//            }
//        };

        subscriberMap = new ProgressSubscriber<String>(getTopMovieOnNext,MainActivity.this);

        HttpMethods.getInstance().getTopMovieWithMap(subscriberMap, 0, 10);

    }//getMovieWithRx


//    /**
//     * 使用Retrofit+Rxjava进行网络请求
//     * PS:未使用单例的封装类
//     */
//    public void getMovieWithRx() {
//        String baseUrl = "https://api.douban.com/v2/movie/";
//
//        //创建一个Retrofit 实例
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加Call适配器工厂以支持除Call之外的服务方法返回类型。
//                .build();
//
//        //实例网络请求服务
//        MovieServiceRx movieServiceRx = retrofit.create(MovieServiceRx.class);
//
//        //调用请求方法，并得到call
//        Observable<MovieEntity> observable = movieServiceRx.getTopMovie(0, 10);//MovieEntity为网络请求返回的数据
//
//        //发起网络请求，和更新UI
//        observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<MovieEntity>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.i("TAG", "处理数据完毕");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mResultTV.setText(e.toString());
//                    }
//
//                    @Override
//                    public void onNext(MovieEntity movieEntity) {
//                        Log.i("TAG", "response:" + movieEntity.getCount());
//                        mResultTV.setText(movieEntity.getCount() + "");
//                    }
//                });
//    }//getMovieWithRx

}//End
