package com.AlgoVista.sorting.algorithms;

import com.AlgoVista.sorting.StateSnapshot;
import com.AlgoVista.sorting.SortingAlgorithm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BucketSortAlgorithm implements SortingAlgorithm {

    @Override
    public List<StateSnapshot> generateSnapshots(int[] initialArray) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        int[] arr = initialArray.clone();
        int n = arr.length;

        if (n <= 0) return snapshots;

        int max = arr[0];
        snapshots.add(new StateSnapshot(arr, new int[]{0}, new int[]{}, 0, new HashMap<>(), "Finding max for bucket bounds"));
        for (int i = 1; i < n; i++) {
            if (arr[i] > max) max = arr[i];
        }

        int bucketCount = (int) Math.sqrt(n);
        List<List<Integer>> buckets = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            int bucketIndex = (arr[i] * bucketCount) / (max + 1);
            if (bucketIndex >= bucketCount) { // Ensure within bounds
                bucketIndex = bucketCount - 1;
            }
            buckets.get(bucketIndex).add(arr[i]);
            snapshots.add(new StateSnapshot(arr.clone(), new int[]{i}, new int[]{}, 3, new HashMap<>(), "Placing " + arr[i] + " in bucket " + bucketIndex));
        }

        for (int i = 0; i < buckets.size(); i++) {
            Collections.sort(buckets.get(i));
            snapshots.add(new StateSnapshot(arr.clone(), new int[]{}, new int[]{}, 4, new HashMap<>(), "Sorting bucket " + i));
        }

        int index = 0;
        int[] sortedIndices = new int[n];
        for (int i = 0; i < buckets.size(); i++) {
            for (int val : buckets.get(i)) {
                arr[index] = val;
                sortedIndices[index] = index;
                snapshots.add(new StateSnapshot(arr.clone(), new int[]{index}, sortedIndices.clone(), 6, new HashMap<>(), "Merging bucket " + i));
                index++;
            }
        }

        snapshots.add(new StateSnapshot(arr.clone(), new int[]{}, sortedIndices.clone(), 8, new HashMap<>(), "Bucket Sort Complete!"));

        return snapshots;
    }

    @Override
    public String getName() {
        return "Bucket Sort";
    }

    @Override
    public String[] getCodeSnippet(String language) {
        return new String[]{
            "int bucketCount = sqrt(n);",
            "List<List<Integer>> buckets = new ArrayList<>();",
            "for (int i = 0; i < bucketCount; i++) buckets.add(new ArrayList<>());",
            "for (int i = 0; i < n; i++) {",
            "    int bucketIndex = (arr[i] * bucketCount) / (max + 1);",
            "    buckets.get(bucketIndex).add(arr[i]);",
            "}",
            "for (int i = 0; i < bucketCount; i++) {",
            "    Collections.sort(buckets.get(i));",
            "}",
            "int index = 0;",
            "for (int i = 0; i < buckets.size(); i++) {",
            "    for (int val : buckets.get(i)) arr[index++] = val;",
            "}"
        };
    }

    @Override
    public String getTimeComplexity() {
        return "O(n + k)";
    }
}
