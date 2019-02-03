package com.stephengoeddel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParallelMergeSorter<T extends Comparable> {
    private List<T> elements;
    private int numberOfThreads;

    public ParallelMergeSorter(List<T> elements, int numberOfThreads) {
        this.elements = elements;
        this.numberOfThreads = numberOfThreads;
    }

    public List<T> sort() throws InterruptedException {
        List<T> result = Collections.synchronizedList(new ArrayList<>());
        int itemsPerSublist = elements.size() / numberOfThreads;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            int startOfSublistIndex = i * itemsPerSublist;
            int endOfSublistIndex = startOfSublistIndex + itemsPerSublist;
            List<T> sublist = elements.subList(startOfSublistIndex, endOfSublistIndex);

            Sorter<T> sorter = new Sorter<>(sublist, result);
            Thread sorterThread = new Thread(sorter);
            sorterThread.run();
            threads.add(sorterThread);

        }

        for (Thread thread : threads) {
            thread.join();
        }

        return result;
    }
}
