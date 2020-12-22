package com.freqword.FreqWord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.freqword.FrequentWordInUrl;

/**
 * Unit test for Frequent Word In URL Class
 */
public class FrequentWordInUrlTest {

	private FrequentWordInUrl freqWordInstance;
	private static final String VALID_MOCK_URL = "https://en.wikipedia.org/wiki/Main_Page" ;
	private Map<String, Integer> emptyMap;
	private Set<String> emptySet; 
	
	@BeforeEach
	void initEach() {
		freqWordInstance = new FrequentWordInUrl();
		freqWordInstance.setBaseUrl(VALID_MOCK_URL);
		emptyMap = new HashMap();
		emptySet = new HashSet();
	}
	
	@Test
	void testEmptyInputUrl() {
		freqWordInstance.setBaseUrl(" ");
		assertThrows(IllegalArgumentException.class, () -> freqWordInstance.processUrl(), "Empty URL should throw IllegalArgumentException");
	}
	
	@Test
	void testInvalidnputUrl() {
		freqWordInstance.setBaseUrl("testurl123");
		assertThrows(IllegalArgumentException.class, () -> freqWordInstance.processUrl(), "Invalid URL should throw IllegalArgumentException");
	}
	
	@Test
	void testInvalidStatusCodeFromUrl() {
		//Since URL returns 404 error code, HttpStatusException should be caught & word-occurrence map should be empty
		freqWordInstance.setBaseUrl("http://google.com/doesnotexists");
		freqWordInstance.processUrl();
		assertEquals(emptyMap, freqWordInstance.getWordCountMap());
	}
	
	@Test
	void testVisitedUrlsIsNonZeroWithValidUrl() {
		freqWordInstance.processUrl();
		assertNotEquals(emptySet, freqWordInstance.getVisitedUrlSet());
	}
	
	@Test
	void testWordCountIsNotZeroWithValidUrl() {
		freqWordInstance.processUrl();
		assertNotEquals(emptyMap, freqWordInstance.getWordCountMap());
	}
	
	@Test 
	void testSortMapByValueInDescOrderApi() {
		Map<String, Integer> testMap = new HashMap();
		testMap.put("John", 20);
		testMap.put("Joe", 10);
		testMap.put("Smith", 30);
		Map<String, Integer> expectedMap = new LinkedHashMap();
		expectedMap.put("Smith", 30);
		expectedMap.put("John", 20);
		expectedMap.put("Joe", 10);
		
		Map<String, Integer> actualMapFromAPI = freqWordInstance.sortByMapValueInDescOrder(testMap);
		assertTrue(expectedMap.equals(actualMapFromAPI));
	}
	
	@Test 
	void testLimitMapSizeToTopKApi() {
		Map<String, Integer> testMap = new LinkedHashMap();
		testMap.put("Smith", 30);
		testMap.put("John", 20);
		testMap.put("Joe", 10);
		Map<String, Integer> expectedMap = new LinkedHashMap();
		expectedMap.put("Smith", 30);
		expectedMap.put("John", 20);
		Map<String, Integer> actualMapFromAPI = freqWordInstance.limitMapSize(testMap, 2);
		
		assertTrue(expectedMap.equals(actualMapFromAPI));
	}
	
	@Test
	void testWordCountWithValidUrl() {
		freqWordInstance.setBaseUrl("https://www.google.com/intl/en-GB/gmail/about/#");
		freqWordInstance.processUrl();
		Map<String, Integer> mockMap = new LinkedHashMap(); 
		mockMap.put("Gmail" , 15);
		mockMap.put("to" , 11);
		mockMap.put("Get" , 10);
		mockMap.put("you" , 8);
		mockMap.put("an" , 7);
		mockMap.put("Create" , 7);
		mockMap.put("account" , 7);
		mockMap.put("and" , 6);
		mockMap.put("in" , 5);
		mockMap.put("emails" , 5);
		assertEquals(mockMap, freqWordInstance.getTopKWordCountMap());
	}
	
	@Test 
	void testWordCountIsLargerWithHigherDepth() {
		freqWordInstance.setBaseUrl("https://www.314e.com/");
		freqWordInstance.setMaxDepth(1);
		freqWordInstance.processUrl();
		Set<String> keys = freqWordInstance.getTopKWordCountMap().keySet();
		String topFreqWordLowerDepth = "";
		for (String key : keys) {
			topFreqWordLowerDepth = key;
			break;
		}
		int topFreqWordWordCountWithLowerDepth = freqWordInstance.getTopKWordCountMap().get(topFreqWordLowerDepth);
		System.out.println("Top Freq Word with LOWER DEPTH is : "+ topFreqWordLowerDepth+ " Occurrence is : "+topFreqWordWordCountWithLowerDepth);
		
		freqWordInstance.setBaseUrl("https://www.314e.com/"); 
		freqWordInstance.setMaxDepth(2);
		clearInMemoryDS();
		freqWordInstance.processUrl();
		
		Set<String> newKeys = freqWordInstance.getTopKWordCountMap().keySet();
		String topFreqWordHigherDepth = "";
		for (String key : newKeys) {
			topFreqWordHigherDepth = key;
			break;
		}
		int topFreqWordWordCountWithHigherDepth = freqWordInstance.getTopKWordCountMap().get(topFreqWordHigherDepth);
		System.out.println("Top Freq Word with HIGHER DEPTH is : "+ topFreqWordHigherDepth+ " Occurrence is : "+topFreqWordWordCountWithHigherDepth);
		
		assertTrue(topFreqWordWordCountWithHigherDepth > topFreqWordWordCountWithLowerDepth);
	}
	
	@Test 
	void testVisitedUrlCountIsLargerWithHigherDepth() {
		freqWordInstance.setBaseUrl("https://www.314e.com/");
		freqWordInstance.setMaxDepth(1);
		freqWordInstance.processUrl();
		int lowerDepthVisitedUrlCount = freqWordInstance.getVisitedUrlSet().size();
		System.out.println("Visited URLs with lower depth : "+lowerDepthVisitedUrlCount);
		
		freqWordInstance.setBaseUrl("https://www.314e.com/"); 
		freqWordInstance.setMaxDepth(2);
		clearInMemoryDS();
		freqWordInstance.processUrl();
		int higherDepthVisitedUrlCount = freqWordInstance.getVisitedUrlSet().size();
		System.out.println("Visited URLs with larger depth : "+higherDepthVisitedUrlCount);
		
		assertTrue(higherDepthVisitedUrlCount > lowerDepthVisitedUrlCount);
	}

	private void clearInMemoryDS() {
		freqWordInstance.setVisitedUrlSet(new HashSet<String>());
		freqWordInstance.setTopKWordCountMap(new HashMap<String, Integer>());
		freqWordInstance.setWordCountMap(new HashMap<String, Integer>());
	}
	
}
