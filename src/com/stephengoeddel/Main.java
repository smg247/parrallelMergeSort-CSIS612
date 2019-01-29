package com.stephengoeddel;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Sorter<Integer> sorter = new Sorter<Integer>(randomIntegerList(100));
        Thread sorterThread = new Thread(sorter);
        sorterThread.run();
        sorterThread.join();
        sorter.getAllElements().forEach(System.out::println);
    }

    private static List<Integer> randomIntegerList(int size) {
        Random random = new Random();
        return random.ints(0, 1000000).limit(size).boxed().collect(Collectors.toList());
    }
}
