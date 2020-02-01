public class LinkedListDeque<T> implements Deque<T> {

    private static class TListNode<T> {
        private T item;
        private TListNode next;
        private TListNode prev;

        public TListNode(T item, TListNode next, TListNode prev) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }

    }

    private TListNode sentinel;
    private int noOfItems;
    private TListNode recursiveHelper = null;

    /** Creates an empty LinkedListDeque. */
    public LinkedListDeque() {
        sentinel = new TListNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        recursiveHelper = sentinel;
        noOfItems = 0;
    }

    /** Adds an item of type T to the front of the deque. */
    public void addFirst(T item) {
        sentinel.next = new TListNode(item, sentinel.next, sentinel);
        sentinel.next.next.prev = sentinel.next;
        noOfItems += 1;
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        sentinel.prev = new TListNode(item, sentinel, sentinel.prev);
        sentinel.prev.prev.next = sentinel.prev;
        noOfItems += 1;
    }

    /** Returns the number of items in the deque. */
    public int size() {
        return noOfItems;
    }

    /** Prints the items in the deque from first to last, separated by a space.
    Once all the items have been printed, print out a new line. */
    public void printDeque() {
        if (size() == 0) {
            return;
        }
        TListNode p = sentinel.next;
        String result = "";

        while (p != sentinel) {
            result += p.item + " ";
            p = p.next;
        }

        System.out.println(result.trim());
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque.
     * If no such item exists, returns null. */
    public T removeFirst() {
        if (noOfItems == 0) {
            return null;
        }
        TListNode removedNode = sentinel.next;
        TListNode newNextNode = sentinel.next.next;
        sentinel.next = newNextNode;
        newNextNode.prev = sentinel;
        noOfItems -= 1;
        return (T) removedNode.item;
    }

    /** Removes and returns the item at the back of the deque.
     * If no such item exists, returns null. */
    public T removeLast() {
        if (noOfItems == 0) {
            return null;
        }
        TListNode removedNode = sentinel.prev;
        TListNode newPrevNode = sentinel.prev.prev;
        sentinel.prev = newPrevNode;
        newPrevNode.next = sentinel;
        noOfItems -= 1;
        return (T) removedNode.item;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque! */
    public T get(int index) {
        if (noOfItems == 0) {
            return null;
        }
        TListNode p = sentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return (T) p.item;
    }

    /** Same as get, but uses recursion. */
    public T getRecursive(int index) {
        if (index == 0 || index == -1) {
            TListNode finalNode = recursiveHelper.next;
            recursiveHelper = sentinel;
            return (T) finalNode.item;
        }
        recursiveHelper = recursiveHelper.next;

        return getRecursive(index - 1);
    }

}
