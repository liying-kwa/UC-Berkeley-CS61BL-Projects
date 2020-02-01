public class ArrayDeque<T> implements Deque<T> {

    private T[] items;
    private int size;
    private int noOfItems;
    private int frontPointer;
    private int backPointer;

    /**
     * Creates an empty ArrayDeque.
     */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 8;
        noOfItems = 0;
        frontPointer = 4;
        backPointer = 5;
    }


    /**
     * Adds an item of type T to the front of the deque.
     */
    @Override
    public void addFirst(T item) {
        // Array is full
        if (noOfItems >= size) {
            T[] temp = (T[]) new Object[size + 1];
            System.arraycopy(items, 0, temp, 1, size);
            items = temp;
            items[0] = item;
            backPointer += 1;
            size += 1;
            noOfItems += 1;
        } else if (frontPointer != -1) { // There is space at the front
            while (items[frontPointer] != null) {
                frontPointer -= 1;
            }
            items[frontPointer] = item;
            frontPointer -= 1;
            noOfItems += 1;
        } else { // Array is not full but front spaces are filled
            backPointer += 1;
            frontPointer += 1;
            /*int temp = backPointer - 1;
            while (temp != frontPointer) {
                items[temp] = items[temp - 1];
                temp -= 1;
            }*/
            T[] temp = (T[]) new Object[size];
            System.arraycopy(items, 0, temp, 1, noOfItems);
            items = temp;
            items[frontPointer] = item;
            frontPointer -= 1;
            noOfItems += 1;
        }
    }

    /**
     * Adds an item of type T to the back of the deque.
     */
    public void addLast(T item) {
        // Array is full
        if (noOfItems >= size) {
            T[] temp = (T[]) new Object[size + 1];
            System.arraycopy(items, 0, temp, 0, size);
            items = temp;
            items[size] = item;
            backPointer += 1;
            size += 1;
            noOfItems += 1;
        } else if (backPointer != size) { // There is space at the back
            while (items[backPointer] != null) {
                backPointer += 1;
            }
            items[backPointer] = item;
            backPointer += 1;
            noOfItems += 1;
        } else { // Array is not full but back spaces are filled
            backPointer -= 1;
            frontPointer -= 1;
            /*int temp = frontPointer + 1;
            while (temp != backPointer) {
                items[temp] = items[temp + 1];
                temp += 1;
            }*/
            T[] temp = (T[]) new Object[size];
            System.arraycopy(items, frontPointer + 2, temp, frontPointer + 1, noOfItems);
            items = temp;
            items[backPointer] = item;
            backPointer += 1;
            noOfItems += 1;
        }
    }

    /**
     * Returns the number of items in the deque.
     */
    public int size() {
        return noOfItems;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    public void printDeque() {
        if (size() == 0) {
            return;
        }
        String result = "";

        for (int i = 0; i < size; i++) {
            result += items[i] + " ";
        }
        System.out.println(result.trim());
        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     */
    public T removeFirst() {
        if (noOfItems == 0) {
            return null;
        }
        T removedItem = null;
        if (frontPointer == -1) {
            removedItem = items[0];
            T[] temp = (T[]) new Object[size - 1];
            System.arraycopy(items, 1, temp, 0, size - 1);
            items = temp;
            backPointer -= 1;
            size -= 1;
            noOfItems -= 1;
        } else {
            removedItem = items[frontPointer + 1];
            items[frontPointer + 1] = null;
            frontPointer += 1;
            noOfItems -= 1;
        }
        return removedItem;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     */
    public T removeLast() {
        if (noOfItems == 0) {
            return null;
        }
        T removedItem = null;
        if (backPointer == size) {
            removedItem = items[size - 1];
            T[] temp = (T[]) new Object[size - 1];
            System.arraycopy(items, 0, temp, 0, size - 1);
            items = temp;
            backPointer -= 1;
            size -= 1;
            noOfItems -= 1;
        } else {
            removedItem = items[backPointer - 1];
            items[backPointer - 1] = null;
            backPointer -= 1;
            noOfItems -= 1;
        }
        return removedItem;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     */
    public T get(int index) {
        if (noOfItems == 0) {
            return null;
        }
        if (frontPointer != -1) {
            return items[frontPointer + 1 + index];
        }
        return items[index];
    }

}
