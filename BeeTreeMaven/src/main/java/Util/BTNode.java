package Util;

public class BTNode<K extends Comparable<K>, V> {
    Object[] keys;
    public int deg;
    BTNode<K, V>[] children;
    public int amount;
    public boolean isLeaf;

    public BTNode(int t, boolean isLeaf){
        deg = t;
        this.isLeaf = isLeaf;
        keys = new Object[2 * t - 1];
        children = new BTNode[2 * t];
        amount = 0;
    }

    public void setKey(int i, Tuple<K, V> t){
        keys[i] = t;
    }

    public Tuple<K, V> getKey(int i){
        return (Tuple<K, V>) keys[i];
    }

    public void setChild(int i, BTNode<K, V> c){
        children[i] = c;
    }

    public BTNode<K, V> getChild(int i){
        return children[i];
    }

    /**
     * prints the tree starting from this node
     */
    public void printTree(StringBuilder buffer, String prefix, K k, boolean isMiddle, int cameFromLeft, int cameFromRight){
        int i = 0;
        for (; i < amount; i++){
            if(!isLeaf){
                // going deeper from bottom node to upper node
                children[i].printTree(buffer, prefix + getPrf(i, isMiddle, cameFromRight), getKey(i).getKey(), i > 0, 1, cameFromRight-1);
            }
            buffer.append(prefix);
            buffer.append(getPrefix(k, isMiddle, i) + "──");
            buffer.append(getKey(i).getKey()).append("\r\n");
        }
        if(!isLeaf) { // after reaching the last key the bottom node has to be printed
            String prf = "   ";
            if(cameFromLeft > 0) // if came from left and turned left then vertical line is added
                prf = "│  ";
            children[i].printTree(buffer, prefix + prf, getKey(i-1).getKey(), false, cameFromLeft-1, 1);
        }
    }

    private String getPrf(int i, boolean isMiddle, int cFR){
        if(cFR > 0) return "│  ";
        boolean isMid = i > 0;
        String prf;
        if(isMid || isMiddle)
            prf = "│  ";
        else
            prf = "   ";
        return prf;
    }

    private String getPrefix(K k, boolean isMiddle, int curr){
        if(k == null){
            if(amount > 1){ // if it has more than one element we can start connecting
                if(curr == 0) return "┌"; // if it is the first then '┌' is printed          ┌ 0   top
                if(curr == amount - 1) return "└"; // if it is the last then '└' is printed  ├ 1   middle
                return "├"; // otherwise, it's in the middle so '├' is printed                └ 2   bottom
            } else return "─"; // if it has just one element then there is nothing to connect this key to
        }
        if(isMiddle) return "├";
        int cmp = k.compareTo(getKey(curr).getKey()); // comparing root key with child's key
        if(curr == 0 && cmp > 0)
            return "┌";
        if(curr == amount - 1 && cmp < 0)
            return "└";
        return "├";
    }

    /**
     * finds the first key that is greater of equal to k
     * @param k the key to find
     * @return position of key that is greater or equal to k
     */
    private int findKey(K k){
        int id = 0;
        while(id < amount && getKey(id).compareTo(k) < 0) id++;
        return id;
    }

    /**
     * removes a key from sub-tree when this node is considered as a root node
     * @param k key to remove
     */
    public boolean remove(K k){
        int id = findKey(k);
        // checking if the key that was found exists in the list
        if(id < amount && getKey(id).compareTo(k) == 0)
        {
            // if it is a leaf node then we remove from leaf
            if(isLeaf)
                removeFromLeaf(id);
            else
                removeFromNotLeaf(id);
        } else {
            // if it is a leaf node then the key does not exists in this tree
            if(isLeaf){
                System.out.println("Key '" + k + "' does not exists in the tree.");
                return false;
            }
            // flag indicates whether the key is present in the sub-tree
            boolean flag = id == amount;
            //if that child where the key is suppose to exist has less than defined degree of
            //children than we fill it up
            if(children[id].amount < deg){
                fill(id);
            }
            // if the child merged with the previous child we recurse through id-1 child
            // else we recurse on the id child which now contains defined degree of keys
            if(flag && id > amount){
                return children[id - 1].remove(k);
            } else {
                return children[id].remove(k);
            }
        }
        return true;
    }

