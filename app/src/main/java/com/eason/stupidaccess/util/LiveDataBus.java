package com.eason.stupidaccess.util;

import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据总线
 * 用来替代EventBus
 */
public class LiveDataBus {

    /**
     * 使用 ProtectedUnPeekLiveData 防止数据粘性和数据倒灌
     */
    private final Map<String, ProtectedUnPeekLiveData> liveDataMap;

    private LiveDataBus() {
        liveDataMap = new HashMap<>();
    }

    private static class InstHolder {
        private static final LiveDataBus INSTANCE = new LiveDataBus();
    }

    private static LiveDataBus getInstance() {
        return InstHolder.INSTANCE;
    }

    private <T> void post(String key, Class<T> type, T t) {
        MutableLiveData<T> liveData = with(key, type);
        if (isUiThread()) {
            liveData.setValue(t);
        } else {
            liveData.postValue(t);
        }
    }

    public <T> void observe(String key, Class<T> type, LifecycleOwner owner, Observer<T> observer) {
        if (observer == null) {
            return;
        }
        MutableLiveData<T> liveData = with(key, type);
        if (owner != null) {
            liveData.observe(owner, observer);
        } else {
            liveData.observeForever(observer);
        }
    }

    private <T> void observeStickyInner(String key, Class<T> type, LifecycleOwner owner, Observer<T> observer) {
        if (observer == null) {
            return;
        }
        ProtectedUnPeekLiveData<T> liveData = (ProtectedUnPeekLiveData<T>) with(key, type);
        if (owner != null) {
            liveData.observeSticky(owner, observer);
        } else {
            liveData.observeStickyForever(observer);
        }
    }

    private <T> MutableLiveData<T> with(String key, Class<T> type) {
        if (!liveDataMap.containsKey(key)) {
            liveDataMap.put(key, new ProtectedUnPeekLiveData<T>());
        }
        return (MutableLiveData<T>) liveDataMap.get(key);
    }

    public <T> MutableLiveData<T> with(String key) {
        if (!liveDataMap.containsKey(key)) {
            liveDataMap.put(key, new ProtectedUnPeekLiveData<T>());
        }
        return (MutableLiveData<T>) liveDataMap.get(key);
    }


    public boolean isUiThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }


    /*************** 通用快捷调用 *************************/

    public final static String LIFECYCLE = "lifecycle_callback";


    public static void postInt(String key, int value) {
        getInstance().post(key, Integer.class, value);
    }

    public static void postString(String key, String value) {
        getInstance().post(key, String.class, value);
    }

    public static void postBundle(String key, Bundle value) {
        getInstance().post(key, Bundle.class, value);
    }

    public static <T> void postObject(String key, Class<T> type, T value) {
        getInstance().post(key, type, value);
    }

    public static void observeInt(String key, @NonNull LifecycleOwner owner, Observer<Integer> observer) {
        getInstance().observe(key, Integer.class, owner, observer);
    }

    public static void observeString(String key, LifecycleOwner owner, Observer<String> observer) {
        getInstance().observe(key, String.class, owner, observer);
    }

    public static void observeBundle(String key, @NonNull LifecycleOwner owner, Observer<Bundle> observer) {
        getInstance().observe(key, Bundle.class, owner, observer);
    }

    public static <T> void observeObject(String key, @NonNull LifecycleOwner owner, Class<T> type, Observer<T> observer) {
        getInstance().observe(key, type, owner, observer);
    }

    public static <T> void observeSticky(String key, Class type, @NonNull LifecycleOwner owner, Observer<T> observer) {
        getInstance().observeStickyInner(key, type, owner, observer);
    }

    /*************** 业务快捷调用 *************************/

    public static void observeLifecycle(@NonNull LifecycleOwner owner, Observer<Integer> observer) {
        observeInt(LiveDataBus.LIFECYCLE, owner, observer);
    }


}
