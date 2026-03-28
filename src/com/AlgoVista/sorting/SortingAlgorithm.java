package com.AlgoVista.sorting;

import java.util.List;

public interface SortingAlgorithm {
    List<StateSnapshot> generateSnapshots(int[] initialArray);
    String getName();
    String getTimeComplexity();
    String[] getCodeSnippet(String language);
}
