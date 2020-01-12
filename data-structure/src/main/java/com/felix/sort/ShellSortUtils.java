package com.felix.sort;

import java.util.Arrays;

public class ShellSortUtils {

    public static void main(String[] args) {
        int[] arr = {2, 5, 1, 4, 9, 0, 7, 6, 3, 8};
        System.out.println(Arrays.toString(arr));
        shellSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 2, 5, 1, 4, 9, 0, 7, 6, 3, 8
     *
     * @param arr
     */
    public static void shellSort(int[] arr) {
        for (int d = arr.length / 2; d > 0; d /= 2) {
            //遍历所有元素
            for (int i = 0; i < arr.length; i++) {
                //遍历本组中所有元素
                for (int j = i; j >= d && arr[j - d] > arr[j]; j -= d) {
                    SortUtils.swap(arr, j, j - d);
                }
            }
        }
    }
}
