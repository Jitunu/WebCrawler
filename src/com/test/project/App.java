package com.test.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jsoup.HttpStatusException;
//Jsoup Imports
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 *
 */
public class App {
	private Set<String> links;
	private Set<String> images;
	String domain = "wiprodigital.com";
	String protocol = "https://";

	public App() {
		links = new HashSet<String>();
		images = new HashSet<String>();
	}

	public void processPage(String URL) throws IOException {
		// File dir = new File(".");
		String loc = System.getProperty("user.dir") + File.separator + "recordWebCrawling.txt";
		// check for other sites

		System.out.println(URL);
		File file = new File(loc);
		if (null != links && !links.contains(URL)) {
			if (URL.startsWith(protocol + domain)) {

				links.add(URL);
				// System.out.println("------ : " + URL);
				try (FileWriter fstream = new FileWriter(loc, true)) {
					BufferedWriter out = new BufferedWriter(fstream);

					if (!checkDuplicateEntry(URL, file)) {
						out.write(URL);
						out.newLine();
					}
				}
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

	private void processImages() throws IOException {
		File dir = new File(".");
		String loc = dir.getCanonicalPath() + File.separator + "recordWebCrawling.txt";
		try (FileWriter fstream = new FileWriter(loc, true)) {
			BufferedWriter out = new BufferedWriter(fstream);
			Iterator<String> imagesList = images.iterator();
			File file = new File(loc);
			while (imagesList != null && imagesList.hasNext()) {
				String imageURL = imagesList.next();
				if (!checkDuplicateEntry(imageURL, file)) {
					out.write(imageURL);
					out.newLine();
				}
				// System.out.println("Images..."+imageURL);
			}
		}
	}

	private Boolean checkDuplicateEntry(String next, File file) throws IOException {

		try (FileInputStream fis = new FileInputStream(file)) {
			BufferedReader in = new BufferedReader(new InputStreamReader(fis));

			String aLine = null;
			while ((aLine = in.readLine()) != null) {
				if (aLine.trim().contains(next)) {
					return true;
				}
			}

			return false;
		}
	}

	public static void main(String[] args) {
		App crawlerObj = new App();
		String Url = "https://wiprodigital.com";
		try {
			crawlerObj.processPage(Url);
			crawlerObj.processImages();

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (HttpStatusException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// remove if you don't want to see in console
		System.out.println("All links saved in recordWebCrawling file");
		System.out.println("No Of links-" + crawlerObj.links.size());
		System.out.println("No of images-" + crawlerObj.images.size());
	}
}