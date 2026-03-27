package homework1.src;

import java.util.Arrays;
import java.util.Random;

public class MergeSort {
    public static void main(String[] args) {
        int[] input = new int[1000];
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            input[i] = rand.nextInt(5000);
        }


        int[] result = mergeSort(input);
        // System.out.println(Arrays.toString(result));
    }


    public static int[] mergeSort(int[] input) {
        if (input == null || input.length <= 1) {
            return input;
        }
        int mid = input.length / 2;

        int[] left = new int[mid];
        int[] right = new int[input.length - mid];

        for (int i = 0; i < mid; i++) {
            left[i] = input[i];
        }

        for (int i = mid; i < input.length; i++) {
            right[i-mid] = input[i];
        }
        left = mergeSort(left);
        right = mergeSort(right);

        return merge(input, left, right);
    }

    public static int[] merge(int[] input, int[] left, int[] right) {
        int lp = 0;
        int rp = 0;
        int ip = 0;

        //sort between arrays
        while (lp < left.length && rp < right.length) {
            if (left[lp] < right[rp]) {
                input[ip] = left[lp];
                ip++;
                lp++;
            } else {
                input[ip] = right[rp];
                ip++;
                rp++;
            }
        }
        //if right array is empty
        while (lp < left.length) {
            input[ip] = left[lp];
            ip++;
            lp++;
        }
        //if left array is empty
        while (rp < right.length) {
            input[ip] = right[rp];
            ip++;
            rp++;
        }
        return input;
    }
}
