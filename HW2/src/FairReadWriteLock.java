public class FairReadWriteLock {
    private int readers, writers = 0;
    private int nextTurn = 0;
    private int count = 0;

    public synchronized void beginRead() {
        int curRead = count;
        count++;
        while(writers > 0 || curRead != nextTurn) {     //block reader while there are writers writing or waiting to write
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readers++;                                  // unblock the reader
        nextTurn++;
    }

    public synchronized void endRead() {
        readers--;                                  // remove the reader since it's done
        notifyAll();
    }

    public synchronized void beginWrite() {
        int curWrite = count;
        count++;
        while(writers > 0 || readers > 0 || curWrite != nextTurn) {     // block writer if there are writers writing or readers reading
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        writers++;                                  // unblock
        nextTurn++;
    }
    public synchronized void endWrite() {
    }
}