package com.test.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	private static Set<String> links;
	private static Set<String> images;
	public static final String FILE_NAME = "recordWebCrawling.txt";


	 String domain = "wiprodigital.com"; 
	 String protocol="https://";
	 
	public WebCrawler() {
		links = new HashSet<String>();
		images = new HashSet<String>();
	}


	public void processPage(String URL) {
		String loc = System.getProperty("user.dir") + File.separator + "recordWebCrawling.txt";

		if (null != links && !links.contains(URL)) {
			if (URL.startsWith(protocol + domain)) {

				links.add(URL);
				Document doc = null;
				try {
					doc = Jsoup.connect(URL)
							.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
							.referrer("http://www.google.com").ignoreHttpErrors(true).get();

				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
				Elements allLinks = doc.select("a[href]");
				Elements imageLinks = doc.select("img[src]");
				if (imageLinks != null && !imageLinks.isEmpty()) {
					for (Element link : imageLinks) {
						String imageURL = link.attr("abs:src");
						images.add(imageURL);
					}
				}
				if (allLinks != null && !allLinks.isEmpty()) {
					for (Element link : allLinks) {
						processPage(link.attr("abs:href"));
					}
				}
			} else {
				return;
			}
		}
	}

	

	public static void main(String[] args) {
		WebCrawler crawlerObj = new WebCrawler();
		System.out.println("Start Crawling...");
		String url = "https://wiprodigital.com";

		crawlerObj.processPage(url);
		System.out.println("End Crawling.");
		try {
			crawlerObj.writeIntoFile();
		} catch (IOException e) {
			System.out.println("Got error while writing in file !!!");
		}
	}


	private void writeIntoFile() throws IOException {
		String loc = System.getProperty("user.dir") + File.separator + "WebCrawlingData.txt";
		System.out.println("Start writing cwarl data in File : "+ loc);
		try (PrintStream out = new PrintStream(new FileOutputStream(loc))) {
			for (String link : links) {
				out.println(link);
			}
			for (String imgLink : images) {
				out.println(imgLink);
			}
		}
		System.out.println("End of file writing.");
	}

}
