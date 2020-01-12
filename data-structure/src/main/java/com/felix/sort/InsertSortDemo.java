package com.felix.sort;

import java.util.Arrays;

public class InsertSortDemo {
    public static void main(String[] args) {
        int[] arr = {2, 5, 1, 4, 9, 0, 7, 6, 3, 8};
        System.out.println(Arrays.toString(arr));
        insertSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 2, 5, 1, 4, 9, 0, 7, 6, 2, 8
     * 1, 2, 5, 4, 9, 0, 7, 6, 2, 8
     *
     * @param arr
     */
    public static void insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                for (int j = i - 1; j >= 0 && arr[j] > arr[j + 1]; j--) {
                    SortUtils.swap(arr, j, j + 1);
                }
            }

        }
    }
}
