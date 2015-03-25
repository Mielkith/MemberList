/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package member.list;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Weary
 */




public class MemberList {

    static private ArrayList<ArrayList<String>> RUSU ;
    static private ArrayList<ArrayList<String>> AGB ;
    static private ArrayList<Integer> missing;
    static private Float threshold;
    
    static public void SetThreshold(Float t)
    {
        threshold = t;
    }
  
    
    
    /**
     * @param args the command line arguments
     */
    static public void begin(String[] args) {
      //paths for the all the files
        
        //TODO Make this dynamic
        
        //TODO deal with Excel formats
        
        if (args.length < 2)
        {
            return;
        }
     String agbPath = args[0];
     String rusuPath = args[1];
     String missingPath =  args[2];
     String membersPath = args[3];
     
     
   
       
       if (!missingPath.endsWith(".csv"))
       {
           missingPath += "\\missing.csv";
       }
                
        
        //missing will hold line numbers for any name missing from the AGB list
        //RUSU an AGB are structures to hold the files, which presumably will not be massive
        missing = new ArrayList<Integer>();
        ArrayList<Integer> typos = new ArrayList<Integer>();
        RUSU = new ArrayList<ArrayList<String>>();
        AGB = new  ArrayList<ArrayList<String>>();
        ArrayList<SmallStruct> members = new ArrayList<SmallStruct>();
        
        
        RUSU = ReadFile(rusuPath);
        AGB = ReadFile(agbPath);
        
        
        //for every entry in RUSU (We only care if it is in RUSU and not in AGB, not vice versa
        for(int i = 1; i < RUSU.size(); i++ )
        {
           
        try
        {
            //smallStruct will tell us if a line is present, its line number and if it contained a typo
            //the line number is recorded so we can delete it from the AGB list and speed up comparison
            SmallStruct s = IsNamePresent(RUSU.get(i).get(0), RUSU.get(i).get(1), i);
             
      //if it is not present- add it to the missing list
            if(!s.IsFound())
            {
                missing.add(i);   
                // if the name was spelt wrong then record the line number in RUSU
                
            }          
            //if it is present- remove that name from AGB for future checks
            else 
            {
                if (s.IsTypos())
                {
                    typos.add(i);
                }
                members.add(s);
                AGB.remove(s.GetLineNumber());
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
            
        }
        
        
        }
        //write the missing list to file
         WriteMissing(missingPath, typos);
        
        if (args.length == 4)
        {
            WriteMembers(membersPath, members);
        }
    }
   
    static public void WriteMembers(String path, ArrayList<SmallStruct> members)
    {
            try{
            FileWriter  writer = new FileWriter(path,false);
            for(int missingNames: missing)
            {
                writer.write("Name");
                writer.write(",");
                writer.write("Student Number");
                writer.write(",");
                writer.write("DOB");
                writer.write("\n");
            }
            
            
            for (int i = 0; i < members.size(); i++)
            {
                writer.write(members.toString());
            }
            writer.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
         
    }
    
    
    
    
    static public void WriteMissing(String missingPath, ArrayList<Integer> typos)
    {
            try{
            FileWriter  writer = new FileWriter(missingPath,false);
            for(int missingNames: missing)
            {
                writer.write(RUSU.get(missingNames).get(0));
                writer.write(",");
                writer.write(RUSU.get(missingNames).get(1));
                writer.write('\n');
            }
            
            
            if (!typos.isEmpty())
            {
                 writer.write("\n");
                writer.write("TYPOS");
                
                writer.write("\n");
                for (int spellingError: typos)
                {
                    writer.write(RUSU.get(spellingError).get(0));
                    writer.write(", ");
                    writer.write(RUSU.get(spellingError).get(1));
                    writer.write('\n');
                }
            }
            writer.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
         
    }
    //Read the file and perfrom string cleaning (case and white space)
    static public ArrayList<ArrayList<String>> ReadFile(String fileName)
    {
        File rusu = new File(fileName);
        
        //not sure why this is called the map, it is not a map 
        ArrayList<ArrayList<String>> theMap = new  ArrayList<ArrayList<String>>();
        try{
            
        FileInputStream inputStream = new FileInputStream(rusu);
        Scanner sc = new Scanner(inputStream);
        int lineCounter = 0;
        while (sc.hasNext()){
            //reads each line and adds the line number + the string values to the data structure
            String[] line = sc.nextLine().split(",");
            for (int i = 0; i < line.length; i++)
            {
                //remove trailing white space and strange characters
                line[i] = line[i].trim().toUpperCase();
                line[i] = line[i].replace("\"", "");
                line[i] = line[i].replace("\\", "");
            }
            
            theMap.add(lineCounter++, new ArrayList<String>(Arrays.asList(line)));
        }
        
        }
        catch(IOException e)
        {
             System.out.println(e);
        }
        
        return theMap;
        
    }
    
    
    
    //Retuns a SmallStruct indiciating if a name is present and weather it is spelt wrong
    static public SmallStruct IsNamePresent(String needle1, String needle2, int rusuIndex)
    {
        /*AGB is in the format: 0First Name, 1Surname, 2Num, 3Salutation, 4M/F,5,6,7,8,9,10,11Active,12Sen/Nov,13Club,14Due,15PaidDate,16,17DOB
         RUSU is in the format: 0SurName/FirstName, 1Card Nom, 2Joined, 3Expires
        problem names 
        DO NASCIMENTO FERNANDES SOUZA, ESTEVAO 
        GIOVANELLA VIVIAN, PRISCILA
        HAJI BAKIR, AAINAA SOFIYAH
        */   
            for (int j = 1; j < AGB.size(); j++)
            {
                
                //store in sympol strings for debugging
                String match1 = AGB.get(j).get(1);

                String match2 = AGB.get(j).get(0);

                //score of 2 is a match, 1 is a partial match and may need confirming or spell checking
                int score = JumbleMatch(needle1, needle2, match1, match2);
                 if (score == 2) 
                 {
                     SmallStruct s = new SmallStruct(j, true, AGB.get(j).get(17), RUSU.get(rusuIndex).get(2));
                     s.SetName(RUSU.get(rusuIndex).get(0) + "," + RUSU.get(rusuIndex).get(1));
                     return new SmallStruct(j, true, AGB.get(j).get(17), RUSU.get(rusuIndex).get(1));
                 }
                 else if (score == 1)
                 {
                      SmallStruct s = new SmallStruct(j, true,true, AGB.get(j).get(17), RUSU.get(rusuIndex).get(2));
                     s.SetName(RUSU.get(rusuIndex).get(0) + "," + RUSU.get(rusuIndex).get(1));
                     return new SmallStruct(j, true, AGB.get(j).get(17), RUSU.get(rusuIndex).get(1));
                 }
            //    } 
                
            }
        return new SmallStruct();
    }
    //Jumble match will try and match strings and return 2 - match, 1 - partial match - 0 no match. A return of 1 suggests a typo.
    static public int JumbleMatch(String needle1, String needle2, String Hay1, String Hay2)
    {
        //combine both parts of the name into one part
        String name1 = needle1 + " " + needle2;
        String name2 = Hay1 + " " + Hay2;
        //break the name into componants 
        String name1Parts[] = name1.split(" ");
        String name2Parts[] = name2.split(" ");
        
      
        int score = 0;
  
        //depending on which array will over run first search through each array
        //and match the individual componants. Every match generates a score
        
      
        for (int i = 0; i < name1Parts.length; i++)
        {
              for(int j = 0; j < name2Parts.length; j++)
              {
                  if (name1Parts[i].equals(name2Parts[j]))
                  {
                      score++;
                  }
              }
        }
        //Threshold for matches is 2
        if (score >= 2)
        {
            return 2;
        }
        else if (score >= 1)
        {
            if(TypoCheck(name1Parts, name2Parts))
            {
                return 1;
            }
        }
        
        //later if >1 check for typos with Euclidean Distance
        return 0;
            
        
    }
    
    static boolean TypoCheck(String name1Parts[], String name2Parts[])
    {
        
        
        //Take each name part1 and turn it into a symbol
        ArrayList<double[]> symbolParts1 = ToSymbols(name1Parts);
        ArrayList<double[]> symbolParts2 = ToSymbols(name2Parts);
        
        int score = 0;
        int characterCount = 0;
        
        //for every symbol in the first entry find the euclidean distance to every symbol
        //in the second entry. Check against a threshold to determine if this is the same person
        //each symbol is a count of the occurances of each letter of the alphabet 
        //ie .    A,B,C,D,E,F,G
        //        1,1,0,0,1,0,1 would be the symbol for GABE
        
        for(int i = 0; i < symbolParts1.size(); i++)
        {
           double[] symbol1 = symbolParts1.get(i);
           for (int j = 0; j < symbolParts2.size(); j++)
           {
                double[] symbol2 = symbolParts2.get(j);
                double distance = 0;
                for(int k = 0; k < 26; k++)
                {
                    //Eucliedian distance 
                    distance += Math.abs(symbol1[k] - symbol2[k]);
                }
                //divide by the length of the first entry (ie. the number of characters in that part
                // of the name
                distance = distance/name1Parts[i].length();
                
                //threshold 
                //TODO Set this dynamically
                if (distance < threshold)
                {
                    score++;
                }
               
           }
        }
        
        //return true if the score is high enough to be relatilvey sure of a match
        //2 of the words in the first entry have a close enough distance to be 
        //be the same as two of the words in the second entry
        if (score >= 2)
        {
            return true;
        }
        //Do the same for part2
        //Check each part against each other and calculate Euclidean Distance
        //If Distance > Threshold -> Score++
        //if Score > 2 return true
        return false;
        
      
    }
   
    
    static ArrayList<double[]> ToSymbols(String nameParts[]){
        
        char[] alpha = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        ArrayList<double[]> symbolParts = new  ArrayList<double[]>();
        ArrayList<char[]> cNameParts = new  ArrayList<char[]>();
          
         for (int i = 0; i < nameParts.length; i++)
        {
            //make char array and store
           cNameParts.add(nameParts[i].toCharArray()); 
           double[] symbol = new double[26];
           //for each letter of the alphabet
           for(int j = 0; j < alpha.length; j++)
           {          
               //cout the occurances in this name part
               double occurance = 0;
              
               //for each character in the char array
               for(int k = 0; k < cNameParts.get(i).length; k++)
               {
                   
                   char c = cNameParts.get(i)[k];
                   //count if it matches alphabet[j]
                   if (c == alpha[j])
                   {
                       occurance++;
                   }
                   
               }
               symbol[j] = occurance;
               
               
               
           }
           symbolParts.add(symbol);
           
        }
         
         return symbolParts;
    }
    
    
    
}
