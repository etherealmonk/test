package com.ume.test;

import static java.lang.Math.floor;
import static java.lang.String.format;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;

class WordFrequencyCalculatingFileScanner {

    private static final String NON_ALPHA_CHARACTERS = "[.,;:*_|\\-'\"#!?()\\[\\]/]+";

    private int uniqueWordsCount;
    private int mostFrequentWordsLimit;
    private List<Entry<String, Integer>> wordFrequencyList;
    private List<Entry<String, Integer>> mostFrequentWords;
    private List<Entry<String, Integer>> leastFrequentWords;
    private Comparator<Entry<String, Integer>> entryComparator;
    private float mostFrequentPercentage;
    private URL url;

    WordFrequencyCalculatingFileScanner(String url, float mostFrequentPercentage) {
        if (url == null) {
            throw new IllegalArgumentException("You need to provide a valid URL!");
        }
        this.url = createURL(url);
        if (this.url == null) {
            throw new IllegalArgumentException("You need to provide a valid URL!");
        }
        this.mostFrequentPercentage = mostFrequentPercentage;
    }

    private URL createURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    ScanResult scan() {
        final File file;
        try {
            file = download();
        } catch (IOException e) {
            System.err.println("Unable to download file from URL: " + this.url);
            return new ScanResult();
        }
        Map<String, Integer> wordFrequencies = new HashMap<>();
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("File not found. Aborting.");
            return new ScanResult();
        }

        int words = 0;
        while(sc.hasNext()) {
            final String word = sc.next().trim().toLowerCase().replaceAll("^" + NON_ALPHA_CHARACTERS, "").replaceAll(NON_ALPHA_CHARACTERS + "$", "");
            wordFrequencies.merge(word, 1, Integer::sum);
            words++;
        }
        System.out.println("Words: " + words);

        sc.close();
        // clean-up
        file.deleteOnExit();

        final Set<Entry<String, Integer>> entries = wordFrequencies.entrySet();
        wordFrequencyList = new ArrayList<>(entries);
        uniqueWordsCount = wordFrequencyList.size();
        System.out.println("Unique words: " + uniqueWordsCount);
        mostFrequentWordsLimit = (int) floor(uniqueWordsCount * mostFrequentPercentage);
        System.out.println("Unique words limit: " + mostFrequentWordsLimit);
        final Comparator<Entry<String, Integer>> comparator = comparingInt(Entry::getValue);
        entryComparator = comparator.thenComparing(Entry::getKey).reversed();
        wordFrequencyList.sort(entryComparator);

        mostFrequentWords = wordFrequencyList.subList(0, mostFrequentWordsLimit);
        System.out.println("Most frequent: " + mostFrequentWords);
        leastFrequentWords = wordFrequencyList.subList(uniqueWordsCount - mostFrequentWordsLimit - 1, uniqueWordsCount - 1);
        System.out.println("Least frequent: " + leastFrequentWords);

        return new ScanResult(mostFrequentWords, leastFrequentWords);
    }

    private File download() throws IOException {
        System.out.println("Downloading: " + this.url);
        ReadableByteChannel readableByteChannel = Channels.newChannel(this.url.openStream());
        final String fileName = "wordList-temporaryFile-" + System.currentTimeMillis();
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        System.out.println("Finished downloading to: " + fileName);
        return new File(fileName);
    }

    /**
     * Computes the result of a comparison between two word lists (as Scanners)
     * @param secondScanner
     * @return
     */
    public ScannerComparisonResult determineCommonAndUniqueWords(WordFrequencyCalculatingFileScanner secondScanner) {
        System.out.println("Determining comparison results for two word lists...");
        if (this.wordFrequencyList == null) {
            return new ScannerComparisonResult();
        }
        final ArrayList<Entry<String, Integer>> secondScannerWordList = new ArrayList<>(secondScanner.wordFrequencyList);
        secondScannerWordList.sort(Entry.comparingByKey());
        final ArrayList<Entry<String, Integer>> firstScannerWordList = new ArrayList<>(wordFrequencyList);
        firstScannerWordList.sort(Entry.comparingByKey());
        final List<Entry<String, Integer>> commonWordsList = firstScannerWordList.stream()
                                                                     .filter(entry -> secondScannerWordList.stream()
                                                                                             .anyMatch(getEntryPredicate(entry)))
                                                                     .sorted(entryComparator).collect(toList());
        System.out.println(format("Common words (%d): %s", commonWordsList.size(), commonWordsList));
        int mostFrequentCommonWordsLimit = (int) floor(commonWordsList.size() * mostFrequentPercentage);
        List<Entry<String, Integer>> mostFrequentCommonWords = commonWordsList.subList(0, mostFrequentCommonWordsLimit);
        System.out.println(format( "Most frequent common words (%d): %s", mostFrequentCommonWordsLimit, mostFrequentCommonWords));
        final List<Entry<String, Integer>> wordsUniqueToFirstScanner = getUniqueWords(firstScannerWordList, commonWordsList);
        final List<Entry<String, Integer>> wordsUniqueToSecondScanner = getUniqueWords(secondScannerWordList, commonWordsList);
        System.out.println(format("Words unique to the first list (%d): %s", wordsUniqueToFirstScanner.size(), wordsUniqueToFirstScanner));
        System.out.println(format("Words unique to the second list (%d): %s", wordsUniqueToSecondScanner.size(), wordsUniqueToSecondScanner));
        return new ScannerComparisonResult(commonWordsList, mostFrequentCommonWords, wordsUniqueToFirstScanner, wordsUniqueToSecondScanner);
    }

    /**
     * Returns the list of entries which are unique to the <code>wordList</code> (i.e not present in the commonWordsList).
     * The comparison is done by key and the value is ignored.
     * @param wordList
     * @param commonWordsList
     * @return
     */
    private List<Entry<String, Integer>> getUniqueWords(ArrayList<Entry<String, Integer>> wordList,
            List<Entry<String, Integer>> commonWordsList) {
        return wordList.stream()
                       .filter(entry -> commonWordsList.stream()
                                                        .noneMatch(getEntryPredicate(entry)))
                       .collect(toList());
    }

    /**
     * Returns a predicate thet is true when the two entries have the same key (ignoreCase).
     * @param entry
     * @return
     */
    private Predicate<Entry<String, Integer>> getEntryPredicate(Entry<String, Integer> entry) {
        return entry2 -> entry
                .getKey()
                .equalsIgnoreCase(
                        entry2.getKey());
    }
}
