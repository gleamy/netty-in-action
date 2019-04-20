package nia.chapter7;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Listing 7.1 Executing tasks in an event loop
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EventLoopExamples {
    /**
     * Listing 7.1 Executing tasks in an event loop
     * */
    public static void executeTaskInEventLoop() {
        boolean terminated = true;
        //...
        while (!terminated) {
            List<Runnable> readyEvents = blockUntilEventsReady();
            for (Runnable ev: readyEvents) {
                ev.run();
            }
        }
    }

    private static final List<Runnable> blockUntilEventsReady() {
        return Collections.<Runnable>singletonList(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        EventLoop loop = new DefaultEventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("1. run in thread " + Thread.currentThread().getName());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1, TimeUnit.SECONDS);

        loop.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("2. run in thread " + Thread.currentThread().getName());
            }
        }, 1, TimeUnit.SECONDS);
        Thread.sleep(2000);
        Future<?> future = loop.shutdownGracefully(10, 10, TimeUnit.SECONDS);
        future.addListener(new FutureListener<Object>() {
            @Override
            public void operationComplete(Future<Object> future) throws Exception {
                System.out.println(future.isSuccess() + " - " + Thread.currentThread().getName());
            }
        });
    }
}
