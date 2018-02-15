import java.lang.Math;
public class PriQueueTest implements Runnable{
    static PriorityQueue q = new PriorityQueue(3);
    String[] names = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};
    public static void main(String[] args){
        PriQueueTest t1 = new PriQueueTest();
        PriQueueTest t2 = new PriQueueTest();
        PriQueueTest t3 = new PriQueueTest();
        PriQueueTest t4 = new PriQueueTest();
        PriQueueTest t5 = new PriQueueTest();
        PriQueueTest t6 = new PriQueueTest();
        PriQueueTest t7 = new PriQueueTest();
        PriQueueTest t8 = new PriQueueTest();
        PriQueueTest t9 = new PriQueueTest();
        PriQueueTest t10 = new PriQueueTest();
        PriQueueTest t11 = new PriQueueTest();




        t1.run();
        t2.run();
        t3.run();
        t4.run();
        t5.run();
        t6.run();
        t7.run();
        t8.run();
        t9.run();
        t10.run();
        t11.run();

        q.print();
        System.out.println("First " + q.getFirst());
        q.print();
        System.out.println("Search: "+ q.search("a"));
    }

    @Override
    public void run() {
        if(q.size == q.maxSize ){
            System.out.println("Removing.");
            q.getFirst();
        }
        else{
            System.out.println("Adding.");
            q.add(names[(int)(Math.random()*10)], (int)(Math.random()*10));

        }
    }
}
