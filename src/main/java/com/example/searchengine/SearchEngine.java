package com.example.searchengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;




@RestController
public class SearchEngine {

	public final String indexFileName = "./src/main/resources/index.csv";

	public final String flippedIndexFileName = "./src/main/resources/index_flipped.csv";

	public final String startUrl = "https://api.interactions.ics.unisg.ch/hypermedia-environment/cc2247b79ac48af0";

	@Autowired
	Searcher searcher;

	@Autowired
	IndexFlipper indexFlipper;

	@Autowired
	SearchEngineProperties properties;

	Crawler crawler;

	@PostConstruct
	public void initialize(){
		if (properties.getCrawler().equals("multithread")){
			this.crawler = new MultithreadCrawler(indexFileName);
		} else {
			this.crawler = new SimpleCrawler(indexFileName);
		}
		if (properties.getCrawl()) {
			crawler.crawl(startUrl);
			indexFlipper.flipIndex(indexFileName, flippedIndexFileName);
		}
	}
	@GetMapping("/search")
	public String search(@RequestParam("q") String query) throws IOException {
		List<String> urls = searcher.search(query, flippedIndexFileName);
		if (urls.size() == 0){
			return "No results found";
		}
		String html = "<html>\n" +
				"    <head>\n" +
				"        <title>Poogle results</title>\n" +
				"        <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" +
				"        <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" +
				"        <link href=\"https://fonts.googleapis.com/css2?family=Roboto:wght@900&display=swap\" rel=\"stylesheet\">\n" +
				"        <style>\n" +
				"            a{\n" +
				"                color: rgb(1, 68, 105);\n" +
				"                font-family: 'Roboto', sans-serif;\n" +
				"            }\n" +
				"            h1{\n" +
				"                margin-top: 50px;\n" +
				"                margin-left: 20px;\n" +
				"                margin-bottom: 50px;\n" +
				"                color:  rgb(80, 174, 214);\n" +
				"                font-family: 'Roboto', sans-serif;\n" +
				"            }\n" +
				"        </style>\n" +
				"    </head>\n" +
				"    <body>\n" +
				"        <h1>Results:</h1>";
		for (String url : urls){
			html += "<a href=\"" + url + "\">"+ "&#9679; " + url + "</a><br><br>";
		}
		html += "</body></html>";
		return html;
	}

	public static String getUrl(List<String> urls) {
		if (urls.size() > 0){
			return urls.get(0);
		} else {
			return "http://localhost/";
		}
	}
	@GetMapping("/lucky")
	public ResponseEntity<String> lucky(@RequestParam("q") String query){
		List<String> urls = searcher.search(query, flippedIndexFileName);
		HttpHeaders headerMap = new HttpHeaders();
		headerMap.put("Location", Collections.singletonList(getUrl(urls)));
		return new ResponseEntity<>(headerMap, org.springframework.http.HttpStatus.FOUND);
	}
	@GetMapping("/")
	public String home() throws IOException {
		Resource resource = new ClassPathResource("static/index.html");

		if (resource.exists()) {
			byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());
			return new String(fileContent);
		} else {
			return "HTML file not found.";
		}
	}

}
