
/*
 * EID's of group members
 *
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class ThreadSynch {

    private static int numThreads = 0;
    private int parties;
   // private final Semaphore mutex = new Semaphore();

    public ThreadSynch(int parties) {
        // Creates a new ThreadSynch that will release threads only when
        // the given number of threads are waiting upon it
        this.parties = parties;
    }

    public int await() throws InterruptedException {
        // Waits until all parties have invoked await on this ThreadSynch.
        // If the current thread is not the last to arrive then it is
        // disabled for thread scheduling purposes and lies dormant until
        // the last thread arrives.
        // Returns: the arrival index of the current thread, where index
        // (parties - 1) indicates the first to arrive and zero indicates
        // the last to arrive.
        int index = 0;

        // you need to write this code
        return index;
    }
}