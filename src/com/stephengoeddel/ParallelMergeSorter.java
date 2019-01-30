package com.stephengoeddel;

import java.util.ArrayList;
import java.util.List;

public class ParallelMergeSorter<T extends Comparable> {
    private List<T> elements;
    private int numberOfThreads;

    public ParallelMergeSorter(List<T> elements, int numberOfThreads) {
        this.elements = elements;
        this.numberOfThreads = numberOfThreads;
    }

    public List<T> sort() throws InterruptedException {
        int itemsPerSublist = elements.size() / numberOfThreads;
        List<Thread> threads = new ArrayList<>();
        List<SorterWorker<T>> sorters = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            int startOfSublistIndex = i * itemsPerSublist;
            int endOfSublistIndex = startOfSublistIndex + itemsPerSublist;
            List<T> sublist = elements.subList(startOfSublistIndex, endOfSublistIndex);

            SorterWorker<T> sorter = new SorterWorker<>(sublist);
            Thread sorterThread = new Thread(sorter);
            sorterThread.run();
            threads.add(sorterThread);
            sorters.add(sorter);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        List<T> result = sorters.get(0).getAllElements();
        for (int i = 1; i < sorters.size(); i++) {
            result = mergeLists(result, sorters.get(i).getAllElements());
        }

        return result;
    }

    private List<T> mergeLists(List<T> leftElements, List<T> rightElements) {
        List<T> mergedElements = new ArrayList<>();

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
}
