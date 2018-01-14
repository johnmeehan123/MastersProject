package com.johnmeehan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Triplet {

    //private variables to hold the verb, noun and noun of each triplet
    private final String verb;
    private final String noun1;
    private final String noun2;

    //getter methods as the variables are private so getter methods (used to stay in line with oop concepts)
    public String getVerb() {
        return verb;
    }

    public String getNoun1() {
        return noun1;
    }

    public String getNoun2() {
        return noun2;
    }

    //public constructor to create Triplet objects
    private Triplet(String verb, String noun1, String noun2) {
        this.verb = verb;
        this.noun1 = noun1;
        this.noun2 = noun2;
    }

    /**
     * This method will return a list of type triplet
     * @param list
     * @return List<Triplet>
     */
    public static List<Triplet> listForm(List<String> list) {
        //make sure the input list is divisible by 3. This is beacuse a triplet holds 3 variables so if its not dividisble by 3, it cant be a triplet
        Objects.requireNonNull(list.size() % 3 == 0);
        List<Triplet> result = new ArrayList<>();
        //for every item in the input list, store these into a new Triplet object and store it in our output list
        for(int i = 0; i<list.size(); i+=3) {
            result.add(new Triplet(
                    list.get(i), list.get(i+1), list.get(i+2)));
        }
        return result;
    }
}
