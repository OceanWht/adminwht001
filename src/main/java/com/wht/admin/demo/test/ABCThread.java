package com.wht.admin.demo.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ABCThread {

    private static int cond = 1;
    private static Lock lock = new ReentrantLock();//jdk1.5 同步锁 锁来保证线程的访问的互斥
    private static Condition condition = lock.newCondition();

    public static void main(String[] args) {
        Runnable ra = new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    while (true) {
                        if (cond % 3 == 1) {
                            System.out.print("A");
                            cond++;
                            condition.signalAll();
                            break;
                        } else {
                            try {
                                condition.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        };

        Runnable rb = new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    while (true) {
                        if (cond % 3 == 2) {
                            System.out.print("B");
                            cond++;
                            condition.signalAll();
                            break;
                        } else {
                            try {
                                condition.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        };

        Runnable rc = new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    while (true) {
                        if (cond % 3 == 0) {
                            System.out.print("C");
                            cond++;
                            condition.signalAll();
                            break;
                        } else {
                            try {
                                condition.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        };


       /* ThreadA ta = abcThread.new ThreadA();
        ThreadB tb = abcThread.new ThreadB();
      ThreadC tc = abcThread.new ThreadC();*/
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
            executorService.execute(ra);
            executorService.execute(rb);
            executorService.execute(rc);
        }

        executorService.shutdown();// 关闭线程池


    }

    /*class ThreadA implements Runnable {
        @Override
        public void run() {
            lock.lock();//获得锁
            try {
                while (true) {
                    if (cond % 3 == 1) {
                        System.out.print("A");
                        cond++;
                        condition.signalAll();
                        break;
                    } else {
                        try {
                            condition.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                lock.unlock();
            }

        }
    }

    class ThreadB implements Runnable {
        @Override
        public void run() {
            lock.lock();//获得锁
            try {
                while (true) {
                    if (cond % 3 == 2) {
                        System.out.print("B");
                        cond++;
                        condition.signalAll();
                        break;
                    } else {
                        try {
                            condition.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                lock.unlock();
            }

        }
    }

    class ThreadC implements Runnable {
        @Override
        public void run() {
            lock.lock();//获得锁
            try {
                while (true) {
                    if (cond % 3 == 0) {
                        System.out.print("C");
                        cond++;
                        condition.signalAll();
                        break;
                    } else {
                        try {
                            condition.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                lock.unlock();
            }

        }
    }*/
}
