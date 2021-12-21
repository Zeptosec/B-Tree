package Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class BTree<K extends Comparable<K>, V> {
    private BTNode<K, V> root;
    public final int degree;
    private int size;

    public BTree(int deg){
        if(deg < 1){
            System.out.println("Degree can not be smaller than 1");
            deg = 3;
        }
        root = null;
        degree = deg;
        size = 0;
    }

    /**
     * travels ant prints each node key
     */
    public void traverse(){
        if(root != null){
            root.traverse();
        }
    }

    /**
     * search the tree for the key
     * @param k key to search for
     * @return value of associated with the key
     */
    public V get(K k){
        if(root == null)
            return null;
        return root.search(k);
    }

    public void insert(K k, V v){
        if(k == null){
            System.out.println("Key is null.");
            return;
        }
        size++;
        Tuple<K, V> t = new Tuple<>(k, v);
        if (root == null){ // if empty
            root = new BTNode<>(degree, true);
            root.setKey(0, t);
            root.amount = 1;
        } else {
            // if its full
            if(root.amount == degree * 2 - 1){
                BTNode<K, V> s = new BTNode<>(degree, false);
                s.setChild(0, root);
                // splits the old root and moves one key to the new root
                s.splitChild(0, root);
                // new root is now with 2 children
                // assigning the key to appropriate child
                int i = 0;
                if(s.getKey(0).compareTo(k) < 0)
                    i++;
                s.getChild(i).insertNotFull(t);
                // changing root
                root = s;
            } else { // if root is not full then we can insert in it
                root.insertNotFull(t);
            }
        }
    }

    /**
     * removes the key from the tree
     * @param k the key to remove
     */
    public void remove(K k){
        if(root == null){
            System.out.println("Tree is empty");
            return;
        }
        if (root.remove(k))
            size--;
        // if root has 0 keys after removal
        // the first child is set to be the new root
        // if there is not a child present then root is null
        if(root.amount == 0){
            if(root.isLeaf)
                root = null;
            else
                root = root.getChild(0);
        }
    }

    /**
     * Prints tree to a console in a tree fashion form
     */
    public void printTreeConsole(){
        System.out.println(getTreeString());
    }

    public void printTreeFile(String path){
        try {
            PrintWriter pw = new PrintWriter(path);
            StringBuilder sb = getTreeString();
            char[] chars = new char[sb.length()];
            sb.getChars(0, sb.length(), chars, 0);
            pw.write(chars);
            pw.close();
            System.out.println("Printed tree to '" + path + "'");
        } catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private StringBuilder getTreeString(){
        if(root == null){
            return new StringBuilder("Tree is empty");
        }
        StringBuilder buffer = new StringBuilder();
        root.printTree(buffer, "", null, false, 0, 0);
        return buffer;
    }
}
