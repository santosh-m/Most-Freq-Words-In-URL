package com.freqword;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {

	//This map keeps track of words & their occurrence
	static Map<String, Integer> wordCountMap = new HashMap();
	static Map<String, Integer> resultCountMap = new HashMap();
	//This set is used to keep track of unique URLs visited 
	static Set<String> visitedUrlSet = new HashSet<String>();
	static String baseUrl = "";

	public static void main(String[] args) {
		try {
			//Take URL from command line  
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String inputUrl = reader.readLine();
			setBaseUrl(inputUrl);
			System.out.println("Computing Most Frequent Words from input URL >> " + baseUrl + " <<  ...");

			//Capture first visiting URL
			isUrlNotVisited(baseUrl);
			wordCountMap = addWordsFromUrl(baseUrl);
			//Start web crawling from here - Use DFS algorithm & limit depth of recursive calls to a given limit
			recursiveCrawlFromUrl(baseUrl, 1);

			//Sort "Words -> occurrence" mapping by decreasing order of occurrence
			wordCountMap = sortByValue(wordCountMap);
			//Consider only top 10 results from the actual result
			limitMapSize(wordCountMap, 10);
			resultCountMap = sortByValue(resultCountMap);
			printKeyValPair(resultCountMap);
		} catch (IOException e) {
			System.out.println("Something Went Wrong !! Exception msg : " + e.getMessage());
		}
	}

	private static void printKeyValPair(Map<String, Integer> resultCountMap) {
		System.out.println("***** Top 10 frequent words *****\n");
		for (Map.Entry<String, Integer> entry : resultCountMap.entrySet()) {
			System.out.println("Word : " + entry.getKey() + ", Occurrence :  " + entry.getValue());
		}
	}

	private static void setBaseUrl(String inputStr) {
		baseUrl = inputStr;
	}

	private static void limitMapSize(Map<String, Integer> wordCountMap, int limit) {
		int count = 0;
		for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
			if (count < limit) {
				resultCountMap.put(entry.getKey(), entry.getValue());
			} else {
				return;
			}
			count++;
		}
	}

	/**
	 * This method considers text from a URL, splits into words & stores occurrence in a in-memory map.
	 * @param url
	 * @return
	 */
	private static Map<String, Integer> addWordsFromUrl(String url) {
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			String text = doc.body().text();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] words = line.split("[^A-ZÃƒâ€¦Ãƒâ€žÃƒâ€“a-zÃƒÂ¥ÃƒÂ¤ÃƒÂ¶]+");
				for (String word : words) {
					wordCountMap.put(word, 1 + wordCountMap.getOrDefault(word, 0));
				}
			}
			reader.close();
			return wordCountMap;
		} catch (HttpStatusException httpE) {
			System.out.println("HttpStatusException occured : " + httpE.getMessage());
			return wordCountMap;
		} catch (Exception e) {
			System.out.println("Exception occured : " + e.getMessage());
			return wordCountMap;
		}
	}

	/**
	 * This method recursively crawls URLs within the URLs up-to a defined depth. Considers words in nested URLs for their occurrence
	 * @param url
	 * @param recurDepth
	 * @throws IOException
	 */
	public static void recursiveCrawlFromUrl(String url, int recurDepth) throws IOException {
		if (recurDepth > 4) {
			return;
		}
		try {
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				String subUrl = link.attr("href");
				if (subUrl.startsWith(baseUrl) && isUrlNotVisited(subUrl)) {
					addWordsFromUrl(subUrl);
					recurDepth++;
					recursiveCrawlFromUrl(subUrl, recurDepth);
					recurDepth--;
				}
			}
		} catch (HttpStatusException httpE) {
			System.out.println("HttpStatusException " + httpE.getMessage());
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
		}
	}
	/**
	 * This method checks if a URL is already visited. If not stores the new URL.
	 * @param url
	 * @return
	 */
	private static boolean isUrlNotVisited(String url) {
		if (visitedUrlSet.contains(url)) {
			return false;
		} else {
			visitedUrlSet.add(url);
			return true;
		}
	}

	public static HashMap<String, Integer> sortByValue(Map<String, Integer> hm) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());
		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
}
