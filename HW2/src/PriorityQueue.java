import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class PriorityQueue {
    int maxSize;
    int size;
    private Node head;

    final ReentrantLock sizeLock = new ReentrantLock();
    final ReentrantLock fullLock = new ReentrantLock();
    final ReentrantLock emptyLock = new ReentrantLock();

    final Condition full = fullLock.newCondition();
    final Condition empty = emptyLock.newCondition();

    public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
        this.maxSize = maxSize;
        size = 0;
        head = null;
    }

    public int add(String name, int priority) {
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
        Node node = new Node(name, priority);
        if(head == null) {
            head = node;
            head.next = null;
            sizeLock.lock();
            size++;
            sizeLock.unlock();
            return 0;                       // added at the head
        }

        Node cur = head;
        cur.lock.lock();

        if(duplicate(name)) {               // if name is already present in the list
            cur.lock.unlock();
            return -1;
        }

        int index = 0;
        if(priority >= head.getPriority()) {     // if new node has greater priority than the head
            while(size == maxSize) {            // block if priority queue is full
                fullLock.lock();
                try {
                    full.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fullLock.unlock();
            }

            node.next = head;
            head.lock.unlock();
            head = node;

            sizeLock.lock();
            size++;
            sizeLock.unlock();

            return 0;                       // added at the head
        }
        else {
            while(cur.next != null && cur.getPriority() >= priority && cur.next.getPriority() >= priority) {
                while (size == maxSize) {       // block if priority queue is full
                    fullLock.lock();
                    try {
                        full.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    fullLock.unlock();
                }

                cur.lock.lock();
                cur.next.lock.lock();
                index++;
                cur.lock.unlock();
                cur.next.lock.unlock();
                cur = cur.next;
            }
            // found right place to add
            node.next = cur.next;
            cur.next = node;

            sizeLock.lock();
            size++;
            sizeLock.unlock();

            // signal that the priority queue is not empty
            emptyLock.lock();
            empty.signal();
            emptyLock.unlock();

            cur.lock.unlock();
        }

        return index;
    }

    public int search(String name) {
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.

        Node node = head;
        head.lock.lock();
        int index = 0;

        while(node.next != null) {
            node.lock.lock();
            node.next.lock.lock();

            if(node.getName().equals(name)) {
                node.lock.unlock();
                return index;
            }
            node.lock.unlock();
            node = node.next;
            index++;
        }

        if(node.getName().equals(name))
            return index;

        return -1;
    }

    public String getFirst() {
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
        String result = "";
        head.lock.lock();
        while(head == null) {
            emptyLock.lock();
            try {
                empty.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            emptyLock.unlock();
        }


        result = head.getName();
        head.lock.unlock();
        head = head.next;

        sizeLock.lock();
        size--;
        sizeLock.unlock();

        fullLock.lock();                    // signal because priority queue is not full anymore
        full.signal();
        fullLock.unlock();

        return result;
    }

    private boolean duplicate(String name) {
        Node node = head;
        while(node.next!= null) {
            node.lock.lock();
            node.next.lock.lock();

            if(node.getName().equals(name)) {
                node.lock.unlock();
                return true;
            }
            node.lock.unlock();
            node = node.next;
        }
        if(node.getName().equals(name))
            return true;
        return false;
    }

    public void print() {
        Node current = head;
        head.lock.lock();
        while(current.next != null){
            System.out.println("name: " + current.getName() + " priority: " + current.getPriority());
            current = current.next;

        }
        System.out.println("name: " + current.getName() + " priority: " + current.getPriority());
    }
}

class Node {
    String name;
    int priority;           //0 - 9
    Node next;
    ReentrantLock lock;

    public Node(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.next = null;
        this.lock = new ReentrantLock();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

}