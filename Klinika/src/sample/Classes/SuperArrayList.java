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

    public boolean superContains(String s){
        for (E e:this) {
            if(e.equals(s))
                return true;
            if ((e).equals("0" +s)) {     //dla godzin w formacie 08:00
                return true;
            }
        }
        return false;
    }
}
