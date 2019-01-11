package com.example.threadpooldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("fff", "-------MainActivity--======" + Thread.currentThread().getName());
//        newCachedThreadPool();
//        newFixedThreadPool();
//        newScheduledThreadPool();
//        newSingleThreadExecutor();
//        newMyThreadExecutor();
        newMyThreadExecutor2();
    }

    /**
     *  ThreadPoolExecutor的构造方法参数的解释，我们的线程提交到线程池之后又是按照什么样的规则去运行呢？OK，它们遵循如下规则：
     *  1.execute一个线程之后，如果线程池中的线程数未达到核心线程数，则会立马启用一个核心线程去执行
     *  2.execute一个线程之后，如果线程池中的线程数已经达到核心线程数，且workQueue未满，则将新线程放入workQueue中等待执行
     *  3.execute一个线程之后，如果线程池中的线程数已经达到核心线程数但未超过非核心线程数，且workQueue已满，则开启一个非核心线程来执行任务
     *  4.execute一个线程之后，如果线程池中的线程数已经超过非核心线程数，则拒绝执行该任务
     */
    /**
     * newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。线程池的规模不存在限制。
     * newFixedThreadPool 创建一个固定长度线程池，可控制线程最大并发数，超出的线程会在队列中等待。
     * newScheduledThreadPool 创建一个固定长度线程池，支持定时及周期性任务执行。
     * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
     */
    private void newCachedThreadPool() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(index * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("fff", "-------newCachedThreadPool--======" + index);
                }
            });
        }
    }

    private void newFixedThreadPool() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("fff", "-------newFixedThreadPool--======" + index);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void newScheduledThreadPool() {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        scheduledThreadPool.schedule(new Runnable() {
            @Override
            public void run() {
                Log.e("fff", "-------newScheduledThreadPool--===delay 3 seconds===");
            }
        }, 3, TimeUnit.SECONDS);
    }

    private void newSingleThreadExecutor() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("fff", "-------newSingleThreadExecutor--======" + index);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 自定义线程池执任务
     */
    private void newMyThreadExecutor() {
        MyThreadPool threadPool = new MyThreadPool(3, 5, 30,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128));
        for (int i = 0; i < 30; i++) {
            final int index = i;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("fff", "-------newMyThreadExecutor--======" + index);
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 自定义线程池执任务并获取任务执行结果
     */
    private void newMyThreadExecutor2() {
        MyThreadPool threadPool = new MyThreadPool(3, 5, 30,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128));
        /**
         * Future.get()会造成线程阻塞，等待返回结果；
         *  FutureTask done()方法会在任务执行结束后回调
         */
        CallableDemo task = new CallableDemo();

//        Future<String> result = threadPool.submit(task);
//        try {
//            Log.e("fff", "-------子线程执行结果--======" + result.get());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        /**
         * 执行计算任务，回调结果
         */
        FutureTask<String> futureTask = new FutureTask<String>(task) {
            @Override
            protected void done() {
                try {
                    Log.e("fff", "-------子线程执行结果--======" + get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadPool.submit(futureTask);

//        /**
//         * 任务成功完成之后，返回指定的结果
//         */
//        Runnable testRunnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.e("fff", "-------testRunnable--======");
//            }
//        };
//        Future<String> result = threadPool.submit(testRunnable, "aaaaaaaaaa");
//        try {
//            Log.e("fff", "-------子线程执行结果--======" + result.get());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    class CallableDemo implements Callable<String> {
        @Override
        public String call() throws Exception {
            Log.e("fff", "-------子线程开始执行--======" + Thread.currentThread().getName());
            String result = "";
            try {
                Thread.sleep(5000);
                result = "计算结果是666";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("fff", "-------子线程执行结束--======");
            return result;
        }
    }

    /**
     * 自定义线程池实现
     */
    class MyThreadPool extends ThreadPoolExecutor {

        public MyThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                            BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
//            Log.e("fff", "-------beforeExecute--======");
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
//            Log.e("fff", "-------afterExecute--======");
        }

        @Override
        protected void terminated() {
            super.terminated();
//            Log.e("fff", "-------terminated--======");
        }
    }
}
