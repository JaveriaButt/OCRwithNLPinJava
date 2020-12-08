package com.longlee.ocrwithnlp;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdjectiveLoader {
    //nounKey holds data of NounsIndex file
    protected Map<String, String> nounKey = new HashMap<String, String>();
    //nounDef hold data of NounsData file
    protected Map<String, String> nounDef = new HashMap<String, String>();
    Context cont;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public AdjectiveLoader(Context con)
    {
        cont=con;
        LoadData();

    }
    //this method loads data from txt files to dictionary.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void LoadData()
    {
        //loading data of Index file in dictionary ,

        String s = "";
        StringBuilder stringBuilder = new StringBuilder();
        InputStream is =cont.getResources().openRawResource(R.raw.adjindex);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        int i=0;
        while (true) {
            try {
                if ((s = reader.readLine()) == null) break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            s = s.trim();
            //split String that we read from file using |
            String[] token = s.split(":::");
            String[] multikeys = token[1].split(",");
            //checking if a key already exists then extend it's noun content, else add new item of that key in dictionary
            for(String k : multikeys)
            {
                if (nounKey.containsKey(k))
                    nounKey.replace(k,nounKey.get(k),nounKey.get(k) + "," + token[0]);
                else
                    nounKey.put(k, token[0]);
            }
            i++;
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //loading data of NounsData.txt in NounDef dictionary.
        is =cont.getResources().openRawResource(R.raw.adjdata);
        reader = new BufferedReader(new InputStreamReader(is));
        i=0;
        while (true) {
            try {
                if ((s = reader.readLine()) == null) break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            s = s.trim();
            //split String that we read from file using |
            String[] token = s.split(":::");
            nounDef.put(token[0], token[1]);
            i++;
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //this method checks if the word(ngram) exists in NounKey dictionary or not
    //if it exists then it picks the key of it and then calls FindDefinition method
    //to get the definition of that noun
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String CheckNGram(String word)
    {
        word = word.toLowerCase();
        List<String> list = new ArrayList<>();
        for(Map.Entry<String,String> x: nounKey.entrySet())
        {
            String[] y= x.getValue().split(",");
            for(String z : y)
            {
                if (z.toLowerCase().equals(word))
                {
                  //  list.add(x.getKey());
                    return nounDef.get(x.getKey());
                }
            }

        }
        return "";
    }
}
