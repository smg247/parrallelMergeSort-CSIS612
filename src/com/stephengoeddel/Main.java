package com.stephengoeddel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    private static int NUMBER_OF_THREADS = 4;
    private static int NUMBER_OF_ELEMENTS = 10000000;

    public static void main(String[] args) throws InterruptedException {

        List<Integer> randomIntList = randomIntegerList(NUMBER_OF_ELEMENTS);
        List<Integer> nonParallelResult = benchmarkNonParallelVersion(new ArrayList<>(randomIntList));
        List<Integer> parallelResult = benchmarkParallelVersion(new ArrayList<>(randomIntList));
        System.out.println(nonParallelResult.equals(parallelResult));
    }

    private static List<Integer> benchmarkNonParallelVersion(List<Integer> randomIntList) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Sorter<Integer> sorter = new Sorter<>(randomIntList);
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
        int itemsPerSublist = randomIntList.size() / 4;
        List<Thread> threads = new ArrayList<>();
        List<Sorter<Integer>> sorters = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            List<Integer> sublist = new ArrayList<>();
            for (int j = 0; j < itemsPerSublist; j++) {
                sublist.add(randomIntList.remove(0));
            }

            Sorter<Integer> sorter = new Sorter<>(sublist);
            Thread sorterThread = new Thread(sorter);
            sorterThread.run();
            threads.add(sorterThread);
            sorters.add(sorter);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        List<Integer> result = sorters.get(0).getAllElements();
        for (int i = 1; i < sorters.size(); i++) {
            result = mergeLists(result, sorters.get(i).getAllElements());
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Parallel Version: ");
        System.out.println("Sorted: " + (itemsPerSublist * NUMBER_OF_THREADS) + " elements in: " + (endTime - startTime) + " millis");

        return result;

    }

    //TODO: anything better we can do here than just copying this?
    private static List<Integer> mergeLists(List<Integer> leftElements, List<Integer> rightElements) {
        List<Integer> mergedElements = new ArrayList<>();

        while (!leftElements.isEmpty() && !rightElements.isEmpty()) {
            if (leftElements.get(0).compareTo(rightElements.get(0)) <= 0) {
                mergedElements.add(leftElements.remove(0));
            } else {
                mergedElements.add(rightElements.remove(0));
            }
        }

        // At this point either left or right may contain elements, but not both, so just add them all
        mergedElements.addAll(leftElements);
        mergedElements.addAll(rightElements);

        return mergedElements;
    }

    private static List<Integer> randomIntegerList(int size) {
        Random random = new Random();
        return random.ints(0, 1000000).limit(size).boxed().collect(Collectors.toList());
    }
}
