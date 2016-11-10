package Ch2;

import java.util.concurrent.locks.ReentrantLock;

/**
 * A concurrent ordered double linked list supporting inserting
 */
public class ConcurrentDoubleLinkedList {

    public static class Node {
        public final int value;
        public Node prev, next;
        private final ReentrantLock lock = new ReentrantLock();

        public Node() {
            this.value = 0;
        }

        public Node(int value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    public final Node head, tail;
    public ConcurrentDoubleLinkedList() {
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.prev = head;
    }

    public void insert(int val) {
        Node prev = head;
        // hold first node
        prev.lock.lock();
        Node cur = head.next;
        try {
            while (true) {
                // hold second node
                cur.lock.lock();
                try {
                    if (cur == tail || cur.value > val) {
                        // Insert
                        Node node = new Node(val, prev, cur);
                        prev.next = node;
                        cur.prev = node;
                        return;
                    }
                } finally {
                    // Release first lock
                    prev.lock.unlock();
                }
                prev = cur;
                cur = cur.next;
            }
        } finally {
            // release first lock
            cur.lock.unlock();
        }
    }

}
