package com.AlgoVista.sorting.algorithms;

import com.AlgoVista.sorting.SortingAlgorithm;
import com.AlgoVista.sorting.StateSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeSortAlgorithm implements SortingAlgorithm {

    @Override
    public List<StateSnapshot> generateSnapshots(int[] initialArray) {
        List<StateSnapshot> snapshots = new ArrayList<>();
        int[] arr = initialArray.clone();
        List<Integer> sorted = new ArrayList<>();
        Map<String, String> vars = new HashMap<>();

        mergeSort(arr, 0, arr.length - 1, snapshots, sorted, vars);
        
        for(int k=0; k<arr.length; k++) if(!sorted.contains(k)) sorted.add(k);
        snapshots.add(new StateSnapshot(arr, new int[]{}, getSortedArray(sorted), 10, new HashMap<>(vars), "Merge Sort Complete!"));
        return snapshots;
    }

    private void mergeSort(int[] arr, int l, int r, List<StateSnapshot> snapshots, List<Integer> sorted, Map<String, String> vars) {
        vars.put("l", String.valueOf(l));
        vars.put("r", String.valueOf(r));
        snapshots.add(new StateSnapshot(arr, new int[]{l, r}, getSortedArray(sorted), 1, new HashMap<>(vars), "mergeSort(" + l + ", " + r + ")"));
        
        if (l < r) {
            int m = l + (r - l) / 2;
            vars.put("m", String.valueOf(m));
            snapshots.add(new StateSnapshot(arr, new int[]{m}, getSortedArray(sorted), 2, new HashMap<>(vars), "Midpoint m = " + m));
            
            snapshots.add(new StateSnapshot(arr, new int[]{l, m}, getSortedArray(sorted), 3, new HashMap<>(vars), "Recursive call left: mergeSort(arr, " + l + ", " + m + ")"));
            mergeSort(arr, l, m, snapshots, sorted, vars);
            
            vars.put("l", String.valueOf(l));
            vars.put("r", String.valueOf(r));
            vars.put("m", String.valueOf(m));
            snapshots.add(new StateSnapshot(arr, new int[]{m + 1, r}, getSortedArray(sorted), 4, new HashMap<>(vars), "Recursive call right: mergeSort(arr, " + (m + 1) + ", " + r + ")"));
            mergeSort(arr, m + 1, r, snapshots, sorted, vars);
            
            vars.put("l", String.valueOf(l));
            vars.put("r", String.valueOf(r));
            vars.put("m", String.valueOf(m));
            snapshots.add(new StateSnapshot(arr, new int[]{l, r}, getSortedArray(sorted), 5, new HashMap<>(vars), "Merging halves: [" + l + ".." + m + "] and [" + (m+1) + ".." + r + "]"));
            merge(arr, l, m, r, snapshots, sorted, vars);
        }
    }

    private void merge(int[] arr, int l, int m, int r, List<StateSnapshot> snapshots, List<Integer> sorted, Map<String, String> vars) {
        int n1 = m - l + 1;
        int n2 = r - m;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; ++i) L[i] = arr[l + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[m + 1 + j];

        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            vars.put("i", String.valueOf(i)); vars.put("j", String.valueOf(j)); vars.put("k", String.valueOf(k));
            snapshots.add(new StateSnapshot(arr, new int[]{l + i, m + 1 + j}, getSortedArray(sorted), 7, new HashMap<>(vars), "Merging: comparing L[" + i + "]=" + L[i] + " & R[" + j + "]=" + R[j]));
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            snapshots.add(new StateSnapshot(arr, new int[]{k}, getSortedArray(sorted), 8, new HashMap<>(vars), "Placed " + arr[k] + " at index " + k));
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++; k++;
            snapshots.add(new StateSnapshot(arr, new int[]{k - 1}, getSortedArray(sorted), 9, new HashMap<>(vars), "Copying remaining from L: " + arr[k - 1]));
        }

        while (j < n2) {
            arr[k] = R[j];
            j++; k++;
            snapshots.add(new StateSnapshot(arr, new int[]{k - 1}, getSortedArray(sorted), 9, new HashMap<>(vars), "Copying remaining from R: " + arr[k - 1]));
        }
        
        for(int idx = l; idx <= r; idx++) {
            if(!sorted.contains(idx)) sorted.add(idx);
        }
    }

    private int[] getSortedArray(List<Integer> list) {
        return list.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public String getName() { return "Merge Sort"; }

    @Override
    public String getTimeComplexity() { return "O(n log n)"; }

    @Override
    public String[] getCodeSnippet(String language) {
        if ("C++".equals(language)) {
            return new String[]{
                "void mergeSort(int arr[], int l, int r) {",
                "  if (l >= r) return;",
                "  int m = l + (r - l) / 2;",
                "  mergeSort(arr, l, m);",
                "  mergeSort(arr, m + 1, r);",
                "  merge(arr, l, m, r);",
                "}",
                "// merge combines two sorted halves...",
                "// into the original array.",
                "// ...",
                "// Complete!"
            };
        } else {
            return new String[]{
                "void mergeSort(int[] arr, int l, int r) {",
                "  if (l >= r) return;",
                "  int m = l + (r - l) / 2;",
                "  mergeSort(arr, l, m);",
                "  mergeSort(arr, m + 1, r);",
                "  merge(arr, l, m, r);",
                "}",
                "// merge combines two sorted halves...",
                "// into the original array.",
                "// ...",
                "// Complete!"
            };
        }
    }
}
