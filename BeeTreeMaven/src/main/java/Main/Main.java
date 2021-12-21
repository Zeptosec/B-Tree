package Main;

import Util.BTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args){
        BTree<Integer, String> bt = new BTree<>(3);
        String v = "v";
        int[] ints = {13,16,2,7,9,11,19,0,12,8,4,1,3,17,5,14,18,6,10,15};
        for(int i : getInts(50))
            bt.insert(i, v);
        bt.printTreeConsole();
        //bt.printTreeFile("treeText.txt");
    }

    static List<Integer> getInts(int c){
        LinkedList<Integer> list = new LinkedList<>();
        for(int i = 0; i < c; i++){
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }

    public static void main2(String[] args){
        Car[] tCars = {new Car("R", "T", 2009, 15, 2.2),
                new Car("R", "T", 2009, 15, 2.2),
                new Car("R", "T", 2009, 15, 2.2),
                new Car("R", "T", 2009, 15, 2.2),
                new Car("R", "T", 2009, 15, 2.2),
                new Car("R", "T", 2009, 15, 2.2),
                new Car("R", "T", 2009, 15, 2.2),
                new Car("R", "T", 2009, 15, 2.2),
                new Car("R", "T", 2009, 15, 2.2),
                new Car("R", "T", 2009, 15, 2.2)};
        String[] tIds = {"TA108", "TA109", "TA105", "TA103", "TA100", "TA107", "TA101", "TA102", "TA104", "TA106"};

        BTree<String, Car> bt = new BTree<>(2);
        int count = 10;
        List<String> ids = new ArrayList<>(CarsGenerator.generateShuffleIds(count));
        List<Car> cars = new ArrayList<>(CarsGenerator.generateShuffleCars(count));
        for(int i = 0; i < count; i++)
            bt.insert(tIds[i], tCars[i]);
        System.out.println("Before: ");
        bt.traverse();
        Car found = bt.get("TA101");
        for(int i = 0; i < count; i++) {
            if(bt.get(tIds[i]) == null)
            {
                System.out.println(tIds[i] + " was null.");
            }
            bt.remove(tIds[i]);
            System.out.println("Removed: " + tIds[i]);
        }
        System.out.println("After: ");
        bt.traverse();
    }
}
