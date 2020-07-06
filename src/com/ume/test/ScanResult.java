package com.ume.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Represents the result of a word list scan and contains
 */
class ScanResult {
    private final List<Entry<String, Integer>> mostFrequentWords;
    private final List<Entry<String, Integer>> leastFrequentWords;

    ScanResult() {
        mostFrequentWords = new ArrayList<>();
        leastFrequentWords = new ArrayList<>();
    }

    ScanResult(List< Entry<String, Integer>> mostFrequentWords,
            List< Entry<String, Integer>> leastFrequentWords) {

        this.mostFrequentWords = mostFrequentWords;
        this.leastFrequentWords = leastFrequentWords;
    }

    public List<Entry<String, Integer>> getMostFrequentWords() {
        return mostFrequentWords;
    }

    public List<Entry<String, Integer>> getLeastFrequentWords() {
        return leastFrequentWords;
    }
}
