package pw.androidthanatos.library;

/**
 * Created on 2017/3/23.
 * 作者：by thanatos
 * 作用：指定订阅者接收事件的线程
 */

public enum ThreadMode {

    /**
     * 订阅者与发布事件的线程同步
     */

    POST_THREAD,

    /**
     * 订阅者所在的线程为主线程
     */

    MAIN,

    /**
     * 订阅者的线程在子线程
     */

    BACKGROUND_THREAD
}
