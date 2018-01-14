package com.johnmeehan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExtendedTriplet {

    //private variables to hold the noun1, noun1ID, verb, verbId, noun2, noun2id and graphid of each ExtendedTriplet
    private final String noun1;
    private final String noun1ID;
    private final String verb;
    private final String verbID;
    private final String noun2;
    private final String noun2ID;
    private final String graphID;

    //public constructor to create ExtendedTriplet objects
    private ExtendedTriplet(String noun1, String noun1ID, String verb, String verbID, String noun2, String noun2ID, String graphID)  {
        this.noun1 = noun1;
        this.noun1ID = noun1ID;
        this.verb = verb;
        this.verbID = verbID;
        this.noun2 = noun2;
        this.noun2ID = noun2ID;
        this.graphID = graphID;
    }

    //getter methods as the variables are prive(follow oop design reccomendations)
    public String getNoun1() {
        return noun1;
    }

    public String getNoun1ID() {
        return noun1ID;
    }

    public String getVerb() {
        return verb;
    }

    public String getVerbID() {
        return verbID;
    }

    public String getNoun2() {
        return noun2;
    }

    public String getNoun2ID() {
        return noun2ID;
    }

    public String getGraphID() {
        return graphID;
    }

    /**
     * This method will reuturn a list of type ExtendedTriplet
     * @param list
     * @return List<ExtendedTriplet>
     */
    public static List<ExtendedTriplet> listForm(List<String> list) {
        //make sure the list is divisible by 9, to stay consistent in fomratting for the rest of the project
        Objects.requireNonNull(list.size() % 9 == 0);
        List<ExtendedTriplet> result = new ArrayList<>();
        //for every item in the input list, store desired indices into a new ExtendedTriplet Object
        for(int i = 0; i < list.size(); i+=9) {
            result.add(new ExtendedTriplet(
                    list.get(i), list.get(i+1), list.get(i+3), list.get(i+4), list.get(i+6), list.get(i+7), list.get(i+8)));
        }
        return result;
    }
}
