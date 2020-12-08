package com.longlee.ocrwithnlp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.PendingIntent.getActivity;

public class NounLoader
{
    //nounKey holds data of NounsIndex file
    protected Map<String, String> nounKey = new HashMap<String, String>();
    //nounDef hold data of NounsData file
    protected Map<String, String> nounDef = new HashMap<String, String>();
    Context cont;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public NounLoader(Context con)
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
        InputStream is =cont.getResources().openRawResource(R.raw.nounsindex);
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
        is =cont.getResources().openRawResource(R.raw.nounsdata);
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
                //    return FindDefinition(word, list);
                }
            }
        }
       return "";
    }

    //using binary search to find definition.
    //this function finds definition of a word in NounsData file.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String FindDefinition(String word, List<String> list)
    {
        //for binary search we used variables low,nlow,high,nhigh,mid,nmid

        String l = list.get(0);
        //contains key of lowest index
        String low=null;
        for (String key : nounDef.keySet()) {
            low=key;
        }
        int nlow = 0;//contains index of lowest memeber of dictionary
        String high=null;//contains key of highest noun
        for (String key : nounDef.keySet()) {
            high=key;
            break;
        }
        int nhigh = nounDef.size();//contains index of highest noun
        String mid  = (String) nounDef.keySet().toArray()[(nlow+nhigh)/2]; //contains key of middle noun
        int nmid = (nlow + nhigh) / 2; //contains index of middle noun
        String s = "";
        while (Integer.parseInt(high) >= Integer.parseInt(low))
        {
            mid  = (String) nounDef.keySet().toArray()[(nlow+nhigh)/2];

            nmid = (nlow + nhigh) / 2;
            s = nounDef.get(mid);
            s = s.trim();

            String result = "";
            //if key found in nounDef file, return definition....
            //binary search 3 conditions,
            //if key is equal to mid it returns result
            if (mid.equals(l))
            {
                result += s;
                return result;
            }
            //if key is greater then mid then move to right means replace low with mid
            else if (Integer.parseInt(l) >= Integer.parseInt(mid))
            {
                low = mid;
                nlow = nmid;
            }
            //if key is less then mid then move to left side of binary searching, replace high with mid.
            else
            {
                high = mid;
                nhigh = nmid;
            }

        }
        //if we don't find word in NounsData file then return empty String
        return "";
    }
}
