package bearmaps.utils.pq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

/* A MinHeap class of Comparable elements backed by an ArrayList. */
public class MinHeap<E extends Comparable<E>> {

    /* An ArrayList that stores the elements in this MinHeap. */
    private ArrayList<E> contents;
    private HashMap<E, Integer> exists;
    private int size = 0;

    /* Initializes an empty MinHeap. */
    public MinHeap() {
        contents = new ArrayList<>();
        contents.add(null);
        exists = new HashMap<E, Integer>();
    }

    /* Returns the element at index INDEX, and null if it is out of bounds. */
    private E getElement(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.get(index);
        }
    }

    /* Sets the element at index INDEX to ELEMENT. If the ArrayList is not big
       enough, add elements until it is the right size. */
    private void setElement(int index, E element) {
        while (index >= contents.size()) {
            contents.add(null);
        }
        contents.set(index, element);
    }

    /* Swaps the elements at the two indices. */
    private void swap(int index1, int index2) {
        E element1 = getElement(index1);
        E element2 = getElement(index2);
        setElement(index2, element1);
        setElement(index1, element2);

        exists.put(element1, index2);
        exists.put(element2, index1);
    }

    /* Prints out the underlying heap sideways. Use for debugging. */
    @Override
    public String toString() {
        return toStringHelper(1, "");
    }

    /* Recursive helper method for toString. */
    private String toStringHelper(int index, String soFar) {
        if (getElement(index) == null) {
            return "";
        } else {
            String toReturn = "";
            int rightChild = getRightOf(index);
            toReturn += toStringHelper(rightChild, "        " + soFar);
            if (getElement(rightChild) != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + getElement(index) + "\n";
            int leftChild = getLeftOf(index);
            if (getElement(leftChild) != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += toStringHelper(leftChild, "        " + soFar);
            return toReturn;
        }
    }

    /* Returns the index of the left child of the element at index INDEX. */
    private int getLeftOf(int index) {
        int nIndex = 2 * index;
//        if (nIndex >= contents.size() || getElement(nIndex) == null) {
//            return -1;
//        }
        return nIndex;
    }

    /* Returns the index of the right child of the element at index INDEX. */
    private int getRightOf(int index) {
        int nIndex = 2 * index + 1;
//        if (nIndex >= contents.size() || getElement(nIndex) == null) {
//            return -1;
//        }
        return nIndex;
    }

    /* Returns the index of the parent of the element at index INDEX. */
    private int getParentOf(int index) {
        if (index == 1) {
            return -1;
        }
        return index / 2;
    }

    /* Returns the index of the smaller element. At least one index has a
       non-null element. If the elements are equal, return either index. */
    private int min(int index1, int index2) {
        E e1, e2;
        if (index1 == -1) {
            e1 = null;
        } else {
            e1 = getElement(index1);
        }
        if (index2 == -1) {
            e2 = null;
        } else {
            e2 = getElement(index2);
        }
        if (e1 == null) {
            return index2;
        } else if (e2 == null) {
            return index1;
        } else {
            if (e1.compareTo(e2) < 0) {
                return index1;
            } else {
                return index2;
            }
        }
    }

    /* Returns but does not remove the smallest element in the MinHeap. */
    public E findMin() {
        if (size > 0) {
            return getElement(1);
        }
        return null;
    }

    /* Bubbles up the element currently at index INDEX. */
    private void bubbleUp(int index) {
        int parentIndex = getParentOf(index);
        while (min(parentIndex, index) == index && index != 1 && parentIndex != -1) {
            swap(parentIndex, index);
            index = parentIndex;
            parentIndex = getParentOf(index);
        }
    }

    /* Bubbles down the element currently at index INDEX. */
    private void bubbleDown(int index) {
        int indexLeft = getLeftOf(index);
        int indexRight = getRightOf(index);
        while (getElement(indexLeft) != null || getElement(indexRight) != null) {
            if (getElement(indexLeft) != null && getElement(indexRight) != null) {
                if (min(indexLeft, index) == indexLeft && min(indexLeft, indexRight) == indexLeft) {
                    swap(indexLeft, index);
                    index = indexLeft;
                    indexLeft = getLeftOf(index);
                    indexRight = getRightOf(index);
                } else if (min(indexRight, index) == indexRight
                        && min(indexLeft, indexRight) == indexRight) {
                    swap(indexRight, index);
                    index = indexRight;
                    indexLeft = getLeftOf(index);
                    indexRight = getRightOf(index);
                } else if (min(indexLeft, index) == index && min(indexRight, index) == indexRight) {
                    swap(indexRight, index);
                    index = indexRight;
                    indexLeft = getLeftOf(index);
                    indexRight = getRightOf(index);
                } else {
                    break;
                }
            } else if (getElement(indexLeft) != null) {
                if (min(indexLeft, index) == indexLeft) {
                    swap(indexLeft, index);
                    index = indexLeft;
                    indexLeft = getLeftOf(index);
                    indexRight = getRightOf(index);
                } else {
                    break;
                }
            }
        }
    }

    /* Returns the number of elements in the MinHeap. */
    public int size() {
        return size;
    }

    /* Inserts ELEMENT into the MinHeap. If ELEMENT is already in the MinHeap,
       throw an IllegalArgumentException.*/
    public void insert(E element) {
        if (!contains(element)) {
            size++;
            setElement(size, element);
            exists.put(element, size);
            bubbleUp(size);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /* Returns and removes the smallest element in the MinHeap. */
    public E removeMin() {
        // swap with bottom rightmost element
        swap(1, size);
        E retE = getElement(size);
        setElement(size, null);
        bubbleDown(1);
        exists.remove(retE);
        size--;
        return retE;
    }

    /* Replaces and updates the position of ELEMENT inside the MinHeap, which
       may have been mutated since the initial insert. If a copy of ELEMENT does
       not exist in the MinHeap, throw a NoSuchElementException. Item equality
       should be checked using .equals(), not ==. */
    public void update(E element) {
        if (contains(element)) {
            int index = exists.get(element);
            setElement(index, element);
            bubbleUp(index);
            bubbleDown(index);
        } else {
            throw new NoSuchElementException();
        }
    }

    /* Returns true if ELEMENT is contained in the MinHeap. Item equality should
       be checked using .equals(), not ==. */
    public boolean contains(E element) {
        return exists.containsKey(element);
    }
}
