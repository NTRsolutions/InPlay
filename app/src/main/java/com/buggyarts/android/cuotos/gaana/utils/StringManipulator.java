package com.buggyarts.android.cuotos.gaana.utils;

import java.util.ArrayList;

/**
 * Created by mayank on 12/8/17
 */

public class StringManipulator {

    private String stringToManipulate;

    public StringManipulator(String stringToManipulate){
        this.stringToManipulate = stringToManipulate;
    }

    public ArrayList<String> refineString(){

        ArrayList<String> list = new ArrayList<>();
        String s1,temp;

        s1 = stringToManipulate.replace('&',',');


        if( s1.contains("- ") ){
            temp = s1;
            int index = temp.indexOf("- ");
            s1 = temp.substring(0,index);
        }

        String[] items = s1.split(",");

        for(int i = 0; i < items.length; i++){

            if( items[i].contains("(") ){
                int firstBracketIndex = items[i].indexOf("(");
                int secondBracketIndex = items[i].indexOf(")");
                temp = items[i].substring(firstBracketIndex,secondBracketIndex+1);
                items[i] = items[i].replace(temp," ");
            }

            items[i] = items[i].trim();

            if(!items[i].isEmpty()){
                list.add(items[i]);
            }
        }

        return list;
    }
}
