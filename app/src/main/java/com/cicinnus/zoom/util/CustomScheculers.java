package com.cicinnus.zoom.util;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/3
 * </pre>
 */
public class CustomScheculers {

    /**
     * 普通的转换
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> iO2Main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .flatMap(new Function<T, ObservableSource<T>>() {
                            @Override
                            public ObservableSource<T> apply(T t) throws Exception {
                                if (t instanceof Exception) {
                                    return Observable.error(new Exception(((Exception) t).getMessage()));
                                }
                                return Observable.just(t);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
