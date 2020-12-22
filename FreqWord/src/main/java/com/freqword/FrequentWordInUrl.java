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

public class FrequentWordInUrl {
	//This map keeps track of words & their occurrence
	private  Map<String, Integer> wordCountMap;
	//This map keeps track of final top 10 words & their occurrence
	private  Map<String, Integer> topKWordCountMap;
	//This set is used to keep track of unique URLs visited 
	private  Set<String> visitedUrlSet;
	
	private String baseUrl;
	private int maxDepth;
	private static final int INITIAL_DEPTH = 1;
	private static final int RESULT_LIMIT = 10;
	private static final int MAX_DEPTH = 4;
	
	/**
	 * Constructor - Initializes data structures
	 */
	public FrequentWordInUrl() {
		wordCountMap = new HashMap<String, Integer>();
		visitedUrlSet = new HashSet<String>();
	}
	
	/* Getters & Setters */
	public Map<String, Integer> getWordCountMap() {
		return wordCountMap;
	}
	public void setWordCountMap(Map<String, Integer> wordCountMap) {
		this.wordCountMap = wordCountMap;
	}
	public Map<String, Integer> getTopKWordCountMap() {
		return topKWordCountMap;
	}
	public void setTopKWordCountMap(Map<String, Integer> topKWordCountMap) {
		this.topKWordCountMap = topKWordCountMap;
	}
	public Set<String> getVisitedUrlSet() {
		return visitedUrlSet;
	}
	public void setVisitedUrlSet(Set<String> visitedUrlSet) {
		this.visitedUrlSet = visitedUrlSet;
	}
	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public static void main(String[] args) {
		FrequentWordInUrl instance = new FrequentWordInUrl();
		instance.findMostFrequentWords();
	}
	
	public void findMostFrequentWords() {
		readUrlFromCommandLine();
		setMaxDepth(MAX_DEPTH);
		processUrl();
	}

	public void processUrl() {
		System.out.println("Computing Most Frequent Words from input URL *** " + baseUrl + " ***  ...");
		//Mark frontier URL as visited & process further
		if(isUrlNotVisited(baseUrl)) {
			processWordsFromUrl(baseUrl);
			crawlUrlInRecursive(baseUrl, INITIAL_DEPTH);
			Map<String, Integer> sortedWordCountMap = sortByMapValueInDescOrder(wordCountMap);
			topKWordCountMap = limitMapSize(sortedWordCountMap, RESULT_LIMIT);
			printKeyValuePair(topKWordCountMap);
		}
	}

	/**
	 * Read URL from command line input
	 */
	private void readUrlFromCommandLine(){
		String inputUrl = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			inputUrl = reader.readLine();
		} catch (IOException e) {
			System.out.println("Something Went Wrong !! Exception msg : " + e.getMessage());
		}
		setBaseUrl(inputUrl);
	}

	public void setBaseUrl(String inputStr) {
		baseUrl = inputStr;
	}

	/**
	 * This method considers text from a URL, splits into words & stores occurrence in a in-memory map.
	 * @param url
	 */
	private void processWordsFromUrl(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			String text = doc.body().text();
			addWordsIntoMap(text);
		} catch (HttpStatusException httpExcep) {
			System.out.println("HttpStatusException occured while processing"
					+ " url " +url+ " in processWordsFromUrl API, exception msg : " + httpExcep.getMessage());
		} catch (IOException ioExcep) {
			System.out.println("IOException occured in processWordsFromUrl, exception msg : " + ioExcep.getMessage());
		}
	}

	private void addWordsIntoMap(String text) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				String[] words = line.split("[^A-ZÃƒâ€¦Ãƒâ€žÃƒâ€“a-zÃƒÂ¥ÃƒÂ¤ÃƒÂ¶]+");
				for (String word : words) {
					wordCountMap.put(word, 1 + wordCountMap.getOrDefault(word, 0));
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("IOException occured : " + e.getMessage());
		}
	}

	/**
	 * This method recursively crawls URLs up-to a defined depth using DFS algorithm. Considers words in nested URLs for processing their occurrence
	 * @param url
	 * @param recurDepth
	 */
	private void crawlUrlInRecursive(String url, int recurDepth){
		if (recurDepth >= maxDepth) {
			return;
		}
		try {
			Document doc = Jsoup.connect(url).get();
			Elements linksOnPage = doc.select("a[href]");
			for (Element link : linksOnPage) {
				String subUrl = link.attr("href");
				if (subUrl.startsWith(baseUrl) && isUrlNotVisited(subUrl)) {
					processWordsFromUrl(subUrl);
					recurDepth++;
					crawlUrlInRecursive(subUrl, recurDepth);
					recurDepth--;
				}
			}
		} catch (HttpStatusException httpE) {
			System.out.println("HttpStatusException occurred in crawlUrlInRecursive, exception msg : " + httpE.getMessage());
		} catch (IOException e) {
			System.out.println("IOException occurred in crawlUrlInRecursive, exception msg : " + e.getMessage());
		}
	}
	
	/**
	 * This method checks if a URL is already visited. If not stores the new URL.
	 * @param url
	 * @return boolean flag isUrlNotVisited
	 */
	private boolean isUrlNotVisited(String url) {
		if (visitedUrlSet.contains(url)) {
			return false;
		} else {
			visitedUrlSet.add(url);
			return true;
		}
	}

	/**
	 * This method sorts "words(key) -> occurrence(value)" mapping by decreasing order of occurrence(value) 
	 * @param wordCountMap
	 * @return Returns sorted HashMap 
	 */
	public HashMap<String, Integer> sortByMapValueInDescOrder(Map<String, Integer> wordCountMap) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(wordCountMap.entrySet());
		// Sorts the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			sortedMap.put(aa.getKey(), aa.getValue());
		}
		return sortedMap;
	}
	
	/**
	 * This method limits only top 10 entries in sorted map
	 * @param wordCountMap
	 * @param limit
	 * @return Returns limited map
	 */
	public Map<String, Integer> limitMapSize(Map<String, Integer> wordCountMap, int limit) {
		int count = 0;
		Map<String, Integer> limitedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
			if (count < limit) {
				limitedMap.put(entry.getKey(), entry.getValue());
			} else {
				break;
			}
			count++;
		}
		return limitedMap;
	}
	
	/**
	 * Prints result of frequent words & their occurrence
	 * @param resultCountMap
	 */
	private void printKeyValuePair(Map<String, Integer> resultCountMap) {
		System.out.println("***** Top " + RESULT_LIMIT + " frequent words *****\n");
		for (Map.Entry<String, Integer> entry : resultCountMap.entrySet()) {
			System.out.println("Word : " + entry.getKey() + ", Occurrence :  " + entry.getValue());
		}
	}
}
