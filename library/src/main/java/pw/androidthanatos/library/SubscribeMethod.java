package pw.androidthanatos.library;

import java.lang.reflect.Method;

/**
 * Created on 2017/3/23.
 * 作者：by thanatos
 * 作用：订阅者信息实体
 */

 class SubscribeMethod {

    private Method method;

    private ThreadMode targetThread;

    private Class<?> subscribeType;

    public SubscribeMethod(Method method, ThreadMode targetThread, Class<?> subscribeType) {
        this.method = method;
        this.targetThread = targetThread;
        this.subscribeType = subscribeType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ThreadMode getThreadMode() {
        return targetThread;
    }

    public void setThreadMode(ThreadMode targetClass) {
        this.targetThread = targetClass;
    }

    public Class<?> getSubscribeType() {
        return subscribeType;
    }

    public void setSubscribeType(Class<?> subscribeType) {
        this.subscribeType = subscribeType;
    }

    @Override
    public String toString() {
        return "SubscribeMethod{" +
                "method=" + method +
                ", targetClass=" + targetThread +
                ", subscribeType=" + subscribeType +
                '}';
    }
}
