public class testReadWrite {
    final ThreadSynch gate;

    public testReadWrite(ThreadSynch gate) {
        this.gate = gate;
    }


    public static void main(String[] args) {
        Thread[] readers = new Thread[10];
        Thread[] writers = new Thread[10];
        FairReadWriteLock rwl = new FairReadWriteLock();

//        for(int i = 0; i < readers.length; i++) { //Option to create readers separately from writers
//            readers[i] = new Thread(new Reader());
//            readers[i].start();
//        }
//
//        for(int i = 0; i < writers.length; i++) {
//            writers[i] = new Thread(new Writer());
//            writers[i].start();
//        }
        for(int i = 0; i < readers.length; i++) { //Option to create readers and writers approximately at the same time
            readers[i] = new Thread(new Reader(rwl));
            writers[i] = new Thread(new Writer(rwl));
            readers[i].start();
            writers[i].start();
        }
    }
}

class Reader implements Runnable {
    FairReadWriteLock rwl;

    public Reader(FairReadWriteLock rwl) {
        this.rwl = rwl;
    }
    public void run() {
        //System.out.println("Beginning read on Thread " + Thread.currentThread().getId());
        rwl.beginRead();
        rwl.endRead();
        //System.out.println("Ending read on Thread " + Thread.currentThread().getId());

    }
}
class Writer implements Runnable {
    FairReadWriteLock rwl;

    public Writer(FairReadWriteLock rwl) {
        this.rwl = rwl;
    }

    public void run() {
        //System.out.println("Beginning write on Thread " + Thread.currentThread().getId());
        rwl.beginWrite();
        rwl.endWrite();
        //System.out.println("Ending write on Thread " + Thread.currentThread().getId());

    }
}