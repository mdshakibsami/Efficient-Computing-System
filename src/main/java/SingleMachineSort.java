import java.util.Arrays;

public class SingleMachineSort {
    public void sortAndMeasure() {
        int[] array = generateLargeArray(100000);
        long startTime = System.currentTimeMillis();

        Arrays.sort(array);

        long endTime = System.currentTimeMillis();
        System.out.println("Single Machine Sorting Time: " + (endTime - startTime) + " ms");
    }

    private int[] generateLargeArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = (int) (Math.random() * 10000);
        }
        return array;
    }
}
