package Ch2;

import org.junit.Assert;
import org.junit.Test;


/**
 * Created by LumiG on 11/9/16.
 */
public class ConcurrentDoubleLinkedListTest {

    @Test
    public void testInit() throws Exception {
        ConcurrentDoubleLinkedList list = new ConcurrentDoubleLinkedList();
        Assert.assertTrue(list.head instanceof ConcurrentDoubleLinkedList.Node);
    }

    @Test
    public void testSingleThreadInsert() throws Exception {
        ConcurrentDoubleLinkedList list = new ConcurrentDoubleLinkedList();
        list.insert(1);
        Assert.assertEquals(list.head.next.value, 1);
        list.insert(4);
        Assert.assertEquals(list.head.next.value, 1);
        Assert.assertEquals(list.head.next.next.value, 4);
        list.insert(3);
        Assert.assertEquals(list.head.next.value, 1);
        Assert.assertEquals(list.head.next.next.value, 3);
        Assert.assertEquals(list.head.next.next.next.value, 4);
    }

    @Test
    public void testConcurrentInsert() throws Exception {
        ConcurrentDoubleLinkedList list = new ConcurrentDoubleLinkedList();
        Thread t1 = new insertThread(list, 1, 3, 7),
                t2 = new insertThread(list, 2, 9, 3),
                t3 = new insertThread(list, 6, 4, 5);
        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        verifyList(list, new int[] {1, 2, 3, 3, 4, 5, 6, 7, 9});

    }

    private void verifyList(ConcurrentDoubleLinkedList list,int[] expected) {
        ConcurrentDoubleLinkedList.Node p = list.head.next;
        for (int value: expected) {
            Assert.assertTrue(p != list.tail && p.value == value);
            p = p.next;
        }
        Assert.assertEquals(p, list.tail);
    }

    private class insertThread extends Thread {
        public final int[] values;
        public final ConcurrentDoubleLinkedList list;

        public insertThread(ConcurrentDoubleLinkedList list, int... value) {
            this.values = value;
            this.list = list;
        }

        @Override
        public void run() {
            for (int value: values) {
                list.insert(value);
            }
        }
    }

}