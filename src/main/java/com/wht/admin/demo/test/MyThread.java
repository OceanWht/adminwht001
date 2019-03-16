package com.wht.admin.demo.test;

public class MyThread implements Runnable {

    private String name;
    private Object prev;
    private Object self;

    public MyThread(String name, Object prev, Object self) {
        this.name = name;
        this.prev = prev;
        this.self = self;
    }

    @Override
    public void run() {

        int i = 0;
        while (++i <= 10) {
            synchronized (prev) {
                synchronized (self) {
                    System.out.print(name);
                    self.notifyAll();
                }
                try {
                    prev.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("done");
    }

    public static void main(String[] args) throws InterruptedException {
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        MyThread threadA = new MyThread("A", c, a);
        MyThread threadB = new MyThread("B", a, b);
        MyThread threadC = new MyThread("C", b, c);
        Thread A = new Thread(threadA);
        Thread B = new Thread(threadB);
        Thread C = new Thread(threadC);

        A.start();
        Thread.sleep(100);
        B.start();
        Thread.sleep(100);
        C.start();
        Thread.sleep(3000);

        while (!A.isInterrupted()) {
            try {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {
                A.interrupt();
            }

        }


        while (!B.isInterrupted()) {
            try {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {
                B.interrupt();
            }

        }

        while (!C.isInterrupted()) {
            try {
                Thread.sleep(3000);
            }
            catch(InterruptedException e)
            {
               C.interrupt();
            }

        }
    }
}

