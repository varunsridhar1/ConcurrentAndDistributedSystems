//UT-EID= vks377, rsr873


import java.util.*;
import java.util.concurrent.*;


public class PMerge implements Callable<int[]>{
    final int[] A;
    final int[] B;
    int[] C;
    int start, end;
    boolean arrayA;
    static Set<Integer> numbers = new HashSet<Integer>();

    public PMerge(int[] A, int[] B, int[] C, int start, int end, boolean arrayA) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.start = start;
        this.end = end;
        this.arrayA = arrayA;
    }

    public static void parallelMerge(int[] A, int[] B, int[] C, int numThreads){
        // TODO: Implement your parallel merge function
        try {
            ExecutorService es = Executors.newFixedThreadPool(numThreads);
            int elemPerTask = (int) Math.ceil((double)C.length / (double)numThreads);
            for(int i = 0; i <= A.length; i+=elemPerTask) {
                PMerge p;
                if(i + (elemPerTask - 1) > A.length - 1)
                    p = new PMerge(A, B, C, i, A.length - 1, true);
                else
                    p = new PMerge(A, B, C, i, i + (elemPerTask - 1), true);
                Future<int[]> f = es.submit(p);
                f.get();
            }
            for(int i = 0; i <= B.length; i+=elemPerTask) {
                PMerge p;
                if(i + (elemPerTask - 1) > B.length - 1)
                    p = new PMerge(A, B, C, i, B.length - 1, false);
                else
                    p = new PMerge(A, B, C, i, i+(elemPerTask - 1), false);
                Future<int[]> f = es.submit(p);
                f.get();
            }
            //System.out.println("Answer is " + Arrays.toString(C));
            numbers.clear();
            es.shutdown ();
        } catch (Exception e) { System.err.println (e); }
    }

    public int[] call() {
        if(arrayA) {
            for(int i = start; i <= end; i++) {
                int BIndex = binarySearch(B, A[i]);
                C[i + BIndex] = A[i];
            }
        }
        else {
            for(int i = start; i <= end; i++) {
                int AIndex = binarySearch(A, B[i]);
                C[i + AIndex] = B[i];
            }
        }
        return C;
    }

    private int binarySearch(int[] array, int num) {
        int start = 0;
        int end = array.length - 1;
        while(start <= end) {
            int mid = start + (end - start)/2;
            if(array[mid] == num) {
                if(numbers.contains(num))
                    return mid + 1;
                numbers.add(num);
                return mid;
            }
            else if(num < array[mid])
                end = mid - 1;
            else
                start = mid + 1;
        }
        numbers.add(num);
        return start;
        // add indexA and start/end (always choose start)
    }
}
