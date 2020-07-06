package com.ume.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the results of the comparison between two {@link WordFrequencyCalculatingFileScanner} word lists.
 * It will contain the list of common words, the lists of words that are unique in both scanner (i.e not common)
 * and the most frequent common words.
 */
class ScannerComparisonResult {

    private final List<Map.Entry<String, Integer>> commonWordsList;
    private final List<Map.Entry<String, Integer>> mostFrequentCommonWords;
    private final List<Map.Entry<String, Integer>> wordsUniqueToFirstScanner;
    private final List<Map.Entry<String, Integer>> wordsUniqueToSecondScanner;

    ScannerComparisonResult() {
        commonWordsList = new ArrayList<>();
        mostFrequentCommonWords = new ArrayList<>();
        wordsUniqueToFirstScanner = new ArrayList<>();
        wordsUniqueToSecondScanner = new ArrayList<>();
    }

    ScannerComparisonResult(List<Map.Entry<String, Integer>> commonWordsList,
            List<Map.Entry<String, Integer>> mostFrequentCommonWords,
            List<Map.Entry<String, Integer>> wordsUniqueToFirstScanner,
            List<Map.Entry<String, Integer>> wordsUniqueToSecondScanner) {

        this.commonWordsList = commonWordsList;
        this.mostFrequentCommonWords = mostFrequentCommonWords;
        this.wordsUniqueToFirstScanner = wordsUniqueToFirstScanner;
        this.wordsUniqueToSecondScanner = wordsUniqueToSecondScanner;
    }


    public List<Map.Entry<String, Integer>> getCommonWordsList() {
        return commonWordsList;
    }

    public List<Map.Entry<String, Integer>> getMostFrequentCommonWords() {
        return mostFrequentCommonWords;
    }

    public List<Map.Entry<String, Integer>> getWordsUniqueToFirstScanner() {
        return wordsUniqueToFirstScanner;
    }

    public List<Map.Entry<String, Integer>> getWordsUniqueToSecondScanner() {
        return wordsUniqueToSecondScanner;
    }
}
