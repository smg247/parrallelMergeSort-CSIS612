package com.stephengoeddel;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many random elements would you like to sort?");
        int numberOfElements = scanner.nextInt();
        System.out.println("How many threads for the parallel version?");
        int numberOfThreads = scanner.nextInt();

        List<Integer> randomIntList = randomIntegerList(numberOfElements);
        List<Integer> nonParallelResult = benchmarkNonParallelVersion(new ArrayList<>(randomIntList));
        List<Integer> parallelResult = benchmarkParallelVersion(new ArrayList<>(randomIntList), numberOfThreads);

        assert nonParallelResult.equals(parallelResult) : "Parallel and Sequential results did not match"; // Safety check, did both produce the same result?
        List<Integer> javaSortedResults = new ArrayList<>(nonParallelResult);
        Collections.sort(javaSortedResults);
        assert parallelResult.equals(javaSortedResults) : "Parallel and Collections.sort results did not match"; // And make sure it is actually sorted
    }

    private static List<Integer> benchmarkNonParallelVersion(List<Integer> randomIntList) throws InterruptedException {
        long startTime = System.nanoTime();
        List<Integer> result = new ArrayList<>();
        Sorter<Integer> sorter = new Sorter<>(randomIntList, result);
        Thread sorterThread = new Thread(sorter);
        sorterThread.run();
        sorterThread.join();
        long endTime = System.nanoTime();
        System.out.println("Non-Parallel Version: ");
        System.out.println("Sorted: " + result.size() + " elements in: " + (endTime - startTime) + " nano seconds");

        return result;
    }

    private static List<Integer> benchmarkParallelVersion(List<Integer> randomIntList, int numberOfThreads) throws InterruptedException {
        long startTime = System.nanoTime();
        ParallelMergeSorter<Integer> parallelMergeSorter = new ParallelMergeSorter<>(randomIntList, numberOfThreads);
        List<Integer> result = parallelMergeSorter.sort();
        long endTime = System.nanoTime();

        System.out.println("Parallel Version: ");
        System.out.println("Sorted: " + result.size() + " elements in: " + (endTime - startTime) + " nano seconds");

        return result;

    }

    private static List<Integer> randomIntegerList(int size) {
        Random random = new Random();
        return random.ints(0, 1000000).limit(size).boxed().collect(Collectors.toList());
    }
}
