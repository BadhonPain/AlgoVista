package com.AlgoVista.sorting.algorithms;

import com.AlgoVista.sorting.StateSnapshot;
import com.AlgoVista.sorting.SortingAlgorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RadixSortAlgorithm implements SortingAlgorithm {

    @Override
    public List<StateSnapshot> generateSnapshots(int[] initialArray) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        int[] arr = initialArray.clone();
        int n = arr.length;

        if (n == 0) return snapshots;

        int max = arr[0];
        snapshots.add(new StateSnapshot(arr, new int[]{0}, new int[]{}, 0, new HashMap<>(), "Finding max element"));
        for (int i = 1; i < n; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        for (int exp = 1; max / exp > 0; exp *= 10) {
            countSort(arr, n, exp, snapshots);
        }

        int[] sortedIndices = new int[n];
        for (int i = 0; i < n; i++) sortedIndices[i] = i;
        snapshots.add(new StateSnapshot(arr.clone(), new int[]{}, sortedIndices.clone(), 9, new HashMap<>(), "Radix Sort Complete!"));

        return snapshots;
    }

    private void countSort(int[] arr, int n, int exp, List<StateSnapshot> snapshots) {
        int[] output = new int[n];
        int[] count = new int[10];
        Arrays.fill(count, 0);

        for (int i = 0; i < n; i++) {
            count[(arr[i] / exp) % 10]++;
            snapshots.add(new StateSnapshot(arr.clone(), new int[]{i}, new int[]{}, 3, new HashMap<>(), "Counting digit " + ((arr[i] / exp) % 10)));
        }

        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        for (int i = n - 1; i >= 0; i--) {
            output[count[(arr[i] / exp) % 10] - 1] = arr[i];
            count[(arr[i] / exp) % 10]--;
            snapshots.add(new StateSnapshot(output.clone(), new int[]{}, new int[]{}, 6, new HashMap<>(), "Placing element based on digit"));
        }

        for (int i = 0; i < n; i++) {
            arr[i] = output[i];
            snapshots.add(new StateSnapshot(arr.clone(), new int[]{i}, new int[]{}, 8, new HashMap<>(), "Copying back for exponential " + exp));
        }
    }

    @Override
    public String getName() {
        return "Radix Sort";
    }

    @Override
    public String[] getCodeSnippet(String language) {
        return new String[]{
            "int max = findMax(arr);",
            "for (int exp = 1; max / exp > 0; exp *= 10)",
            "    countSort(arr, n, exp);",
            "void countSort(int arr[], int n, int exp) {",
            "    int output[] = new int[n];",
            "    int count[] = new int[10];",
            "    for (int i = 0; i < n; i++) count[(arr[i] / exp) % 10]++;",
            "    for (int i = 1; i < 10; i++) count[i] += count[i - 1];",
            "    for (int i = n - 1; i >= 0; i--) {",
            "        output[count[(arr[i] / exp) % 10] - 1] = arr[i];",
            "        count[(arr[i] / exp) % 10]--; }",
            "    for (int i = 0; i < n; i++) arr[i] = output[i];",
            "}"
        };
    }

    @Override
    public String getTimeComplexity() {
        return "O(d * (n + b))";
    }
}
