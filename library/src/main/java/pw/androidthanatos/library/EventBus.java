package pw.androidthanatos.library;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Created on 2017/3/23.
 * 作者：by thanatos
 * 作用：高仿EventBus事件总线
 */

public class EventBus {

    /**
     * 事件缓存
     */
    private Map<Object , List<SubscribeMethod>> mCacheMap;

    /**
     * 将事件发送到主线程
     */
    private Handler mHandler=new Handler();

    /**
     * 线程池
     */

    private ExecutorService mThreadPool;



    private EventBus(){
        mCacheMap=new HashMap<>();
        mThreadPool=ThreadPool.init();
    }

    /**
     * 使用静态内部类获取EventBus实例
     * @return 返回EventBus实例
     */
    public static EventBus getDefault(){
        return EventBusHolder.EVENT_BUS;
    }

    private static class EventBusHolder{
        private static final EventBus EVENT_BUS=new EventBus();
    }

    /**
     * 在目标类当中注册EventBus
     * @param target 目标类
     */
    public void register(Object target){
        List<SubscribeMethod> list= mCacheMap.get(target);
        if (list==null){
            List<SubscribeMethod> subscribeMethods=findSubscribeMethodList(target);
            mCacheMap.put(target,subscribeMethods);
        }


    }

    /**
     * 在目标类当中查找订阅者，并将信息保存到list中
     * @param target 目标类
     * @return 集合
     */
    private List<SubscribeMethod> findSubscribeMethodList(Object target)  {
        /**
         * 为了线程安全  使用 CopyOnWriteArrayList
         */
        List<SubscribeMethod> list=new CopyOnWriteArrayList<>();

        Class<?> clazz= target.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        while (clazz!=null){
            /**
             * 判断当前类是否为系统类
             */
            String name = clazz.getName();
            if (name.startsWith("java.")|| name.startsWith("javax.")|| name.startsWith("android.")){
                break;
            }
            /**
             * 获取当前类的所有方法
             */


            for (Method method:methods) {
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                /**
                 * 遍历当前方法是否有 Subscribe 注解 ，如果没有则跳出本次循环
                 */
                if (annotation==null){
                    continue;
                }
                /**
                 * 获取方法的参数长度
                 */
                int parameterlength = method.getParameterTypes().length;
                /**
                 * 当参数只有一个的时候才是合法的订阅者
                 */
                if (parameterlength==1){
                    /**
                     * 如果是合法的参数则拿到其参数类型
                     */
                    Class<?> targetType = method.getParameterTypes()[0];
                    /**
                     * 将订阅者的信息保存到 SubscribeMethod 对象中
                     */
                    ThreadMode targetThread=annotation.value();
                    SubscribeMethod subscribeMethod=new SubscribeMethod(method,targetThread,targetType);
                    list.add(subscribeMethod);
                }
                /**
                 * 当参数个数大于一个的时候抛出异常
                 */
                else {
                    throw new  RuntimeException(" this method can only one parameter but it has  "+ parameterlength+"" +
                            " parameter ");
                }
            }

            /**
             * 当前类的父类
             */
            clazz = clazz.getSuperclass();
        }
        Log.w("thanatos", "findSubscribeMethodList: "+list.size() );

        return list;
    }

    /**
     * 在目标类销毁的时候注销EventBus
     * @param target  目标类
     */
    public void unRegister(Object target){
        List<SubscribeMethod> list= mCacheMap.get(target);
        /**
         * 如果目标类有订阅者，则清除掉所有的订阅者
         */
        if (list!=null){
            for (int i = 0; i < list.size(); i++) {
                list.remove(i);
            }
        }else {
            Log.w("thanatos", "unRegister: "+target+" was not registered" );
        }
        /**
         * 将目标类从事件缓存中清除掉
         */
        mCacheMap.remove(target);



    }

    /**
     * 向目标类发送事件
     * @param targetType 目标事件类型
     */
    public void post(final Object targetType){

        for (final Object target : mCacheMap.keySet()) {
            /**
             * 缓存里面的目标类的订阅者集合
             */
            List<SubscribeMethod> list = mCacheMap.get(target);

            for (final SubscribeMethod subscribeMethod : list) {
                /**
                 * 如果目标类和缓存中的订阅者类相同，即表示找到了订阅者
                 */
                if (subscribeMethod.getSubscribeType().isAssignableFrom(targetType.getClass())) {

                    postToSubscribe(subscribeMethod,target,targetType);

                }

            }

        }

    }

    /**
     * 将事件发送给订阅者
     * @param subscribeMethod  订阅者信息
     * @param target  订阅者的目标类
     * @param targetType  订阅者的事件类型
     */
    private void postToSubscribe(final SubscribeMethod subscribeMethod, final Object target, final Object targetType) {

        switch (subscribeMethod.getThreadMode()) {
            case POST_THREAD:
                /**
                 * 订阅者和发送事件的线程同步
                 */
                invoke(subscribeMethod, target, targetType);
                break;
            case MAIN:
                /**
                 * 当前订阅者的线程为主线程，直接发送事件
                 */
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    invoke(subscribeMethod, target, targetType);
                }
                /**
                 * 发送事件的线程不是主线程，切换线程
                 */
                else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            invoke(subscribeMethod, target, targetType);
                        }
                    });
                }

                break;
            case BACKGROUND_THREAD:
                /**
                 * 订阅者的线程不是主线程
                 */
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    invoke(subscribeMethod, target, targetType);
                }
                /**
                 * 订阅者的线程为主线程，切换线程
                 */

                else {
                    mThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            invoke(subscribeMethod, target, targetType);
                        }
                    });
                }
                break;
        }

    }

    /**
     *
     * @param subscribeMethod 订阅事件的方法
     * @param target   接收事件的目标类
     * @param targetType 事件类型
     */
    @SuppressWarnings("ALL")
    private void invoke(SubscribeMethod subscribeMethod, Object target, Object targetType) {
        try {
            Method method = subscribeMethod.getMethod();
            method.setAccessible(true);
            method.invoke(target,targetType);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
