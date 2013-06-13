package datacollection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matcher.Matcher;
import matcher.CMULexicon;

import datastructures.FeatureSet;
import datastructures.FeatureSetLookup;
import datastructures.Lexicon;
import datastructures.Matching;
import datastructures.Ranking;

/**
 * Reads phones from a file (.lm file format) and attempts to match.
 * @author Jessica Kenney
 *
 */
public class PhonesFromFile {

    public static void main(String[] args) throws IOException{
        //create the lexicon (from the file timitdict.txt)
        Lexicon lexicon = new CMULexicon("src/english/cmudict.0.7a");
        //creating the matcher, based off of that lexicon.
        Matcher matcher = new Matcher(lexicon);
        
        //Read the phone sequence from file
        BufferedReader br = new BufferedReader(new FileReader("src/english/Conv07_final.lm"));
        List<String> phones = new ArrayList<String>();
        String line;
        while ((line = br.readLine()) != null){
            if (line.startsWith("#")){
                continue;
            }
            //System.out.println(line);
            line = line.replace("\n", "").replace("\r", "");
            String[] parts = line.split(" ");
            //System.out.println(parts[0]);
            //System.out.println(parts[1]);
            if (parts.length == 2){
                phones.add(parts[1].replaceAll("[0-9]","").toLowerCase());
            }
            else {
            	phones.add("");
            }
        }
        br.close();
        
        //Split into "phone groups", separating by spaces in the phone list
        //So instead of running the matcher on the entire file, it splits up
        //into smaller, more managable chunks
        List<ArrayList<String>> phoneGroups = new ArrayList<ArrayList<String>>();
        ArrayList<String> phoneList = new ArrayList<String>();
        for (String phone : phones){
        	if (phone.trim()==""){
        		//System.out.println("Adding phonegroup to groups");
        		//System.out.println(phoneList);
        		phoneGroups.add(phoneList);
                ArrayList<String> newPhoneList = new ArrayList<String>();
                phoneList = newPhoneList;
        	}
        	else {
        		//System.out.println("Adding phone to group");
        		phoneList.add(phone);
        	}
        }
        //System.out.println(phoneGroups);
        for (ArrayList<String> phoneGroup : phoneGroups){
        	if(phoneGroup.size() > 0){
		        //Get FeatureSet sequence from phones list
		        List<FeatureSet> featureSetSequence = new ArrayList<FeatureSet>();
		        for (String phone : phoneGroup){
		            for (FeatureSet fs : FeatureSetLookup.lookup(phone)){
		                featureSetSequence.add(fs);
		            }
		        }
		        
		        //Get some predicted matchings.
		        //System.out.print("Running matcher on ");
		        //System.out.print(phoneGroup);
		        //System.out.print("....\n");
		        List<Matching> matchings = matcher.match(featureSetSequence, 0.1f);
		        //System.out.println("Finished matcher");
		        //System.out.println(matchings);
		        //Print best matching
		        for (Ranking r : matchings.get(0).getRankings()){
		        	System.out.println(r.getBestProbabilitySet().getWords());
		        }
        	}
        }
        
        //Present all the matchings to the user.
//        for (Matching m : matchings){
//            System.out.println(String.format("Matching (p = %f):",m.getBestProbability()));
//            for (Ranking r : m.getRankings()){
//                //The most probable words from each ranking in the matching are the ones we want to
//                //display when we represent the matching for the user.
//                System.out.print(r.getBestProbabilitySet().getWords());
//            }
//            System.out.println();
//        }
    }

}