    /**
     * gets the predecessor of keys[id]
     * @param id the id of the key
     * @return the predecessor of the key
     */
    private Tuple<K, V> getPred(int id) {
        BTNode<K, V> curr = children[id];
        // get the right most leaf of this child
        while(!curr.isLeaf)
            curr = curr.getChild(curr.amount);
        // return the last key of the leaf
        return curr.getKey(curr.amount - 1);
    }

    /**
     * gets the successor of keys[id]
     * @param id the id of the key
     * @return the left most child's key of that child
     */
    private Tuple<K, V> getSucc(int id){
        BTNode<K, V> curr = children[id + 1];
        // getting the left most child that is a leaf
        while(!curr.isLeaf)
            curr = curr.getChild(0);
        // returning the first key of that leaf
        return curr.getKey(0);
    }

    /**
     * fills the child with keys
     * @param id the id of child to fill
     */
    private void fill(int id){
        // if the previous child has more than defined degree of keys
        // then we borrow a key
        if (id != 0 && children[id - 1].amount >= deg){
            borrowFromPrev(id);
        }
        // if the next child has more than defined degree of keys
        // then we borrow a key from that child
        else if (id != amount && children[id + 1].amount >= deg){
            borrowFromNext(id);
        }
        // else if the id child is the last child then it is merged with previous neighbor
        // else it is merged with next neighbor
        else {
            if(id != amount)
                merge(id);
            else
                merge(id - 1);
        }
    }

    /**
     * borrows a key from the previous child and inserts it in id child
     * @param id the child that will borrow a key from the previous child
     */
    private void borrowFromPrev(int id){
        BTNode<K, V> curr = children[id];
        BTNode<K, V> prev = children[id - 1];

        // the last key of the prev goes to the parent and the one before last
        // is inserted as the first key in the curr.

        // making space for the key
        for (int i = curr.amount - 1; i >= 0; i--){
            curr.setKey(i + 1, curr.getKey(i));
        }
        // if it is not a leaf node then the children are adjusted
        if (!curr.isLeaf){
            for(int i = curr.amount; i >= 0; i--)
                curr.setChild(i + 1, curr.getChild(i));
        }
        // curr first key is set to id-1 key from this node
        curr.setKey(0, getKey(id - 1));
        // setting curr first child as a last child from prev
        if(!curr.isLeaf){
            curr.setChild(0, prev.getChild(prev.amount));
        }
        // the key is moved from prev to parent
        keys[id - 1] = prev.getKey(prev.amount - 1);
        curr.amount++;
        prev.amount--;
    }

    /**
     * borrows a key from the next child and inserts it in id child
     * @param id the child that will borrow a key from the next child
     */
    private void borrowFromNext(int id){
        BTNode<K, V> curr = children[id];
        BTNode<K, V> next = children[id + 1];

        // the id key is inserted as the last key in curr child
        curr.setKey(curr.amount, getKey(id));

        // next child's first child is inserted as the last child in curr
        if(!curr.isLeaf)
            curr.setChild(curr.amount + 1, next.getChild(0));

        // the first key from next is inserted into keys[id]
        keys[id] = next.getKey(0);

        // filling in the hole in next's keys
        for(int i = 1; i < next.amount; i++){
            next.setKey(i - 1, next.getKey(i));
        }

        // moving children accordingly
        if(!next.isLeaf){
            for(int i = 1; i <= next.amount; i++){
                next.setChild(i - 1, next.getChild(i));
            }
        }
        // adjusting the sizes
        curr.amount++;
        next.amount--;
    }

    /**
     * merges id child with id+1 child. The id+1 child is removed after merging
     * @param id the id of child to merge with next child
     */
    private void merge(int id){
        BTNode<K, V> curr = children[id];
        BTNode<K, V> next = children[id + 1];

        // the middle key is taken from this node and put into curr node keys[id]
        curr.setKey(deg - 1, getKey(id));

        // copying the keys from the next node to the curr node
        // and putting them at the end
        for(int i = 0; i < next.amount; i++){
            curr.setKey(i + deg, next.getKey(i));
        }
        // copying the children as well
        if(!curr.isLeaf){
            for(int i = 0; i <= next.amount; i++){
                curr.setChild(i + deg, next.getChild(i));
            }
        }

        // filling in the gap created after moving the key from this node
        for(int i = id + 1; i < amount; i++){
            keys[i - 1] = keys[i];
        }
        // filling the gap in children array
        for(int i = id + 2; i <= amount; i++){
            children[i - 1] = children[i];
        }
        // adjusting the amounts
        curr.amount += next.amount + 1;
        amount--;
    }

