package Util;

public class Tuple<E1 extends Comparable<E1>, E2> implements Comparable<Tuple<E1, E2>> {
    private E1 key;
    private E2 val;
    public Tuple(E1 k, E2 v){
        key = k;
        val = v;
    }

    public E1 getKey(){
        return key;
    }

    public E2 getVal(){
        return val;
    }

    public void setKey(E1 k){
        key = k;
    }

    public void setVal(E2 v){
        val = v;
    }

    @Override
    public int compareTo(Tuple<E1, E2> o) {
        return key.compareTo(o.getKey());
    }

    public int compareTo(E1 o) {
        return key.compareTo(o);
    }

    @Override
    public String toString(){
        return "[" + getKey() + "|" + getVal() + "]";
    }
}
