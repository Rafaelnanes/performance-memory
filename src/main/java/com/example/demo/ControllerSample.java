package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sample")
public class ControllerSample {

  @GetMapping("/single-thread-executor")
  public void singleThreadExecutor() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(sampleTask());
    executor.shutdown();
  }

  @GetMapping("/cached-thread-pool")
  public void cachedThreadPool() {
    ExecutorService executor = Executors.newCachedThreadPool();
    executor.submit(sampleTask());
    executor.shutdown();
  }

  @GetMapping("/fixed-thread-pool")
  public void fixedThreadPool() {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.submit(sampleTask());
    executor.shutdown();
  }

  @GetMapping("/scheduled-thread-pool")
  public void scheduledThreadPool() {
    var executor = Executors.newScheduledThreadPool(2);
    executor.submit(sampleTask());
    executor.shutdown();
  }

  @GetMapping("/thread-pool-executor")
  public void threadPoolExecutor() {
    ExecutorService executor = new ThreadPoolExecutor(
        1, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));
    executor.submit(sampleTask());
    executor.shutdown();
  }

  private Runnable sampleTask() {
    return () -> {
      try {
        Thread.sleep(Duration.ofMillis(500));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    };
  }

}
