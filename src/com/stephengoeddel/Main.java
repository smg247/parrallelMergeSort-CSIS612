package com.stephengoeddel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    private static int NUMBER_OF_THREADS = 2;
    private static int NUMBER_OF_ELEMENTS = 1000000;

    public static void main(String[] args) throws InterruptedException {

        List<Integer> randomIntList = randomIntegerList(NUMBER_OF_ELEMENTS);
        List<Integer> nonParallelResult = benchmarkNonParallelVersion(new ArrayList<>(randomIntList));
        List<Integer> parallelResult = benchmarkParallelVersion(new ArrayList<>(randomIntList));
        System.out.println("Safety check, did both produce the same result? " +  nonParallelResult.equals(parallelResult));
    }

    private static List<Integer> benchmarkNonParallelVersion(List<Integer> randomIntList) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        SorterWorker<Integer> sorter = new SorterWorker<>(randomIntList);
        Thread sorterThread = new Thread(sorter);
        sorterThread.run();
        sorterThread.join();
        long endTime = System.currentTimeMillis();
        System.out.println("Non-Parallel Version: ");
        System.out.println("Sorted: " + randomIntList.size() + " elements in: " + (endTime - startTime) + " millis");

        return sorter.getAllElements();
    }

    private static List<Integer> benchmarkParallelVersion(List<Integer> randomIntList) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ParallelMergeSorter<Integer> parallelMergeSorter = new ParallelMergeSorter<>(randomIntList, NUMBER_OF_THREADS);
        List<Integer> result = parallelMergeSorter.sort();
        long endTime = System.currentTimeMillis();

        System.out.println("Parallel Version: ");
        System.out.println("Sorted: " + NUMBER_OF_ELEMENTS + " elements in: " + (endTime - startTime) + " millis");

        return result;

    }

    private static List<Integer> randomIntegerList(int size) {
        Random random = new Random();
        return random.ints(0, 1000000).limit(size).boxed().collect(Collectors.toList());
    }
}
