package com.AlgoVista.sorting.algorithms;

import com.AlgoVista.sorting.StateSnapshot;
import com.AlgoVista.sorting.SortingAlgorithm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CountingSortAlgorithm implements SortingAlgorithm {

    @Override
    public List<StateSnapshot> generateSnapshots(int[] initialArray) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        int[] arr = initialArray.clone();
        int n = arr.length;

        if (n == 0) return snapshots;

        int max = arr[0];
        snapshots.add(new StateSnapshot(arr, new int[]{0}, new int[]{}, 0, new HashMap<>(), "Finding max element"));
        for (int i = 1; i < n; i++) {
            snapshots.add(new StateSnapshot(arr, new int[]{i}, new int[]{}, 1, new HashMap<>(), "Comparing for max"));
            if (arr[i] > max) {
                max = arr[i];
                snapshots.add(new StateSnapshot(arr, new int[]{i}, new int[]{}, 2, new HashMap<>(), "New max found"));
            }
        }

        int[] count = new int[max + 1];
        int[] sortedIndices = new int[n];
        int sortedCount = 0;

        for (int i = 0; i < n; i++) {
            count[arr[i]]++;
            snapshots.add(new StateSnapshot(arr, new int[]{i}, new int[]{}, 4, new HashMap<>(), "Incrementing count for " + arr[i]));
        }

        for (int i = 1; i <= max; i++) {
            count[i] += count[i - 1];
        }

        int[] output = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            output[count[arr[i]] - 1] = arr[i];
            count[arr[i]]--;
            snapshots.add(new StateSnapshot(output.clone(), new int[]{}, new int[]{}, 7, new HashMap<>(), "Placing " + arr[i] + " into correct position"));
        }

        for (int i = 0; i < n; i++) {
            arr[i] = output[i];
            sortedIndices[sortedCount++] = i;
            snapshots.add(new StateSnapshot(arr.clone(), new int[]{i}, sortedIndices.clone(), 9, new HashMap<>(), "Copying back to array"));
        }

        snapshots.add(new StateSnapshot(arr.clone(), new int[]{}, sortedIndices.clone(), 10, new HashMap<>(), "Counting Sort Complete!"));

        return snapshots;
    }

    @Override
    public String getName() {
        return "Counting Sort";
    }

    @Override
    public String[] getCodeSnippet(String language) {
        return new String[]{
            "int max = findMax(arr);",
            "for (int i = 1; i < n; i++) if (arr[i] > max) max = arr[i];",
            "int[] count = new int[max + 1];",
            "for (int i = 0; i < n; i++) count[arr[i]]++;",
            "for (int i = 1; i <= max; i++) count[i] += count[i - 1];",
            "int[] output = new int[n];",
            "for (int i = n - 1; i >= 0; i--) {",
            "    output[count[arr[i]] - 1] = arr[i];",
            "    count[arr[i]]--;",
            "}",
            "for (int i = 0; i < n; i++) arr[i] = output[i];"
        };
    }

    @Override
    public String getTimeComplexity() {
        return "O(n + k)";
    }
}
