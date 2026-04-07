package com.AlgoVista.sorting.algorithms;

import com.AlgoVista.sorting.SortingAlgorithm;
import com.AlgoVista.sorting.StateSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BubbleSortAlgorithm implements SortingAlgorithm {

    @Override
    public List<StateSnapshot> generateSnapshots(int[] initialArray) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        int[] arr = initialArray.clone();
        int n = arr.length;
        List<Integer> sorted = new ArrayList<>();

        Map<String, String> vars = new HashMap<>();
        vars.put("n", String.valueOf(n));
        snapshots.add(new StateSnapshot(arr, new int[]{}, getSortedArray(sorted), 0, new HashMap<>(vars), "Starting Bubble Sort"));

        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            vars.put("i", String.valueOf(i));
            vars.put("swapped", "false");
            snapshots.add(new StateSnapshot(arr, new int[]{}, getSortedArray(sorted), 1, new HashMap<>(vars), "Outer loop: passes completed = " + i));

            for (int j = 0; j < n - i - 1; j++) {
                vars.put("j", String.valueOf(j));
                snapshots.add(new StateSnapshot(arr, new int[]{j, j + 1}, getSortedArray(sorted), 2, new HashMap<>(vars), "Comparing arr[" + j + "] and arr[" + (j + 1) + "]"));
                snapshots.add(new StateSnapshot(arr, new int[]{j, j + 1}, getSortedArray(sorted), 3, new HashMap<>(vars), "Checking if " + arr[j] + " > " + arr[j + 1]));

                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                    vars.put("swapped", "true");
                    snapshots.add(new StateSnapshot(arr, new int[]{j, j + 1}, getSortedArray(sorted), 4, new HashMap<>(vars), "Swapped " + arr[j+1] + " and " + arr[j]));
                }
            }
            sorted.add(n - i - 1);
            snapshots.add(new StateSnapshot(arr, new int[]{}, getSortedArray(sorted), 7, new HashMap<>(vars), "Largest element bubbled to correct position"));
            
            if (!swapped) {
                break;
            }
        }
        
        for(int k=0; k<n; k++) if(!sorted.contains(k)) sorted.add(k);
        snapshots.add(new StateSnapshot(arr, new int[]{}, getSortedArray(sorted), 8, new HashMap<>(vars), "Array is fully sorted!"));
        return snapshots;
    }

    private int[] getSortedArray(List<Integer> list) {
        return list.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public String getName() { return "Bubble Sort"; }

    @Override
    public String getTimeComplexity() { return "O(n²)"; }

    @Override
    public String[] getCodeSnippet(String language) {
        if ("C++".equals(language)) {
            return new String[]{
                "void bubbleSort(int arr[], int n) {",
                "  for (int i = 0; i < n - 1; i++) {",
                "    for (int j = 0; j < n - i - 1; j++) {",
                "      if (arr[j] > arr[j + 1]) {",
                "        std::swap(arr[j], arr[j + 1]);",
                "      }",
                "    }",
                "  }",
                "}"
            };
        } else {
            return new String[]{
                "void bubbleSort(int[] arr) {",
                "  for (int i = 0; i < arr.length - 1; i++) {",
                "    for (int j = 0; j < arr.length - i - 1; j++) {",
                "      if (arr[j] > arr[j + 1]) {",
                "        int temp = arr[j];",
                "        arr[j] = arr[j + 1];",
                "        arr[j + 1] = temp;",
                "      }",
                "    }",
                "  }",
                "}"
            };
        }
    }
}
