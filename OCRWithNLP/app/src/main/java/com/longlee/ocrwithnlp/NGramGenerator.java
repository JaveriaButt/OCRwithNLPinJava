package com.longlee.ocrwithnlp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class NGramGenerator
{
    private String outputText="";
    private String[] inputString;
    NounLoader nl ;
    AdjectiveLoader al;
    Context con;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public NGramGenerator(Context context)
    {
        outputText = "";
        con=context;
        nl=new NounLoader(con);
        al=new AdjectiveLoader(con);
    }
    //this method manages the input text. divides them in sentences and then splits
    //each sentence futher into words and pass array of each sentence words to NGram
    //method to generate 2,3,4 NGrams
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String GenerateNGram(String str)
    {
        inputString = str.split("xxx");
        outputText = str+"\n\n";
        for(String x: inputString)
        {

            if(x!=null)
            {

                String[] wordsting = x.split(" |\n");
                outputText += "\n\n-----" + x + "-----\n\n";
                NGram(wordsting,1);
                NGram(wordsting, 2);
                NGram(wordsting, 3);
                NGram(wordsting, 4);

            }
        }
        return outputText;
    }
    //this method generates NGram of n number. where n is a positive integer..
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void NGram(String[] x, int n)
    {
        //outputText is a String that actually holds all the data that at the end we have to save in txt file.
        outputText += "\n\n" + n + " NGram : \n\n";
        //this loop manages all words in a String
        for (int i = 0; i < x.length-(n-1); i++)
        {
            String temp = "" ;
            //this actually generate an NGram of n length.
            for (int j = i; j < i+n; j++)
            {
                outputText += x[j] + "_";
                temp += x[j] + "_";
            }
            //removing extra underscore from last
            outputText = outputText.substring(0, outputText.length() - 1);
            temp = temp.substring(0, temp.length() - 1);
            //calling CheckNGram method of NounLoader file which checks if the word is noun
            //or not. if it's noun it finds it's definition from the dictionary and returns it.
            temp=temp.toLowerCase();
            temp=temp.replace(".","");
          String t=nl.CheckNGram(temp);
         //   outputText += " :::: " + nl.CheckNGram(temp) + "\n";
            if(t!="")
                outputText += "," +t+ "\n";
            else
                outputText += "," + al.CheckNGram(temp) + "\n";
        }
    }

}
