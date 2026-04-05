package com.AlgoVista.sorting.algorithms;

import com.AlgoVista.sorting.SortingAlgorithm;
import com.AlgoVista.sorting.StateSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertionSortAlgorithm implements SortingAlgorithm {

    @Override
    public List<StateSnapshot> generateSnapshots(int[] initialArray) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        int[] arr = initialArray.clone();
        int n = arr.length;
        List<Integer> sorted = new ArrayList<>();
        sorted.add(0);

        Map<String, String> vars = new HashMap<>();

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            vars.put("i", String.valueOf(i));
            vars.put("key", String.valueOf(key));
            vars.put("j", String.valueOf(j));
            
            snapshots.add(new StateSnapshot(arr, new int[]{i}, getSortedArray(sorted), 1, new HashMap<>(vars), "Selected key " + key + " to insert into sorted portion"));

            while (j >= 0 && arr[j] > key) {
                snapshots.add(new StateSnapshot(arr, new int[]{j, j + 1}, getSortedArray(sorted), 4, new HashMap<>(vars), "Checking if " + arr[j] + " > " + key));
                
                arr[j + 1] = arr[j];
                snapshots.add(new StateSnapshot(arr, new int[]{j, j + 1}, getSortedArray(sorted), 5, new HashMap<>(vars), "Shifted " + arr[j] + " to the right"));
                
                j = j - 1;
                vars.put("j", String.valueOf(j));
            }
            arr[j + 1] = key;
            sorted.add(i);
            snapshots.add(new StateSnapshot(arr, new int[]{j + 1}, getSortedArray(sorted), 8, new HashMap<>(vars), "Inserted key " + key + " at index " + (j + 1)));
        }
        
        snapshots.add(new StateSnapshot(arr, new int[]{}, getSortedArray(sorted), 9, new HashMap<>(vars), "Insertion Sort Complete!"));
        return snapshots;
    }

    private int[] getSortedArray(List<Integer> list) {
        return list.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public String getName() { return "Insertion Sort"; }

    @Override
    public String getTimeComplexity() { return "O(n²)"; }

    @Override
    public String[] getCodeSnippet(String language) {
        if ("C++".equals(language)) {
            return new String[]{
                "void insertionSort(int arr[], int n) {",
                "  for (int i = 1; i < n; i++) {",
                "    int key = arr[i];",
                "    int j = i - 1;",
                "    while (j >= 0 && arr[j] > key) {",
                "      arr[j + 1] = arr[j];",
                "      j = j - 1;",
                "    }",
                "    arr[j + 1] = key;",
                "  }",
                "}"
            };
        } else {
            return new String[]{
                "void insertionSort(int[] arr) {",
                "  for (int i = 1; i < arr.length; i++) {",
                "    int key = arr[i];",
                "    int j = i - 1;",
                "    while (j >= 0 && arr[j] > key) {",
                "      arr[j + 1] = arr[j];",
                "      j = j - 1;",
                "    }",
                "    arr[j + 1] = key;",
                "  }",
                "}"
            };
        }
    }
}
