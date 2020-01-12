package com.felix.sort;

import java.util.Arrays;

public class QuickSortDemo {

    public static void main(String[] args) {
        int[] arr = {2, 5, 1, 4, 9, 0, 7, 6, 2, 8};
        System.out.println(Arrays.toString(arr));
        quickSort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 2, 5, 1, 8, 4, 0, 7, 6, 2, 9
     * 4
     * 2, 5, 1, 8, 5, 0, 7, 6, 2, 9
     * 2, 2, 1, 8, 5, 0, 7, 6, 2, 9
     *
     * @param arr
     * @param start
     * @param end
     */
    public static void quickSort(int[] arr, int start, int end) {
        if (start < end) {
            int mid = (start + end) / 2;
            int standard = arr[mid];
            int low = start;
            int high = end;
            while (low < high) {
                while (low < high && arr[low] <= standard) {
                    low++;
                }
                arr[high] = arr[low];
                while (low < high && standard <= arr[high]) {
                    high--;
                }
                arr[low] = arr[high];
            }
            arr[low] = standard;
            quickSort(arr, start, low - 1);
            quickSort(arr, low, end);
        }
    }
}
