package datastructures.sorting;

import misc.BaseTest;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.Searcher;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestTopKSortFunctionality extends BaseTest {
    
    protected <T> List<T> convertList(IList<T> list) {
        List<T> nList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            nList.add(list.get(i));
        }
        return nList;
    }
    
    @Test (timeout = SECOND)
    public void testAllNegative() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(-1 * i);
        }
        IList<Integer> top = Searcher.topKSort(5, list);
        assertEquals(5, top.size());
        for (Integer i : top) {
            assertEquals(true, i >= -5);
        }
    }
    
    @Test (timeout = SECOND)
    public void testRandomList() {
        int k = 13;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 30; i++) {
            if (i % k >= 2) {
                list.add(-1 * i);
            } else {
                list.add(i + 3);
            }
        }
        List<Integer> cList = convertList(list);
        list = Searcher.topKSort(k, list);
        assertEquals(k, list.size());
        Collections.sort(cList);
        Collections.reverse(cList);
        for (int i = 0; i < k; i++) {
            assertEquals(list.get(i), cList.get(k - 1 - i));
        }
    }
    
    @Test (timeout = SECOND)
    public void testExceptions() {
        int k = -5;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        try {
            list = Searcher.topKSort(k, list);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // GOOD
        }
    }
    
    @Test (timeout = SECOND)
    public void testGreatK() {
        int k = 5000;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        list = Searcher.topKSort(k, list);
        assertEquals(20, list.size());
        assertEquals(0, list.get(0));
        assertEquals(19, list.get(19));
        k = 0;
        list = Searcher.topKSort(k, list);
        assertEquals(0, list.size());
    }
    
    @Test (timeout = SECOND)
    public void testSameElements() {
        int k = 10;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(6);
        }
        list.add(0);
        list.add(999);
        list = Searcher.topKSort(k, list);
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                assertEquals(999, list.get(i));
            } else {
                assertEquals(6, list.get(i));
            }
        }
    }
    
    @Test (timeout = SECOND)
    public void testModifyOrig() {
        int k = 3;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        IList<Integer> nList = Searcher.topKSort(k, list);
        assertEquals(20, list.size());
        assertEquals(3, nList.size());
        for (int i = 0; i < nList.size(); i++) {
            assertEquals(nList.get(nList.size() - 1 - i), list.get(list.size() -1 - i));
        }
    }
}
