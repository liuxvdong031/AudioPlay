package com.xvdong.audioplayer.bus;

import io.reactivex.observers.DisposableObserver;

/**
 * 为RxBus使用的Subscriber, 主要提供next事件的try,catch
 */
public abstract class RxBusSubscriber<T> extends DisposableObserver<T> {

    @Override
    public void onNext(T t) {
        try {
            onEvent(t);
        } catch (Exception e) {
           //可以自己打印的KLog
        }
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
       //可以自己打印的KLog
    }

    protected abstract void onEvent(T t);
}
