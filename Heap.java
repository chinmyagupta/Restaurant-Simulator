import java.util.*;


class HeapNode {
    int value;
    int idx;

    HeapNode(int key, int i) {
        this.value = key;
        this.idx = i;
    }
}



public class Heap {
    public Vector<HeapNode> heap_nodes = new Vector<>();

    public int left_child(int idx) {
        return 2 * idx + 1;
    }

    public int right_child(int idx) {
        return (2 * idx + 2);
    }

    public int parent(int idx) {
        return Math.max((idx - 1) / 2, 0);
    }

    public void swap(int a, int b) {
        HeapNode tmp = heap_nodes.get(a);
        heap_nodes.setElementAt(heap_nodes.get(b), a);
        heap_nodes.setElementAt(tmp, b);
    }

    public void above_heap(int x) {
        if (x > 0 && heap_nodes.get(parent(x)).value > heap_nodes.get(x).value) {
            swap(parent(x), x);
            above_heap(parent(x));
        }
    }
    public void below_heap(int x) {
        int smallest = x;
        if (left_child(x) < heap_nodes.size() && heap_nodes.get(left_child(x)).value < heap_nodes.get(x).value)
            smallest = left_child(x);

        if (right_child(x) < heap_nodes.size() && heap_nodes.get(right_child(x)).value < heap_nodes.get(smallest).value)
            smallest = right_child(x);

        if (smallest != x) {
            swap(smallest, x);
            below_heap(smallest);
        }
    }

    public HeapNode peek() {
        if (heap_nodes.size() == 0) return null;
        return heap_nodes.firstElement();
    }

    public void add(Integer val, int i) {
        heap_nodes.addElement(new HeapNode(val, i));
        int o = heap_nodes.size() - 1;
        above_heap(o);
    }

    public void poll() {
        if (heap_nodes.size() == 0) return;
        HeapNode last = heap_nodes.lastElement();
        heap_nodes.setElementAt(last, 0);
        heap_nodes.remove(this.heap_nodes.size() - 1);
        below_heap(0);
    }
}