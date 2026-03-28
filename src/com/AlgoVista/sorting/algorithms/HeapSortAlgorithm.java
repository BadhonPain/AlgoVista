package com.AlgoVista.sorting.algorithms;

import com.AlgoVista.sorting.SortingAlgorithm;
import com.AlgoVista.sorting.StateSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeapSortAlgorithm implements SortingAlgorithm {

    @Override
    public List<StateSnapshot> generateSnapshots(int[] initialArray) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        int[] arr = initialArray.clone();
        int n = arr.length;
        List<Integer> sorted = new ArrayList<>();
        Map<String, String> vars = new HashMap<>();

        vars.put("n", String.valueOf(n));

        for (int i = n / 2 - 1; i >= 0; i--) {
            vars.put("i", String.valueOf(i));
            snapshots.add(new StateSnapshot(arr, new int[]{i}, getSortedArray(sorted), 2, new HashMap<>(vars), "Building initial max heap, heapify at index " + i));
            heapify(arr, n, i, snapshots, sorted, vars);
        }

        for (int i = n - 1; i > 0; i--) {
            vars.put("i", String.valueOf(i));
            snapshots.add(new StateSnapshot(arr, new int[]{0, i}, getSortedArray(sorted), 4, new HashMap<>(vars), "Moving current root (max element) to end"));
            
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            
            sorted.add(i);
            snapshots.add(new StateSnapshot(arr, new int[]{0, i}, getSortedArray(sorted), 4, new HashMap<>(vars), "Swapped root to index " + i));

            snapshots.add(new StateSnapshot(arr, new int[]{0}, getSortedArray(sorted), 5, new HashMap<>(vars), "Calling heapify on reduced heap for root"));
            heapify(arr, i, 0, snapshots, sorted, vars);
        }
        
        sorted.add(0);
        snapshots.add(new StateSnapshot(arr, new int[]{}, getSortedArray(sorted), 7, new HashMap<>(vars), "Heap Sort Complete!"));
        return snapshots;
    }

    private void heapify(int arr[], int n, int i, List<StateSnapshot> snapshots, List<Integer> sorted, Map<String, String> vars) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        
        vars.put("largest", String.valueOf(largest));
        vars.put("l", String.valueOf(l));
        vars.put("r", String.valueOf(r));
        
        snapshots.add(new StateSnapshot(arr, new int[]{i, l, Math.min(r, n-1)}, getSortedArray(sorted), 9, new HashMap<>(vars), "heapify checking node " + i + " and children"));

        if (l < n && arr[l] > arr[largest]) {
            largest = l;
            vars.put("largest", String.valueOf(largest));
        }

        if (r < n && arr[r] > arr[largest]) {
            largest = r;
            vars.put("largest", String.valueOf(largest));
        }

        if (largest != i) {
            snapshots.add(new StateSnapshot(arr, new int[]{i, largest}, getSortedArray(sorted), 13, new HashMap<>(vars), "Swapping node " + i + " with larger child " + largest));
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            
            snapshots.add(new StateSnapshot(arr, new int[]{i, largest}, getSortedArray(sorted), 14, new HashMap<>(vars), "Recursively heapifying the affected sub-tree"));
            heapify(arr, n, largest, snapshots, sorted, vars);
        }
    }

    private int[] getSortedArray(List<Integer> list) {
        return list.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public String getName() { return "Heap Sort"; }

    @Override
    public String getTimeComplexity() { return "O(n log n)"; }

    @Override
    public String[] getCodeSnippet(String language) {
        if ("C++".equals(language)) {
            return new String[]{
                "void heapSort(int arr[], int n) {",
                "  for (int i = n / 2 - 1; i >= 0; i--)",
                "    heapify(arr, n, i);",
                "  for (int i = n - 1; i > 0; i--) {",
                "    std::swap(arr[0], arr[i]);",
                "    heapify(arr, i, 0);",
                "  }",
                "}",
                "void heapify(int arr[], int n, int i) {",
                "  int largest = i; int l = 2*i + 1; int r = 2*i + 2;",
                "  if (l < n && arr[l] > arr[largest]) largest = l;",
                "  if (r < n && arr[r] > arr[largest]) largest = r;",
                "  if (largest != i) {",
                "    std::swap(arr[i], arr[largest]);",
                "    heapify(arr, n, largest);",
                "  }",
                "}"
            };
        } else {
            return new String[]{
                "void heapSort(int arr[]) {",
                "  int n = arr.length;",
                "  for (int i = n / 2 - 1; i >= 0; i--)",
                "    heapify(arr, n, i);",
                "  for (int i = n - 1; i > 0; i--) {",
                "    int temp = arr[0]; arr[0] = arr[i]; arr[i] = temp;",
                "    heapify(arr, i, 0);",
                "  }",
                "}",
                "void heapify(int arr[], int n, int i) {",
                "  int largest = i; int l = 2*i + 1; int r = 2*i + 2;",
                "  if (l < n && arr[l] > arr[largest]) largest = l;",
                "  if (r < n && arr[r] > arr[largest]) largest = r;",
                "  if (largest != i) {",
                "    int swap = arr[i]; arr[i] = arr[largest]; arr[largest] = swap;",
                "    heapify(arr, n, largest);",
                "  }",
                "}"
            };
        }
    }
}
