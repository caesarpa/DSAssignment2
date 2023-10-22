package com.example.searchengine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
@Component
public class IndexFlipper {

    public void flipIndex(String indexFileName, String flippedIndexFileName){
        try {
            CSVReader csvReader = new CSVReader(new FileReader(indexFileName));
            List<String[]> csvLines = csvReader.readAll();
            Map<String, ArrayList<String>> lines = new HashMap<String, ArrayList<String>>();
            //TODO: define lines to contain the lines that should be printed to index_flipped.csv

            for (String line[]: csvLines){
                String url = line[0];
                for (int i=1; i < line.length; i++){
                    if (lines.containsKey(line[i])){
                        lines.get(line[i]).add(url);
                    }
                    else{
                        lines.put(line[i], new ArrayList<String>());
                        lines.get(line[i]).add(url);

                    }
                }
            }

            CSVWriter writer = new CSVWriter(new FileWriter(flippedIndexFileName),',', CSVWriter.NO_QUOTE_CHARACTER,' ',"\r\n");

            for (Map.Entry<String, ArrayList<String>> entry : lines.entrySet()) {
                ArrayList<String> urls = entry.getValue();
                String[] line = new String[urls.size()+1];
                line[0] = entry.getKey();
                for (int i=0; i < urls.size(); i++){
                    line[i+1] = urls.get(i);
                }
                writer.writeNext(line);
            }
            writer.close();


        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
