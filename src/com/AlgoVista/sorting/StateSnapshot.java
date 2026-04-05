package com.AlgoVista.sorting;

import java.util.Arrays;
import java.util.Map;

public class StateSnapshot {
    private final int[] array;
    private final int[] activeIndices;
    private final int[] sortedIndices;
    private final int activeCodeLine;
    private final Map<String, String> variables;
    private final String statusMessage;

    public StateSnapshot(int[] array, int[] activeIndices, int[] sortedIndices, 
                         int activeCodeLine, Map<String, String> variables, String statusMessage) {
        this.array = Arrays.copyOf(array, array.length);
        this.activeIndices = activeIndices != null ? Arrays.copyOf(activeIndices, activeIndices.length) : new int[0];
        this.sortedIndices = sortedIndices != null ? Arrays.copyOf(sortedIndices, sortedIndices.length) : new int[0];
        this.activeCodeLine = activeCodeLine;
        this.variables = variables;
        this.statusMessage = statusMessage;
    }

    public int[] getArray() { return array; }
    public int[] getActiveIndices() { return activeIndices; }
    public int[] getSortedIndices() { return sortedIndices; }
    public int getActiveCodeLine() { return activeCodeLine; }
    public Map<String, String> getVariables() { return variables; }
    public String getStatusMessage() { return statusMessage; }
}
