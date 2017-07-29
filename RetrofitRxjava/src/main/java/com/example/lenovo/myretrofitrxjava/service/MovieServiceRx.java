package com.example.lenovo.myretrofitrxjava.service;

import com.example.lenovo.myretrofitrxjava.entity.MovieEntity;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 结合Rxjava 是用的网络请求服务
 * 返回值是Observer，而不是Call了
 */

public interface MovieServiceRx {
    @GET("top250")
    Observable<MovieEntity> getTopMovie(@Query("start") int start, @Query("count") int count);
}
