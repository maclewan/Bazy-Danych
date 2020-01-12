package sample.Classes;

import java.util.ArrayList;

public class SuperArrayList<E> extends ArrayList<E> {
    public E getLast(){
        if(this.size()==0){
            return null;
        }
        else
            return this.get(this.size()-1);
    }
}
