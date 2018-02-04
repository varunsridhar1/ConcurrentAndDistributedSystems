//UT-EID=


import java.util.*;
import java.util.concurrent.*;


public class PMerge implements Callable<int[]>{
	final int[] A;
	final int[] B;
	int[] C;
	int indexA, indexB;
	
	public PMerge(int[] A, int[] B, int[] C, int indexA, int indexB) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.indexA = indexA;
		this.indexB = indexB;
	}
	
	public static void parallelMerge(int[] A, int[] B, int[] C, int numThreads){
		// TODO: Implement your parallel merge function
		try {
			ExecutorService es = Executors.newFixedThreadPool(numThreads);
			for(int i = 0; i < A.length; i++) {
				PMerge p = new PMerge(A, B, C, i, -1);
				Future<int[]> f = es.submit(p);
				f.get();
			}
			for(int i = 0; i < B.length; i++) {
				PMerge p = new PMerge(A, B, C, -1, i);
				Future<int[]> f = es.submit(p);
				f.get();
			}
			//System.out.println("Answer is " + Arrays.toString(C));
			es.shutdown ();
		} catch (Exception e) { System.err.println (e); }
	}
	
	public int[] call() {
		if(indexA == -1) {
			int AIndex = binarySearch(A, B[indexB]);
			C[AIndex + indexB] = B[indexB];
		}
		else {
			int BIndex = binarySearch(B, A[indexA]);
			C[indexA + BIndex] = A[indexA];
		}
		return C;
	}
	
	private int binarySearch(int[] array, int num) {
		int start = 0;
		int end = array.length - 1;
		while(start < end) {					
			int mid = start + (end - start)/2;
			if(array[mid] == num) 
				return mid;
			else if(num < array[mid]) 
				end = mid - 1;
			else
				start = mid + 1;
		}
		return start;
		// add indexA and start/end (always choose start)
	}
}
