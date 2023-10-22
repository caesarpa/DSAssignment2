package com.example.searchengine;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

@Component
public class Searcher {
    /**
     *
     * @param keyword to search
     * @param flippedIndexFileName the file where the search is performed.
     * @return the list of urls
     */
    public List<String> search(String keyword, String flippedIndexFileName){
        long duration = 0; //TODO: update the value in the code
        long startTime = System.currentTimeMillis();
        List<String> urls = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(flippedIndexFileName));
            String line;
            while ((line = br.readLine()) != null){
                String[] values = line.split(",");
                if (values[0].equals(keyword)){
                    for (int i = 1; i < values.length; i++){
                        String url = "https://api.interactions.ics.unisg.ch/hypermedia-environment"+values[i];
                        urls.add(url);
                    }
                    break;
                }
            }
        } catch (Exception e){
            System.out.println("Error while reading the file");
        }
        long endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        System.out.println("duration searcher flipped: "+duration/1000+" seconds");
        return urls;
    }


}
