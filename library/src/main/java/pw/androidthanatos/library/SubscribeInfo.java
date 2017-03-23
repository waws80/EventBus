package pw.androidthanatos.library;

import java.lang.reflect.Method;

/**
 * Created on 2017/3/23.
 * 作者：by thanatos
 * 作用：订阅者信息实体
 */

 class SubscribeInfo {

    private Method method;

    private ThreadMode targetThread;

    private Class<?> subscribeType;

     SubscribeInfo(Method method, ThreadMode targetThread, Class<?> subscribeType) {
        this.method = method;
        this.targetThread = targetThread;
        this.subscribeType = subscribeType;
    }

     Method getMethod() {return method;}


     ThreadMode getThreadMode() {return targetThread;}


     Class<?> getSubscribeType() {return subscribeType;}


    @Override
    public String toString() {
        return "SubscribeInfo{" +
                "method=" + method +
                ", targetClass=" + targetThread +
                ", subscribeType=" + subscribeType +
                '}';
    }
}
