package com.felix.sort;

import java.util.Arrays;

public class BubbleSortUtils {

    public static void main(String[] args) {
        int[] arr = {2, 5, 1, 4, 9, 0, 7, 6, 2, 8};
        System.out.println(Arrays.toString(arr));
        bubbleSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    SortUtils.swap(arr, j, j + 1);
                }
            }
        }
    }
}
