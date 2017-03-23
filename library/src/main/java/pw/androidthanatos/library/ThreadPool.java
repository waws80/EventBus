package pw.androidthanatos.library;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created on 2017/3/23.
 * 作者：by thanatos
 * 作用： 线程池
 */

 class ThreadPool  {
    /**
     * 线程数为当前手机的cpu数量
     */
    private static  int DEFAULT_THREAD_COUNT=Runtime.getRuntime().availableProcessors();


     static ExecutorService init(){return Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);}
}
