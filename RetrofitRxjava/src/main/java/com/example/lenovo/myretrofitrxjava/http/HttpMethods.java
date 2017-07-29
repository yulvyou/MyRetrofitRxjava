package com.example.lenovo.myretrofitrxjava.http;

import com.example.lenovo.myretrofitrxjava.entity.ApiException;
import com.example.lenovo.myretrofitrxjava.entity.HttpResult;
import com.example.lenovo.myretrofitrxjava.entity.MovieEntity;
import com.example.lenovo.myretrofitrxjava.service.MovieServiceRx;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 网络请求封装类
 */

public class HttpMethods {

    public static final String BASE_URL = "https://api.douban.com/v2/movie/";
    //超时时间
    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;
    private MovieServiceRx movieServiceRx;

    /**
     * 构造函数
     * private修饰
     * 它的构造方法是private修饰的，外部类没有办法通过new来创建它的实例，
     * 只能通过调用它的静态方法getIntance来获得实例，并且在多处调用都返回一个实例
     * ，再也不创建多余的实例
     * 如：
     */
    private HttpMethods(){
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        //创建Retrofit 实例
        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())//设置用于网络请求的HTTP Client。
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        //类似与将retrofit和MovieServiceRx绑定起来
        movieServiceRx = retrofit.create(MovieServiceRx.class);
    }//HttpMethods()

    //获取单例
    public static HttpMethods getInstance(){
        return SingletonHolder.INSTANCE;
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final HttpMethods INSTANCE = new HttpMethods();
    }


    /**
     * 用于获取豆瓣电影Top250的数据
     * @param subscriber 由调用者传过来的观察者对象
     * @param start 起始位置
     * @param count 获取长度
     * PS：未对返回的结果进行处理
     */
    public void getTopMovie(Subscriber<MovieEntity> subscriber, int start, int count){
        movieServiceRx.getTopMovie(start, count)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }//getTopMovie

    /**
     * 用于获取豆瓣电影Top250的数据
     * @param subscriber 由调用者传过来的观察者对象
     * @param start 起始位置
     * @param count 获取长度
     * PS：对返回的结果进行处理
     */
    public void getTopMovieWithMap(Subscriber<String> subscriber, int start, int count){
        movieServiceRx.getTopMovie(start, count)
                .map(new Func1<MovieEntity, String>() {
                    @Override
                    public String call(MovieEntity movieEntity) {
                        return "经过转换后的结果为"+movieEntity.toString();
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }//getTopMovie


    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     *  Func1<HttpResult<T>,T> 表示将HttpResult<T>类型的数据转换为T类型的数据即Subscriber真正需要的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>,T>{

        @Override
        public T call(HttpResult<T> tHttpResult) {
            if(tHttpResult.getCount()==0){
                throw new ApiException(100);
            }
            return tHttpResult.getSubjects();
        }
    }//HttpResultFunc
}//End
