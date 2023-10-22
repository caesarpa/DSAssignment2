package com.example.searchengine;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;

public class SimpleCrawler extends Crawler {


    protected SimpleCrawler(String indexFileName) {
        super(indexFileName);
    }

    public void crawl(String startUrl) {
        try {
            int duration = 0; //TODO: update the value in the code
            long startTime = System.currentTimeMillis();
            Set<String[]> lines = explore(startUrl, new HashSet<>(), new HashSet<>());
            FileWriter fileWriter = new FileWriter(indexFileName);
            CSVWriter writer = new CSVWriter(fileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER, ' ', "\r\n");
            for (String[] line : lines) {
                writer.writeNext(line);
            }
            writer.close();
            long endTime = System.currentTimeMillis();
            duration = (int) (endTime - startTime)/1000;
            System.out.println("duration simple crawler: " + duration + " seconds");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param startUrl the url where the crawling operation starts
     * @param lines    stores the lines to print on the index file
     * @param visited  stores the urls that the program has already visited
     * @return the set of lines to print on the index file
     */
    public Set<String[]> explore(String startUrl, Set<String[]> lines, Set<String> visited) {
        Queue queue = new LinkedList();
        queue.add(startUrl);

        while (!queue.isEmpty()) {
            try {
                String url = queue.poll().toString();
                if (!visited.contains(url)) {

                    visited.add(url);
                    Document doc = Jsoup.connect(url).get();
                    String[] words = doc.select("p").text().split(" ");
                    String[] links = doc.select("a").text().split(" ");
                    for (String link : links) {
                        String extLink = "https://api.interactions.ics.unisg.ch/hypermedia-environment/" + link;
                        if (!visited.contains(extLink)) {
                            queue.add(extLink);
                        }
                    }
                    String[] line = new String[4];
                    line[0] = "/" + url.substring(61);
                    line[1] = words[0].trim();
                    line[2] = words[1].trim();
                    line[3] = words[2].trim();
                    lines.add(line);

                }
            } catch (IOException e) {
                System.out.println("Error when connecting to url: " + e.getMessage());
            }
        }
        return lines;


    }

}
