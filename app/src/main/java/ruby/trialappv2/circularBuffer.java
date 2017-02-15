package ruby.trialappv2;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;


/**
 * Created by ruby__000 on 10/02/2017.
 */

public class circularBuffer<T> {

    private T[] buffer;

    private int tail;

    private int head;


    public circularBuffer(int n) {
        buffer = (T[]) new Object[n];
        tail = 0;
        head = 0;
    }

    public void add(T toAdd) {
        if (head != (tail - 1)) {
            buffer[head++] = toAdd;
        } else {
            throw new BufferOverflowException();
        }
        head = head % buffer.length;
    }

    public T get() {
        T t = null;
        int adjTail = tail > head ? tail - buffer.length : tail;
        if (adjTail < head) {
            t = (T) buffer[tail++];
            tail = tail % buffer.length;
        } else {
            throw new BufferUnderflowException();
        }
        return t;
    }

    public String toString() {
        return "CustomCircularBuffer(size=" + buffer.length + ", head=" + head + ", tail=" + tail + ")";
    }
}

