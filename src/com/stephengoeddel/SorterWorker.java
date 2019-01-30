package com.stephengoeddel;

import java.util.ArrayList;
import java.util.List;

public class SorterWorker<T extends Comparable> implements Runnable {
    private List<T> allElements;

    public SorterWorker(List<T> elements) {
        this.allElements = elements;
    }

    @Override
    public void run() {
        allElements = mergeSort(allElements);
    }

    public List<T> getAllElements() {
        return allElements;
    }

    private List<T> mergeSort(List<T> elements) {
        if (elements.size() <= 1) {
            return elements; // A list of 0 or 1 allElements is sorted
        }

        List<T> leftElements = new ArrayList<>();
        List<T> rightElements = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            T element = elements.get(i);
            if (i < elements.size()/2) {
                leftElements.add(element);
            } else {
                rightElements.add(element);
            }
        }

        leftElements = mergeSort(leftElements);
        rightElements = mergeSort(rightElements);
        return merge(leftElements, rightElements);
    }

    private List<T> merge(List<T> leftElements, List<T> rightElements) {
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
