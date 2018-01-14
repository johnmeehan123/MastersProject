package com.johnmeehan;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        //Used for testing, the start time of the program in milli-seconds
        long startTime = System.currentTimeMillis();

        //Create an arrayList of every graph Id you want to find relations for
        ArrayList<Integer> alint = new ArrayList<>();

        //some output graphs we want the background links for. Fill this randomly with 20 graphs.
        Random random = new Random();
        while (alint.size() < 20) {
            alint.add(random.nextInt(1148));
        }

        //this is how we run the ouput. We create an arrayList from the "AllAbstractsROS.XLSX" file, pass in the ids of 3, 6, 11 & 12 and we want to serach for relations between verbs so we pass true
        createArrayListFromKnowledgeGraph("AllAbstractsROS.XLSX", alint, true);

        //Used for testing, print the end time of the program in milli-seconds
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    /**
     * This file will take in the filename where your ROS graphs are stored, the Ids you want to search for and the boolean of if you want to search for verbs
     * It throws an IOException incase the input file name does not exist
     * Please note, the external Apache POI library is needed to run this method. The library will be included in the "Project Code" Folder
     * @param filename
     * @param alInt
     * @param searchForVerbNodes
     * @return void
     * @throws IOException
     */
    private static void createArrayListFromKnowledgeGraph(String filename, ArrayList<Integer> alInt, boolean searchForVerbNodes) throws IOException {
        StringBuilder storeValuesFromExcelCells = new StringBuilder();
        StringBuilder outputStringBuilder = new StringBuilder();

        //Where we want to print the ouputs
        PrintWriter outputPrintWriter = new PrintWriter(new File("Outputs/AbstractsOutputs.csv"));

        //Create a new workbook from the input XLSX file
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filename));
        //Create a new sheet from the input XLSX workbook. Dr Inventor only uses 1 sheet which is at index 0
        XSSFSheet sheet = wb.getSheetAt(0);

        //Loop through every row in our input sheet. We start at 1 as the first line of the input ROS graphs are headings
        for(int q = 1; q < sheet.getLastRowNum() + 1; q++) {
            XSSFRow row = sheet.getRow(q);

            //There are 3 cells in each row - "relation(subject, object) so we loop through the 3 cells
            for(int j = 0; j < 3; j++) {
                //This will look through each graph id. We use this so that each input graph id is treated separately
                for (Integer anAlInt : alInt) {
                    //Set the cell we're working with as String "shortcut" used so we're not repeating as much code
                    String shortcut = String.valueOf(row.getCell(j));

                    //Only look through the Graphid's your currently interested in
                    if (shortcut.contains("\"Graphid\":" + anAlInt + ",")) {

                        //Some graphs don't contain an IntNodeId, if they don't give it a nodeId of -1
                        if (!shortcut.contains("\"IntNodeId\":")) {
                            shortcut = "{\"IntNodeId\":-1," + String.valueOf(row.getCell(j));
                        }

                        //Store every index in the input cell where "Word" occurs
                        String findMatchesWord = (printMatches(shortcut, "\"Word\":\""));
                        //Store every index in the input cell where "IntNodeId" occurs
                        String stringForNodeId = (printMatches(shortcut, "\"IntNodeId\":"));
                        //Store every index in the input cell where "Graphid" occurs
                        String stringForGraphId = (printMatches(shortcut, "\"Graphid\":"));

                        //Parse every index where a word occurs to an integer and place it in an integer array
                        int[] wordToInt = Arrays.stream(findMatchesWord.split(","))
                                .mapToInt(Integer::parseInt)
                                .toArray();

                        //Parse every index where a nodeId occurs to an integer and place it in an integer array
                        int[] nodeIdToInt = Arrays.stream(stringForNodeId.split(","))
                                .mapToInt(Integer::parseInt)
                                .toArray();

                        //Parse every index where a graphId occurs to an integer and place it in an integer array
                        int[] graphIdToInt = Arrays.stream(stringForGraphId.split(","))
                                .mapToInt(Integer::parseInt)
                                .toArray();


                        for (int i = 0; i < wordToInt.length; i++) {
                            //Append each nodes word to a string builder followed by a comma
                            String temp1 = shortcut.substring(wordToInt[i]);
                            //delimiter "/" used to mark the end of a word
                            String temp2 = temp1.substring(0, temp1.indexOf("\""));
                            storeValuesFromExcelCells.append(temp2);
                            storeValuesFromExcelCells.append(",");

                            //Append each nodes id to a string builder followed by a comma
                            String temp3 = shortcut.substring(nodeIdToInt[i]);
                            //delimiter "," used to mark the end of a word
                            String temp4 = temp3.substring(0, temp3.indexOf(","));
                            storeValuesFromExcelCells.append(temp4);
                            storeValuesFromExcelCells.append(",");

                            //Append each nodes graphid to a string builder followed by a comma
                            String temp5 = shortcut.substring(graphIdToInt[i]);
                            //delimiter used "," to mark the end of a word
                            String temp6 = temp5.substring(0, temp5.indexOf(","));
                            storeValuesFromExcelCells.append(temp6);
                            storeValuesFromExcelCells.append(",");
                        }

                    }
                }
            }
        }
        //Transform the string builder above into an arrayList using the delimiter of ","
        ArrayList<String> storeValuesFromStringBuilder = new ArrayList<>(Arrays.asList(storeValuesFromExcelCells.toString().split(",")));

        //Again loop through every graph id you're interested in
        for (Integer anAlInt : alInt) {

            //append each anAlInt followed by a newline to your string builder, this will go to the output. This indicator is used to keep track of the different Graphs
            outputStringBuilder.append(anAlInt).append("\n");
            ArrayList<String> toPerformSearchOn = new ArrayList<>();

            //for every index in our input arrayList
            for (int i = 0; i < storeValuesFromStringBuilder.size(); i += 9) {

                //if we're on the same graph number we're interested in (the graph id is stored at index 3 of each +9 for loop
                if (Integer.parseInt(storeValuesFromStringBuilder.get(i + 2)) == anAlInt) {

                    //then add all the values from this graph id to the toPerformSearchOn arrayList
                    toPerformSearchOn.add(storeValuesFromStringBuilder.get(i));
                    toPerformSearchOn.add(storeValuesFromStringBuilder.get(i + 1));
                    toPerformSearchOn.add(storeValuesFromStringBuilder.get(i + 2));
                    toPerformSearchOn.add(storeValuesFromStringBuilder.get(i + 3));
                    toPerformSearchOn.add(storeValuesFromStringBuilder.get(i + 4));
                    toPerformSearchOn.add(storeValuesFromStringBuilder.get(i + 5));
                    toPerformSearchOn.add(storeValuesFromStringBuilder.get(i + 6));
                    toPerformSearchOn.add(storeValuesFromStringBuilder.get(i + 7));
                    toPerformSearchOn.add(storeValuesFromStringBuilder.get(i + 8));

                }
            }

            //Create a new arrayList which contains the perform searched values based on the previous arrayList with your boolean if you want to search for verbs)
            ArrayList<String> outputOfSearch = (performSearch(toPerformSearchOn, searchForVerbNodes));

            //This retuns 5 values, your node word, node id, verb, node2 word, noun2 id,
            for (int i = 0; i < outputOfSearch.size(); i += 5) {

                //append each value returned to your output string builder. Keeping format consistent wiht the .csv format
                outputStringBuilder.append(outputOfSearch.get(i)).append(", ");
                outputStringBuilder.append(outputOfSearch.get(i + 1)).append(", ");
                outputStringBuilder.append(outputOfSearch.get(i + 2)).append(", ");
                outputStringBuilder.append(outputOfSearch.get(i + 3)).append(", ");
                outputStringBuilder.append(outputOfSearch.get(i + 4)).append("\n");
            }
        }

        //write the above string builder to the file name above
        outputPrintWriter.write(outputStringBuilder.toString());
        //close your print writer
        outputPrintWriter.close();
    }

    /**
     * This method performs the actual search. Here is where we will perform the actual addtion of background information
     * @param inputKnowledgeGraph
     * @param searchForVerbNodes
     * @return ArrayList<String>
     */
    private static ArrayList<String> performSearch(ArrayList<String> inputKnowledgeGraph, boolean searchForVerbNodes) {

        //Call the takeOutCoreferents method on the input knowledge graph. This will remove coreferents from the input knowledge graph
        inputKnowledgeGraph = (ArrayList<String>) takeOutCoreferents(inputKnowledgeGraph);

        //Create a string arrayList from the takeInConceptNet method
        ArrayList<String> conceptNetCorpus = takeInConceptNetCorpus();

        //Create a List of type ExtendedTriplet, which consists of every ExtendedTriplet objects from the input knowledge graph
        final List<ExtendedTriplet> tripletsOne = ExtendedTriplet.listForm(inputKnowledgeGraph);
        //Create a List of type Triplet, which consists of every Triplet object from the input ConceptNetCorpus
        final List<Triplet> tripletsTwo = Triplet.listForm(conceptNetCorpus);

        //create a HashMap<String, List<ExtendedTriplet>. This line creates a hashmap of where a key is all nounOnes from the tripletsOne ArrayList
        //and all values are every ExtendedTriplet where these nounOnes occur
        final Map<String, List<ExtendedTriplet>> mapOneByNoun1 = tripletsOne.stream()
                .collect(Collectors.groupingBy(ExtendedTriplet::getNoun1));

        //create a HashMap<String, List<ExtendedTriplet>. This line creates a hashmap of where a key is all nounTwos from the tripletsOne ArrayList
        //and all values are every ExtendedTriplet where these nounTwos occur
        final Map<String, List<ExtendedTriplet>> mapOneByNoun2 = tripletsOne.stream()
                .collect(Collectors.groupingBy(ExtendedTriplet::getNoun2));

        //create an immuatable empty list
        final List<ExtendedTriplet> EMPTY = Collections.emptyList();
        ArrayList<String> finalCheck = new ArrayList<>();

        //for every triplet in our conceptNet file
        for(final Triplet z : tripletsTwo) {
            //for every Extended Triplet in our input mapOneByNoun1. Call the .getordefualt method on every noun one and the empty list. This will only search for links that will return true. I.e it will never search non matching entries
            for(final ExtendedTriplet x : mapOneByNoun1.getOrDefault(z.getNoun1(), EMPTY)){
                //for every Extended Triplet in our input mapOneByNOun2. Call the .getordefualt method on every noun two and the empty list. This will only search for links that will return true. I.e it will never search non matching entries
                for(final ExtendedTriplet y : mapOneByNoun2.getOrDefault(z.getNoun2(), EMPTY)) {
                    //if we find any two nodes present consecutively in the ConceptNet corpus, add the noun1, noun1id, verb, noun2, noun2id to the output
                    if(x.getNoun1().equals(z.getNoun1()) && y.getNoun2().equals(z.getNoun2())) {
                        finalCheck.add(x.getNoun1());
                        finalCheck.add(x.getNoun1ID());
                        finalCheck.add(z.getVerb());
                        finalCheck.add(y.getNoun2());
                        finalCheck.add(y.getNoun2ID());
                    }
                }
            }
        }

        //perform the same search as described above however this time were searching node twos first and noun1s on the inside loop
        for (final Triplet z : tripletsTwo) {
            for (final ExtendedTriplet x : mapOneByNoun2.getOrDefault(z.getNoun1(), EMPTY)) {
                for (final ExtendedTriplet y : mapOneByNoun1.getOrDefault(z.getNoun2(), EMPTY)) {
                    if (x.getNoun2().equals(z.getNoun1()) && y.getNoun1().equals(z.getNoun2())) {
                        finalCheck.add(x.getNoun2());
                        finalCheck.add(x.getNoun2ID());
                        finalCheck.add(z.getVerb());
                        finalCheck.add(y.getNoun1());
                        finalCheck.add(y.getNoun1ID());
                    }
                }
            }
        }

        //if the user wants to also search for verbs
        if(searchForVerbNodes) {

            //create a HashMap where every key is a verb and every value is an extended triplet where this verb occurs
            final Map<String, List<ExtendedTriplet>> mapOneByVerb = tripletsOne.stream()
                    .collect(Collectors.groupingBy(ExtendedTriplet::getVerb));

            //same search process described above, however this time we will search for links between verb nodes instead of links between noun nodes in the input graph
            for (final Triplet z : tripletsTwo) {
                for (final ExtendedTriplet x : mapOneByVerb.getOrDefault(z.getNoun1(), EMPTY)) {
                    for (final ExtendedTriplet y : mapOneByVerb.getOrDefault(z.getNoun2(), EMPTY)) {
                        if (x.getVerb().equals(z.getNoun1()) && y.getVerb().equals(z.getNoun2())) {
                            finalCheck.add(x.getVerb());
                            finalCheck.add(x.getVerbID());
                            finalCheck.add(z.getVerb());
                            finalCheck.add(y.getVerb());
                            finalCheck.add(y.getVerbID());
                        } else if (x.getVerb().equals(z.getNoun2()) && y.getVerb().equals(z.getNoun1())) {
                            finalCheck.add(x.getVerb());
                            finalCheck.add(x.getVerbID());
                            finalCheck.add(z.getVerb());
                            finalCheck.add(y.getVerb());
                            finalCheck.add(y.getVerbID());
                        }
                    }
                }
            }
        }

        //return the resutls but before we do this, we'll remove the duplicates
        return removeUselessOutputs(removeDuplicates(removeDuplicates(finalCheck)));
    }

    /**
     * This method takes in no parameters as our ConceptNet corpus is constant
     * @return Array<String>
     */
    private static ArrayList<String> takeInConceptNetCorpus() {
        //Take in the condesnsedAssertions csv file
        String csvFile = "condensedAssertions.csv";
        BufferedReader br = null;
        String line;
        //As the condensedAssertions csv file is separated by ",", this is our delimiter
        String csvSplitBy = ",";
        //An arrayList used to store the conceptNet file
        ArrayList<String> storeAsserions = new ArrayList<>();
        try {
            //take in the input csv file using a buffered reader
            br = new BufferedReader(new FileReader(csvFile));
            //while we continue to have lines in the conceptnet file, keep reading it in
            while ((line = br.readLine()) != null) {

                //Store every specific word in an arraylist and store it in a string arrayList
                String[] specificCell = line.split(csvSplitBy);

                //store all in the arraylist. We'll put the verb in first followed by the nouns that connect them
                storeAsserions.add(specificCell[2]);
                storeAsserions.add(specificCell[0]);
                storeAsserions.add(specificCell[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //return the conceptnet corpus
        return storeAsserions;
    }

    /**
     * This method takes in a piece of text and the string you want to find within this text. It returns a string of containing every index where the desired string occurs in the text. For example it will return something like "12,24,124"
     * @param text
     * @param regex
     * @return String
     */
    private static String printMatches(String text, String regex) {
        //take in regular expression (in our case our string)
        Pattern pattern = Pattern.compile(regex);
        //take in the text
        Matcher matcher = pattern.matcher(text);
        StringBuilder result= new StringBuilder();

        //while we find a match
        while (matcher.find()) {

            //add the index of where it ends to a list and increment a counter(this is used later so we know how big our array and for loop will have to be
            result.append(matcher.end()).append(",");
        }
        //return the string of indices
        return result.toString();
    }

    /**
     * This method will take in the input ArrayList and takes out the corefrents as described in the thesis. Nodes sometimes
     * contain words such as it_car and so when we perform the search, car will not be found as it has it attached.
     * This method will take out the word it in this case
     * @param alTomodify
     * @return List<String>
     */
    private static List<String> takeOutCoreferents(ArrayList<String> alTomodify){
        //Crate an arrayList called unnecessaryWords and store each coreferent you want to remove in it
        List<String> unnecessaryWords = new ArrayList<>();
        unnecessaryWords.add("it");
        unnecessaryWords.add("then");
        unnecessaryWords.add("the");
        unnecessaryWords.add("be");
        unnecessaryWords.add("because");
        unnecessaryWords.add("that");
        unnecessaryWords.add("which");
        unnecessaryWords.add("they");
        unnecessaryWords.add("you");
        unnecessaryWords.add("have");
        unnecessaryWords.add("do");

        //_ is the indicator of a coreferent. If a index in your arraylist contains a _ and eithe side of this contains a "unnecessaryWord", remove it
        for(int i = 0; i<alTomodify.size(); i++) {
            if(alTomodify.get(i).contains("_")){
                String s1 = (alTomodify.get(i).split("_")[0]);
                String s2 = (alTomodify.get(i).split("_")[1]);
                for (String unnecessaryWord : unnecessaryWords) {
                    if (s1.equals(unnecessaryWord)) {
                        alTomodify.set(i, s2);
                    } else if (s2.equals(unnecessaryWord)) {
                        alTomodify.set(i, s1);
                    }
                }
            }
        }
        //return arraylist without coreferents
        return alTomodify;
    }

    /**
     * This will remove nodes that loop back to themselves. For example if we have catIscat
     * it will remove this as it's not useful for our output
     * @param al
     * @return ArrayList<String>
     */
    private static ArrayList<String> removeUselessOutputs(ArrayList<String> al) {

        for(int i = 0; i<al.size(); i+=5) {
            if(al.get(i).equals(al.get(i+3))){
                al.remove(i+4);
                al.remove(i+3);
                al.remove(i+2);
                al.remove(i+1);
                al.remove(i);
                i = (i-5);
            }
        }
        return al;
    }

    /**
     * Remove duplicates from the input ArrayList
     * Duplicates are unnecessary for use by the Dr Inventor system and so we take them ouput before we give this to the system
     * @param inptutAl
     * @return ArrayList<String>
     */
    private static ArrayList<String> removeDuplicates(ArrayList<String> inptutAl){
        for(int i = 0; i<inptutAl.size(); i+=5) {
            for(int j = (i+5); j<inptutAl.size(); j+=5){
                //if every index equals any other group of indcies, remove all
                if(inptutAl.get(i).equals(inptutAl.get(j)) && inptutAl.get(i+2).equals(inptutAl.get(j+2)) && inptutAl.get(i+3).equals(inptutAl.get(j+3))){
                    inptutAl.remove(j+4);
                    inptutAl.remove(j+3);
                    inptutAl.remove(j+2);
                    inptutAl.remove(j+1);
                    inptutAl.remove(j);
                    j = (i+5);
                }
            }
        }
        return inptutAl;
    }
}