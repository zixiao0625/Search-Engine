package datastructures.sorting;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import misc.BaseTest;
import misc.exceptions.EmptyContainerException;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());
    }
    
    @Test(timeout = SECOND)
    public void testBasicInput() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 1; i <= 10; i++) {
            heap.insert(i);
        }
        assertEquals(1, heap.peekMin());
        heap.insert(0);
        assertEquals(0, heap.peekMin());
        assertEquals(11, heap.size());
    }
    
    @Test(timeout = SECOND)
    public void testExceptions() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.insert(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // GOOD
        }
        
        try {
            heap.removeMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException e) {
            // GOOD
        }
        
        try {
            heap.peekMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException e) {
            // GOOD
        }
        
        try {
            heap.insert(0);
            heap.removeMin();
            heap.removeMin();
            fail("Expected EmptyContainerException");
        }  catch (EmptyContainerException e) {
            // GOOD
        }
        try {
            for (int i = 0; i <= 10; i++) {
                heap.insert(i);
            }
            for (int i = 0; i <= 10; i++) {
                heap.removeMin();
            }
            heap.removeMin();
        } catch (EmptyContainerException e) {
            // GOOD
        }
        assertEquals(0, heap.size());
    }
    
    @Test (timeout = SECOND)
    public void testEdgeCase() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 19; i++) {
            heap.insert(i);
            assertEquals(i + 1, heap.size());
        }
        assertEquals(0, heap.peekMin());
        heap.insert(-1);
        assertEquals(-1, heap.peekMin());
        heap.removeMin();
        assertEquals(0, heap.removeMin());
        assertEquals(1, heap.peekMin());
        heap.insert(0);
        heap.insert(-9);
        assertEquals(-9, heap.peekMin());
        heap.insert(666);
        assertEquals(21, heap.size());
    }
    
    @Test (timeout = SECOND)
    public void testOneElement() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(666);
        assertEquals(666, heap.removeMin());
        assertEquals(0, heap.size());
        try {
            heap.removeMin();
        } catch (EmptyContainerException e) {
            // GOOD
        }
    }
    
    @Test (timeout = SECOND)
    public void testRanking() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        int[] answer = new int[] {0, 1, 2, 4, 5, 8, 9};
        int[] result = new int[7];
        heap.insert(5);
        heap.insert(8);
        heap.insert(2);
        heap.insert(4);
        heap.insert(0);
        heap.insert(1);
        heap.insert(9);
        int j = 0;
        while (heap.size() != 0) {
            result[j] = heap.removeMin();
            j++;
        }
        for (int i = 0; i < 7; i++) {
            assertEquals(result[i], answer[i]);
        }
    }
    
    @Test (timeout = SECOND)
    public void testDuplicateInput() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(2);
        heap.insert(3);
        heap.insert(4);
        heap.insert(2);
        heap.removeMin();
        assertEquals(2, heap.removeMin());
        heap.removeMin();
        heap.removeMin();
        assertEquals(0, heap.size());
        for (int i = 0; i < 10; i++) {
            heap.insert(6);
        }
        heap.insert(8);
        heap.insert(9);
        heap.insert(0);
        for (int i = 0; i < 11; i++) {
            if (i == 0) {
                assertEquals(0, heap.removeMin());
            } else {
                assertEquals(6, heap.removeMin());
            }
        }
        assertEquals(8, heap.peekMin());
    }
    
    @Test (timeout = SECOND)
    public void testSmallestChild() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(-9);
        heap.insert(1);
        heap.insert(2);
        heap.insert(0);
        heap.insert(3);
        heap.insert(-8);
        heap.removeMin();
        assertEquals(-8, heap.removeMin());
        assertEquals(0, heap.removeMin());
        assertEquals(1, heap.removeMin());
    }
    
    @Test (timeout = SECOND) 
    public void testComparable() {
        String result = "";
        IPriorityQueue<String> heap = this.makeInstance();
        heap.insert("g");
        heap.insert("f");
        heap.insert("e");
        heap.insert("d");
        heap.insert("c");
        heap.insert("b");
        heap.insert("a");
        while (!heap.isEmpty()) {
            result += heap.removeMin();
        }
        assertEquals("abcdefg", result);
    }
    
    @Test (timeout = SECOND)
    public void testAllNegative() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 10; i >= 0; i--) {
            heap.insert(-1 * i);
            assertEquals(-10, heap.peekMin());
        }
        assertEquals(11, heap.size());
        heap.removeMin();
        assertEquals(-9, heap.peekMin());
    }
}
