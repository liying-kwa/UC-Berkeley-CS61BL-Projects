import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void yourTestHere() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(0);
        ad1.addFirst(1);
        ad1.addLast(2);
        ad1.addFirst(3);
        ad1.removeLast();     //  ==> 2
        ad1.addFirst(5);
        ad1.addLast(6);
        ad1.get(0);     // ==> 5
        ad1.addLast(8);
        ad1.addLast(9);
        ad1.addFirst(10);
        ad1.addLast(11);
        ad1.removeLast();    //  ==> 11
        ad1.get(0);     // ==> 10
        ad1.addLast(14);
        ad1.addLast(15);
        ad1.addLast(16);
        ad1.addLast(17);
        ad1.addFirst(18);
        ad1.addFirst(19);
        ad1.addLast(20);
        ad1.addFirst(21);
        ad1.addLast(22);
        ad1.printDeque();
        int a = ad1.removeLast();
        System.out.println(a);
    }
}
