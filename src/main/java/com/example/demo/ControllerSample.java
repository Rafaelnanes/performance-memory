package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/sample")
public class ControllerSample {

  @GetMapping
  public String sample() {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    return "Hello World";
  }

}
