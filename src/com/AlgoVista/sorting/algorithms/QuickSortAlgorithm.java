package com.AlgoVista.sorting.algorithms;

import com.AlgoVista.sorting.SortingAlgorithm;
import com.AlgoVista.sorting.StateSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickSortAlgorithm implements SortingAlgorithm {

    @Override
    public List<StateSnapshot> generateSnapshots(int[] initialArray) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        int[] arr = initialArray.clone();
        List<Integer> sorted = new ArrayList<>();
        Map<String, String> vars = new HashMap<>();

        quickSort(arr, 0, arr.length - 1, snapshots, sorted, vars);
        
        for(int k=0; k<arr.length; k++) if(!sorted.contains(k)) sorted.add(k);
        snapshots.add(new StateSnapshot(arr, new int[]{}, getSortedArray(sorted), 17, new HashMap<>(vars), "Quick Sort Complete!"));
        return snapshots;
    }

    private void quickSort(int[] arr, int low, int high, List<StateSnapshot> snapshots, List<Integer> sorted, Map<String, String> vars) {
        vars.put("low", String.valueOf(low));
        vars.put("high", String.valueOf(high));
        
        if (low < high) {
            snapshots.add(new StateSnapshot(arr, new int[]{low, high}, getSortedArray(sorted), 1, new HashMap<>(vars), "quickSort(" + low + ", " + high + ")"));
            int pi = partition(arr, low, high, snapshots, sorted, vars);
            
            vars.put("pi", String.valueOf(pi));
            vars.put("low", String.valueOf(low));
            vars.put("high", String.valueOf(high));
            
            snapshots.add(new StateSnapshot(arr, new int[]{pi}, getSortedArray(sorted), 3, new HashMap<>(vars), "Recursive call left: quickSort(" + low + ", " + (pi - 1) + ")"));
            quickSort(arr, low, pi - 1, snapshots, sorted, vars);
            
            vars.put("low", String.valueOf(low));
            vars.put("high", String.valueOf(high));
            vars.put("pi", String.valueOf(pi));
            
            snapshots.add(new StateSnapshot(arr, new int[]{pi}, getSortedArray(sorted), 4, new HashMap<>(vars), "Recursive call right: quickSort(" + (pi + 1) + ", " + high + ")"));
            quickSort(arr, pi + 1, high, snapshots, sorted, vars);
        } else if (low == high) {
            sorted.add(low);
        }
    }

    private int partition(int[] arr, int low, int high, List<StateSnapshot> snapshots, List<Integer> sorted, Map<String, String> vars) {
        int pivot = arr[high];
        int i = (low - 1);
        vars.put("pivot", String.valueOf(pivot));
        vars.put("i", String.valueOf(i));
        
        snapshots.add(new StateSnapshot(arr, new int[]{high}, getSortedArray(sorted), 8, new HashMap<>(vars), "Pivot chosen: " + pivot + " at index " + high));

        for (int j = low; j <= high - 1; j++) {
            vars.put("j", String.valueOf(j));
            snapshots.add(new StateSnapshot(arr, new int[]{j, high}, getSortedArray(sorted), 10, new HashMap<>(vars), "Comparing arr[" + j + "]=" + arr[j] + " with pivot " + pivot));
            
            if (arr[j] < pivot) {
                i++;
                vars.put("i", String.valueOf(i));
                
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                snapshots.add(new StateSnapshot(arr, new int[]{i, j}, getSortedArray(sorted), 12, new HashMap<>(vars), "Swapped arr[" + i + "] and arr[" + j + "]"));
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        
        sorted.add(i + 1);
        snapshots.add(new StateSnapshot(arr, new int[]{i + 1, high}, getSortedArray(sorted), 15, new HashMap<>(vars), "Placed pivot at index " + (i + 1)));

        return (i + 1);
    }

    private int[] getSortedArray(List<Integer> list) {
        return list.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public String getName() { return "Quick Sort"; }

    @Override
    public String getTimeComplexity() { return "O(n log n)"; }

    @Override
    public String[] getCodeSnippet(String language) {
        if ("C++".equals(language)) {
            return new String[]{
                "void quickSort(int arr[], int low, int high) {",
                "  if (low < high) {",
                "    int pi = partition(arr, low, high);",
                "    quickSort(arr, low, pi - 1);",
                "    quickSort(arr, pi + 1, high);",
                "  }",
                "}",
                "int partition(int arr[], int low, int high) {",
                "  int pivot = arr[high];",
                "  int i = low - 1;",
                "  for (int j = low; j <= high - 1; j++) {",
                "    if (arr[j] < pivot) {",
                "      i++; std::swap(arr[i], arr[j]);",
                "    }",
                "  }",
                "  std::swap(arr[i + 1], arr[high]);",
                "  return (i + 1);",
                "}"
            };
        } else {
            return new String[]{
                "void quickSort(int[] arr, int low, int high) {",
                "  if (low < high) {",
                "    int pi = partition(arr, low, high);",
                "    quickSort(arr, low, pi - 1);",
                "    quickSort(arr, pi + 1, high);",
                "  }",
                "}",
                "int partition(int[] arr, int low, int high) {",
                "  int pivot = arr[high];",
                "  int i = low - 1;",
                "  for (int j = low; j <= high - 1; j++) {",
                "    if (arr[j] < pivot) {",
                "      i++;",
                "      int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;",
                "    }",
                "  }",
                "  int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;",
                "  return i + 1;",
                "}"
            };
        }
    }
}