    /**
     * removes a node that is a leaf from the tree
     * @param id index of the node to be removed
     */
    private void removeFromLeaf(int id){
        // moving all the keys after id position backwards
        for (int i = id + 1; i < amount; i++)
            keys[i - 1] = keys[i];
        // a key was removed so amount is adjusted
        amount--;
    }

    /**
     * removes a node that is not a leaf from the tree
     * @param id index of the node to be removed
     */
    private void removeFromNotLeaf(int id){
        Tuple<K, V> t = getKey(id);

        // if this child has at least defined degree of keys
        // we find the predecessor of k in sub-tree of id child
        // replace k by pred and remove the pred in id child
        if(children[id].amount >= deg){
            Tuple<K, V> pred = getPred(id);
            keys[id] = pred;
            children[id].remove(pred.getKey());
        }
        // else if the child has less than defined degree of keys
        // then the next child is examined. If it has at least defined
        // degree of keys than we find the successor of k
        // and replace k by succ and delete the succ in that child sub-tree
        else if(children[id + 1].amount >= deg){
            Tuple<K, V> succ = getSucc(id);
            keys[id] = succ;
            children[id + 1].remove(succ.getKey());
        }
        // else if both of these children has less than defined degree of keys
        // then both of their keys are merged and k is deleted
        else {
            merge(id);
            children[id].remove(t.getKey());
        }
    }

    public void splitChild(int k, BTNode<K, V> node){
        BTNode<K, V> z = new BTNode<>(node.deg, node.isLeaf);
        z.amount = deg - 1;
        // copying second half of keys from node to z
        for(int i = 0; i < deg - 1; i++){
            z.keys[i] = node.getKey(i + deg);
        }
        if(!node.isLeaf){
            for (int i = 0; i < deg; i++){
                z.children[i] = node.getChild(i + deg);
            }
        }
        // reducing amount of keys in node
        node.amount = deg - 1;
        // making space for new child
        for (int i = amount; i >= k + 1; i--){
            children[i + 1] = children[i];
        }
        children[k + 1] = z;
        // node key will move here
        // making space for key
        for (int i = amount - 1; i >= k; i--){
            keys[i + 1] = keys[i];
        }
        // the middle key of node will move to this object
        keys[k] = node.getKey(deg - 1);
        // adjusting the amount of the keys
        amount++;
    }

    /**
     * inserts a key to this node if it is not full
     * @param t key to insert
     */
    public void insertNotFull(Tuple<K, V> t){
        int i = amount - 1; // index of last element
        if (isLeaf){
            // finding the place where to insert
            while(i >= 0 && getKey(i).compareTo(t) > 0){
                keys[i + 1] = keys[i];
                i--;
            }
            // inserting the key
            keys[i + 1] = t;
            amount++;
        } else { // if it is NOT a leaf
            // locating the child which will have the key
            while(i >= 0 && getKey(i).compareTo(t) > 0){
                i--;
            }
            // checking if the child is full
            if(children[i + 1].amount == 2 * deg - 1){
                // if it is full then we split it
                splitChild(i + 1, children[i + 1]);
                // the middle key moved up and it is splitted into two
                // assigning the key to appropriate part
                if (getKey(i + 1).compareTo(t) < 0)
                    i++;
            }
            children[i + 1].insertNotFull(t);
        }

    }

    /**
     * Prints all the keys in the tree
     */
    public void traverse(){
        int i = 0;
        for (; i < amount; i++){
            if(!isLeaf){
                children[i].traverse();
            }
            System.out.print(keys[i] + " ");
        }
        if(!isLeaf)
            children[i].traverse();
    }

    /**
     * searches for the node with the key
     * @param k key to search for
     * @return node that contains the key
     */
    public V search(K k) {
        int i = 0;
        while (i < amount && k.compareTo(getKey(i).getKey()) > 0) {
            i++;
        }
        if(i < 2*deg-1) {
            Tuple<K, V> t = getKey(i);
            // if the key was found
            if (t != null && t.compareTo(k) == 0)
                return t.getVal();
        }
        // if its a leaf
        if (isLeaf)
            return null;
        // search selected child
        return children[i].search(k);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < amount; i++){
            sb.append(keys[i]);
        }
        return sb.toString();
    }
}
