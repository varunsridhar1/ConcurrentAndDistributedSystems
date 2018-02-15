public class FairReadWriteLock {
    private int readers, writers = 0;
    private int nextTurn = 0;
    private int count = 0;

    public synchronized void beginRead() {
        System.out.println("READER BEGIN: " + Thread.currentThread().getId());
        int curRead = count;
        count++;
        while(writers > 0 || curRead != nextTurn) {     //block reader while there are writers writing or waiting to write
            try {
                System.out.println("READER BLOCKED: " + Thread.currentThread().getId());
                wait();
                System.out.println("READER UNBLOCKED: " + Thread.currentThread().getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readers++;                                  // unblock the reader

    }

    public synchronized void endRead() {
        System.out.println("READER LEAVES: " +  Thread.currentThread().getId());
        nextTurn++;
        readers--;                                  // remove the reader since it's done
        notifyAll();
    }

    public synchronized void beginWrite() {
        System.out.println("WRITER BEGIN: " + Thread.currentThread().getId());
        int curWrite = count;
        count++;
        while(writers > 0 || readers > 0 || curWrite != nextTurn) {     // block writer if there are writers writing or readers reading
            try {
                System.out.println("WRITER BLOCKED: " +  Thread.currentThread().getId());
                wait();
                System.out.println("WRITER UNBLOCKED: " +  Thread.currentThread().getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        writers++;                                  // unblock
    }
    public synchronized void endWrite() {
        System.out.println("WRITER LEAVES: " +  Thread.currentThread().getId());
        nextTurn++;
        writers--;                                // remove the writer since it's done
        notifyAll();
    }
}

