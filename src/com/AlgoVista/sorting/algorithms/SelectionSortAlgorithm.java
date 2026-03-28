package com.AlgoVista.sorting.algorithms;

import com.AlgoVista.sorting.SortingAlgorithm;
import com.AlgoVista.sorting.StateSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectionSortAlgorithm implements SortingAlgorithm {

    @Override
    public List<StateSnapshot> generateSnapshots(int[] initialArray) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        int[] arr = initialArray.clone();
        int n = arr.length;
        List<Integer> sorted = new ArrayList<>();

        Map<String, String> vars = new HashMap<>();
        vars.put("n", String.valueOf(n));

        for (int i = 0; i < n - 1; i++) {
            vars.put("i", String.valueOf(i));
            int min_idx = i;
            vars.put("min_idx", String.valueOf(min_idx));
            
            snapshots.add(new StateSnapshot(arr, new int[]{i}, getSortedArray(sorted), 1, new HashMap<>(vars), "Starting pass " + i + ", current min is at index " + min_idx));

            for (int j = i + 1; j < n; j++) {
                vars.put("j", String.valueOf(j));
                snapshots.add(new StateSnapshot(arr, new int[]{j, min_idx}, getSortedArray(sorted), 3, new HashMap<>(vars), "Comparing arr[" + j + "] with min val " + arr[min_idx]));

                if (arr[j] < arr[min_idx]) {
                    min_idx = j;
                    vars.put("min_idx", String.valueOf(min_idx));
                    snapshots.add(new StateSnapshot(arr, new int[]{min_idx}, getSortedArray(sorted), 4, new HashMap<>(vars), "Found new minimum at index " + j));
                }
            }
            
            snapshots.add(new StateSnapshot(arr, new int[]{i, min_idx}, getSortedArray(sorted), 7, new HashMap<>(vars), "Swapping arr[" + i + "] with min element arr[" + min_idx + "]"));
            int temp = arr[min_idx];
            arr[min_idx] = arr[i];
            arr[i] = temp;
            
            sorted.add(i);
        }
        
        sorted.add(n - 1);
        snapshots.add(new StateSnapshot(arr, new int[]{}, getSortedArray(sorted), 9, new HashMap<>(vars), "Selection Sort Complete!"));
        return snapshots;
    }

    private int[] getSortedArray(List<Integer> list) {
        return list.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public String getName() { return "Selection Sort"; }

    @Override
    public String getTimeComplexity() { return "O(n²)"; }

    @Override
    public String[] getCodeSnippet(String language) {
        if ("C++".equals(language)) {
            return new String[]{
                "void selectionSort(int arr[], int n) {",
                "  for (int i = 0; i < n - 1; i++) {",
                "    int min_idx = i;",
                "    for (int j = i + 1; j < n; j++) {",
                "      if (arr[j] < arr[min_idx]) {",
                "        min_idx = j;",
                "      }",
                "    }",
                "    std::swap(arr[i], arr[min_idx]);",
                "  }",
                "}"
            };
        } else {
            return new String[]{
                "void selectionSort(int[] arr) {",
                "  for (int i = 0; i < arr.length - 1; i++) {",
                "    int min_idx = i;",
                "    for (int j = i + 1; j < arr.length; j++) {",
                "      if (arr[j] < arr[min_idx]) {",
                "        min_idx = j;",
                "      }",
                "    }",
                "    int temp = arr[min_idx];",
                "    arr[min_idx] = arr[i];",
                "    arr[i] = temp;",
                "  }",
                "}"
            };
        }
    }
}
