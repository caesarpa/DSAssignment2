package com.example.searchengine;

import com.opencsv.CSVWriter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.print.Doc;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class MultithreadCrawler extends Crawler {

    private ThreadPoolTaskExecutor executorService;


    private CopyOnWriteArraySet<String> visited;

    private CopyOnWriteArraySet<String[]> lines;


    private boolean done = false;

    public MultithreadCrawler(String indexFileName) {
        //TODO: initialize
        super(indexFileName);
        executorService = new ThreadPoolTaskExecutor();
        executorService.setCorePoolSize(20);
        executorService.initialize();
        visited = new CopyOnWriteArraySet<String>();
        lines = new CopyOnWriteArraySet<String[]>();


    }

    public void crawl(String startUrl) {
        double startTime = System.currentTimeMillis();
        //TODO: complete
        executorService.submit(new CrawlerRunnable(this, startUrl));
        while (executorService.getActiveCount() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(indexFileName);
            CSVWriter writer = new CSVWriter(fileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER, ' ', "\r\n");
            for (String[] line : lines) {
                writer.writeNext(line);
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executorService.shutdown();


        double endTime = System.currentTimeMillis();
        double duration = endTime - startTime;
        duration = duration / 1000;
        System.out.println("duration: " + duration + " seconds");
        int x = 0;
    }


    /*
      TODO: complete class.
      The purpose of this runnable is to do two tasks:
      1. Process the page at the given url (startUrl).
      2. Create new jobs for the hyperlinks found in the page.
      The instances of this class are used as input to the executorService.submit method.
       */
    class CrawlerRunnable implements Runnable {

        MultithreadCrawler crawler;

        String startUrl;

        public CrawlerRunnable(MultithreadCrawler crawler, String startUrl) {
            this.crawler = crawler;
            this.startUrl = startUrl;

        }

        @Override
        public void run() {
            try {
                if (!visited.contains(this.startUrl)) {
                    visited.add(this.startUrl);
                    String url = this.startUrl;
                    Document doc = Jsoup.connect(url).get();
                    String[] words = doc.select("p").text().split(" ");
                    String[] links = doc.select("a").text().split(" ");
                    for (String link : links) {
                        String extLink = "https://api.interactions.ics.unisg.ch/hypermedia-environment/" + link;
                        if (!visited.contains(extLink)) {
                            executorService.submit(new CrawlerRunnable(crawler, extLink));
                        }
                    }
                    String[] line = new String[4];
                    line[0] = "/" + this.startUrl.substring(61);
                    line[1] = words[0].trim();
                    line[2] = words[1].trim();
                    line[3] = words[2].trim();
                    lines.add(line);
                }
            } catch (IOException e) {
                System.out.println("Error when connecting to url: " + e.getMessage());
            }

        }
    }
}