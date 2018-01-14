package com.johnmeehan;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to modify the ConceptNet corpus. This class should only ever be run once and the output file it produces can be seen in this folder
 * With this in mind there is no need for this code to be run.
 * It simply takes in assertions.csv and converts it to condensedAssertions.csv
 */

public class ModifyConceptNetCorpus {

    //Our count value. This is global as we need to use it in our printMatches() method and our main class
    static int count;

    public static void main(String[] args) {

        //This is the knowledge resource we want to take in. The file is ConceptNets assertions.csv
        String csvFile = "assertions.csv";
        //This will be used to make sure we continue to have items to read in our assertions.csv file
        String line = "";

        ArrayList<String> finalOutputs = new ArrayList<>();
        //Output so the user know's the program is running
        System.out.println("Starting...");

        //We will write the output to a file called condensedAssertions.csv so create that file
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(new File("condensedAssertions.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Initialise a string builder. This will be used to write to condensedAssertions
        StringBuilder sb = new StringBuilder();

        //start looking at our assertions file
        try(BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            //while we continue to have new lines being read in
            while((line = br.readLine()) != null) {

                //String array called matches which stores every occurrence of a \n\r (new line) in a different index
                String[] matches = line.split("\n\r");

                //Set your count = 0. You don't want count to be added every time the while loop increments
                count = 0;

                //Store occurrence of "/,/c/en/"'s first index in our string.
                String findMatches = (printMatches(matches[0], "/,/c/en/"));
                //Count will have incremented here, so create a new String[] array of size count
                String[] storeFinalNouns = new String[count];
                //For every index of lm[]
                for (int i = 0; i < count; i++) {
                    //Cretae a new temp[] string array
                    String[] temp;
                    //split our find matches every time we see a ",". This is because we added "," in our printMatches method
                    temp = findMatches.split(",");
                    //Store temp[i] in a string
                    String storeTemp = temp[i];
                    //Parse whatever numeric value is in our string to an integer (remeber, print matches returns a string like "12,24,43,65.."
                    int intValue = Integer.parseInt(storeTemp);
                    //create a substring between our matched string matches[0] first line of our .csv file.
                    String temp1 = matches[0].substring(intValue, matches[0].length());
                    //find the next occurance of "/" (as per assertions.csv) and store that substring in storeFinalNoun
                    storeFinalNouns[i] = temp1.substring(0, temp1.indexOf("/"));
                    //we can't add i and i+1 so instead, we'll add i-1 and i: if (i>0)
                    if (i > 0) {
                        //add our storeFinalString[i-1], a comma, storeFinalNouns[i] and another comma to our string builder
                        sb.append(storeFinalNouns[i-1]);
                        finalOutputs.add(storeFinalNouns[i-1]);
                        finalOutputs.add(storeFinalNouns[i]);
                        sb.append(",");
                        sb.append(storeFinalNouns[i]);
                        sb.append(",");
                    }

                }

                //if count > 1, we have found two nouns matching our pattern above, so we'll find the corresponding verb
                if (count > 1) {

                    //reset count = 0. We don't want this starting from 2
                    count = 0;

                    //Store occurrence of "/a/[r/"s first index in our string.
                    String findMatchesOfVerbs = (printMatches(matches[0], "/a/\\[/r/"));
                    //Count will have incremented here, so create a new String[] array of size count
                    String[] storeFinalVerbs = new String[count];
                    for (int i = 0; i < count; i++) {
                        //Create a temporary string array
                        String[] temp;
                        //split our find matches every time we see a ",". This is because we added "," in our printMatches method
                        temp = findMatchesOfVerbs.split(",");
                        //Store temp[i] in a string
                        String storeTemp = temp[i];
                        //Parse whatever numeric value is in our string to an integer (remeber, print matches returns a string like "12,24,43,65.."
                        int intValue = Integer.parseInt(storeTemp);
                        //create a substring between our matched string matches[0] first line of our .csv file.
                        String temp1 = matches[0].substring(intValue, matches[0].length());
                        //find the next occurance of "/" (as per assertions.csv) and store that substring in storeFinalVerbs
                        storeFinalVerbs[i] = temp1.substring(0, temp1.indexOf("/"));
                        //append the noun to our string buffer and also append a new line
                        sb.append(storeFinalVerbs[i]);
                        finalOutputs.add(storeFinalVerbs[i]);
                        sb.append("\n");
                    }
                }
            }

            //This is the code used to remove certian relations form the ConcepetNet corpus. As stated, these relations generally produce more bad relations than good and
            //so it was satisfactory to remove these relations
            ArrayList<String> uselessVerbs = new ArrayList<>();
            uselessVerbs.add("Antonym");
            uselessVerbs.add("DerivedFrom");
            uselessVerbs.add("DistinctFrom");
            uselessVerbs.add("EtymologicallyRelatedTo");
            uselessVerbs.add("RelatedTo");
            uselessVerbs.add("SimilarTo");
            uselessVerbs.add("Synonym");
            uselessVerbs.add("dbpedia");

            //if any line of our input arrayList relations equals any of the above relations, remove them
            for(int i = 0; i<finalOutputs.size(); i+=3) {
                for(int j = 0; j<uselessVerbs.size(); j++) {
                    if(finalOutputs.get(i+2).equals(uselessVerbs.get(j))){
                        finalOutputs.remove(i+2);
                        finalOutputs.remove(i+1);
                        finalOutputs.remove(i);
                        i = (i-3);
                        break;
                    }
                }
            }

            System.out.println("half way there");

            //if any of the input arraylists nouns equals it we or be, remove them.
            ArrayList<String> uslessWords = new ArrayList<>();
            uslessWords.add("it");
            uslessWords.add("we");
            uslessWords.add("be");
            for(int i = 0; i<finalOutputs.size(); i+=3) {
                for(int j = 0; j<uslessWords.size(); j++) {
                    if(finalOutputs.get(i+1).equals(uslessWords.get(j)) || finalOutputs.get(i).equals(uslessWords.get(j))){
                        finalOutputs.remove(i+2);
                        finalOutputs.remove(i+1);
                        finalOutputs.remove(i);
                        i = (i-3);
                        break;
                    }
                }
            }

            //append all relevent noun: noun: verb triples to our output string builder
            StringBuilder sb1 = new StringBuilder();
            for(int i = 0; i<finalOutputs.size(); i+=3) {
                sb1.append(finalOutputs.get(i) + ",");
                sb1.append(finalOutputs.get(i+1) + ",");
                sb1.append(finalOutputs.get(i+2) + "\n");
            }


            //pw is our new csv object so write our string builder to it.
            pw.write(sb1.toString());
            //Close our print writer
            pw.close();
            //Output so the user know's the program is finished
            System.out.println("Finished!");
        }
        //Catch block incase we can't find the file we want to read in
        catch (IOException e) {
            e.printStackTrace();
        }
    }





    //This method takes in a String of text and matches occurances of a specific string
    public static String printMatches(String text, String regex) {

        //take in regular expression (in our case our string)
        Pattern pattern = Pattern.compile(regex);
        //take in the text
        Matcher matcher = pattern.matcher(text);
        String result= new String();

        //while we find a match
        while (matcher.find()) {

            //add the index of where it ends to a list and increment a counter(this is used later so we know how big our array and for loop will have to be
            //also add a comma between each index so we can easily split this string later
            result+=matcher.end() + ",";
            count++;
        }
        //return the string of indices
        return result;

    }
}