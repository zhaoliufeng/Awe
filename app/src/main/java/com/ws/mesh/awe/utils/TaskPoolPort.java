package com.ws.mesh.awe.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TaskPoolPort {
    private final static String TAG = TaskPool.class.getSimpleName();

    // {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "TaskPool Worker #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    // }

    public static final int
            TASK_POOL_MODE_ASYNC_RANDOM = 0,
            TASK_POOL_MODE_ASYNC_SEQUENTIAL = 1,

    TASK_POOL_MODE_DEFAULT = TASK_POOL_MODE_ASYNC_RANDOM,
            MAX_TASK_POOL_MODE = 2;


    public TaskPoolPort() {
    }

    public TaskPoolPort(int mode) {
        poolMode = mode;
    }

    private static final TaskPool defRandPool = new TaskPool(TASK_POOL_MODE_ASYNC_RANDOM);
    private static final TaskPool defSeqPool = new TaskPool(TASK_POOL_MODE_ASYNC_SEQUENTIAL);

    public static TaskPool DefRandTaskPool() {
        return defRandPool;
    }

    public static TaskPool DefSeqTaskPool() {
        return defSeqPool;
    }

    public static TaskPool DefTaskPool() {
        return defRandPool;
    }

    private static class SimpleTimer {

        private ReentrantLock timerLock = new ReentrantLock();
        private Condition timerCond = timerLock.newCondition();
        private HashMap<Runnable, List<Long>> taskTimes = new HashMap<Runnable, List<Long>>();
        private TreeMap<Long, Runnable> timedTasks = new TreeMap<Long, Runnable>();
        private Thread timerThread = new Thread(new Runnable() {
            public void run() {

                long first = 0L;
                Runnable cb = null;
                while ( true ) {
                    try {
                        try {
                            timerLock.lock();

                            if (timedTasks.isEmpty()) {
                                timerCond.await();
                                continue;
                            }

                            long currMS = System.currentTimeMillis() - 5;
                            if ((first = timedTasks.firstKey()) > currMS) {
                                timerCond.await(first - currMS, TimeUnit.MILLISECONDS);
                                continue;
                            }

                            // Re-get, because first key may be updated when wait().
                            // first = timedTasks.firstKey();

                            // Log.d(TAG, "TaskPool::Handler::TimerThread start to process task");

                            try {
                                cb = timedTasks.remove(first);

                                List<Long> ts = taskTimes.get(cb);
                                if (ts != null) {
                                    ts.remove(first);
                                    if (ts.size() == 0)
                                        taskTimes.remove(cb);
                                }
                            } catch (Throwable e) {
                                Log.e(TAG, "TaskPool::Handler::TimerThread process taskTimes map with exception(" + e.getClass() + "): " + e.getLocalizedMessage());
                                e.printStackTrace();
                                // because cb is getted, so just to continue process cb...
                            }

                        } catch (InterruptedException e) {
                            Log.e(TAG, "TaskPool::Handler::TimerThread wait failed with exception(" + e.getClass() + "): " + e.getLocalizedMessage());
                            e.printStackTrace();

                            continue;   // retry.
                        } finally {
                            timerLock.unlock();
                        }

                        if (cb != null)
                            cb.run();

                        cb = null;      // Release cb.
                        // Log.d(TAG, "TaskPool::SimpleTimer run a task succ!");

                    } catch (Throwable e) {
                        Log.e(TAG, "TaskPool::Handler::TimerThread loop callback run to a throwable(" + e.getClass() + "): " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }
        }, "TaskPool::Handler timerThread");

        {

            // timerThread.setPriority(Thread.MAX_PRIORITY - 1);
        }

        public SimpleTimer() {
            timerThread.start();
        }


        public boolean removeCallbacks(Runnable cb) {
            boolean removed = false;
            if (cb != null) {
                try {
                    timerLock.lock();

                    List<Long> ts = taskTimes.get(cb);
                    if (ts != null) {
                        taskTimes.remove(cb);

                        for (long t : ts)
                            timedTasks.remove(t);

                        removed = true;
                        // Log.d(TAG, "TaskPool::SimpleTimer removeCallbacks() succ!");
                    }
                } catch (Throwable e) {
                    Log.w(TAG, "TaskPool::SimpleTimer removeAllCallbacks failed: " + e);
                    e.printStackTrace();
                } finally {
                    timerLock.unlock();
                }
            }

            return removed;
        }

        public boolean postDelayed(final Runnable cb, final long msDelay) {
            if (cb != null && msDelay >= 0) {
                long trigTime = System.currentTimeMillis() + msDelay;

                try {
                    timerLock.lock();

                    // Avoid replace existed runnables...
                    //  FIFO
                    while ( timedTasks.containsKey(trigTime) )
                        trigTime++;

                    long lastFirst = timedTasks.isEmpty() ? (trigTime + 10) : timedTasks.firstKey();
                    timedTasks.put(trigTime, cb);

                    List<Long> ts = taskTimes.get(cb);
                    if (ts == null) {
                        ts = new ArrayList<Long>();
                        taskTimes.put(cb, ts);
                    }

                    ts.add(trigTime);

                    if (trigTime < lastFirst)
                        timerCond.signal();

                    // Log.d(TAG, "TaskPool::SimpleTimer postDelayed() succ!");
                    return true;
                } catch (Throwable e) {
                    Log.w(TAG, "TaskPool::SimpleTimer postDelayed() failed: " + e);
                    e.printStackTrace();
                } finally {
                    timerLock.unlock();
                }
            }

            return false;
        }
    }

    private static class Handler {

        private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();

        private static SimpleTimer timer = new SimpleTimer();

        public void loop() {
            while ( true ) {
                try {
                    // Log.d(TAG, "TaskPool::Handler waiting for tasks!");

                    Runnable t = tasks.take();
                    if (t != null)
                        t.run();

                    // Log.d(TAG, "TaskPool::Handler runs succ!");
                } catch (Throwable e) {
                    Log.i(TAG, "TaskPool::Handler run to a excpt/error: " + e);
                    e.printStackTrace();
                }
            }
        }

        public void removeCallbacks(Runnable cb) {
            while ( tasks.remove(cb) ) ;
            timer.removeCallbacks(cb);
        }

        public boolean postDelayed(Runnable cb, long msDelay) {
            try {
                return timer.postDelayed(cb, msDelay);
            } catch (Throwable e) {
                Log.e(TAG, "TaskPool::Handler put failed with exception(" + e.getClass() + "): " + e.getLocalizedMessage());
            }
            return false;
        }

        public boolean post(Runnable cb) {
            try {
                if (tasks.add(cb)) {
                    // Log.d(TAG, "TaskPool::Handler runs succ!");
                    return true;
                }
            } catch (Throwable e) {
                Log.e(TAG, "TaskPool::Handler put failed with exception(" + e.getClass() + "): " + e.getLocalizedMessage());
            }
            return false;
        }
    }

    private static final Runnable seqLoopRun = new Runnable() {
        public void run() {
            seqLoopHdlr = new Handler();        // Bind handle to current-running-thread's looper.

            synchronized (seqLoopThread) {
                threadInitSucc = true;
                Log.i(TAG, "Thread init complete!");
                seqLoopThread.notifyAll();
            }

            while ( true ) {
                try {
                    seqLoopHdlr.loop();
                } catch (Throwable e) {
                    Log.e(TAG, "SeqLoop execute failed with exception(" + e.getClass() + "): " + e.getLocalizedMessage());
                }
            }
        }
    };

    private static boolean threadInitSucc = false;
    private static Thread seqLoopThread = new Thread(seqLoopRun, "TaskPool's seqLook task.");
    private static Handler seqLoopHdlr = null;
    private static ThreadPoolExecutor randLoop = (ThreadPoolExecutor) THREAD_POOL_EXECUTOR;
    // private static ThreadPoolExecutor randLoop = (ThreadPoolExecutor)(AsyncTask.THREAD_POOL_EXECUTOR);
    private static int randLoopPurgeGap = 0;

    static {
        seqLoopThread.start();
    }

    private static Handler seqLoopHandler() {
        if (seqLoopHdlr == null) {
            synchronized (seqLoopThread) {
                try {
                    while ( !threadInitSucc ) {
                        Log.i(TAG, "Thread init not complete, waiting!");
                        seqLoopThread.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
            ;
        }

        return seqLoopHdlr;
    }

    private void LogNullTask() {
        Log.e(TAG, "Pushed a null task!");
        new Throwable().printStackTrace();
    }

    public boolean PushTask(final Runnable t, long delay) {
        if (t != null)
            switch (poolMode) {
                case TASK_POOL_MODE_ASYNC_RANDOM:
                    return seqLoopHandler().postDelayed(new Runnable() {
                        public void run() {
                            PushTask(t);
                        }
                    }, delay);
                // break;
                case TASK_POOL_MODE_ASYNC_SEQUENTIAL:
                    return seqLoopHandler().postDelayed(t, delay);
            }
        else
            LogNullTask();

        return false;
    }

    public final boolean RandExec(final Runnable r) {
        try {
            randLoop.execute(new Runnable() {
                public void run() {
                    try {
                        r.run();
                        randLoop.remove(this);
                    } catch (Throwable e) {
                        Log.e(TAG, "RandLoop execute failed with exception(" + e.getClass() + "): " + e.getLocalizedMessage());
                    }
                }
            });

            if (randLoopPurgeGap++ % 128 == 0) randLoop.purge();

            return true;
        } catch (Throwable e) {
            Log.e(TAG, "TaskPool rand loop push a task failed(" + e.getClass() + "): " + e.getLocalizedMessage());
            Log.e(TAG, e.getLocalizedMessage());
            ;
            return false;
        }
    }

    public boolean PushTask(final Runnable t) {
        if (t != null)
            switch (poolMode) {
                case TASK_POOL_MODE_ASYNC_RANDOM:
                    return RandExec(t);
                case TASK_POOL_MODE_ASYNC_SEQUENTIAL:
                    return seqLoopHandler().post(t);
            }
        else
            LogNullTask();

        return true;
    }

    // DelayTaskMap, is only used for cancel delayed randLoopTask..
//    private static final Map<Runnable, Runnable>     delayTaskMap = new HashMap<Runnable, Runnable>();
    public boolean CancelTask(final Runnable t) {
        if (t != null)
            switch (poolMode) {
                case TASK_POOL_MODE_ASYNC_RANDOM:
                    // Cannot cancel randLoop, but if it is a delay task, we can cancel it.
//                    seqLoopHdlr.removeCallbacks(t);
                    return false;
                case TASK_POOL_MODE_ASYNC_SEQUENTIAL:
                    seqLoopHandler().removeCallbacks(t);
                    break;
            }
        else
            LogNullTask();

        return true;
    }

    private static final Map<Runnable, TimerTask> timerTaskMap = new HashMap<Runnable, TimerTask>();
    private static final Timer scheduler = new Timer(true /* is a daemon thread */);
    private static int purgeGap = 0;

    public boolean PushCycTask(final Runnable t, final long millGap, final long millDelay) {
        if (t != null) {
            CancelCycTask(t);

            TimerTask tt = new TimerTask() {
                public void run() {
                    try {
                        if (!PushTask(t)) {         // Avoid block timer thread...
                            Log.w(TAG, "Timer task excutor is full, task runs in timer thread(may cause timer blocked)...");

                            t.run();
                        }
                    } catch (Throwable e) {
                        Log.e(TAG, "Timer task run with exception" + "(" + e.getClass() + "): " + e.getLocalizedMessage());
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
            };

            try {
                timerTaskMap.put(t, tt);
                try {
                    scheduler.schedule(tt, millDelay, millGap);
                    return true;
                } catch (Throwable e) {
                    timerTaskMap.remove(t);
                }
            } catch (Throwable e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
            return false;
        } else
            LogNullTask();

        return true;
    }

    public boolean CancelCycTask(final Runnable t) {
        TimerTask tt = timerTaskMap.remove(t);
        if (tt != null) {
            boolean canceled = tt.cancel();
            if (purgeGap++ % 128 == 0) scheduler.purge();
            return canceled;
        }
        return true;
    }

    private int poolMode = TASK_POOL_MODE_DEFAULT;

    public int TaskMode() {
        return poolMode;
    }

//  public boolean TaskMode(int mode)
//  {
//      if (poolMode != mode)
//      {
//          poolMode = mode;
//      }
//      return true;
//  }


    
}
