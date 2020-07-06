package com.ume.test;

public class Main {

    private static final float mostFrequentPercentage = 0.05f;

    public static void main(String[] args) {
        String firstUrl, secondUrl;
        if (args == null || args.length == 0) {
            System.out.println("Demo mode. Using default URLs.");
            firstUrl = "https://ocw.mit.edu/ans7870/6/6.006/s08/lecturenotes/files/t8.shakespeare.txt";
            secondUrl = "https://norvig.com/big.txt";
        } else if (args.length == 2) {
            firstUrl = args[0];
            secondUrl = args[1];
        } else {
            System.out.println("Usage: Main url1 url2");
            return;
        }

        final WordFrequencyCalculatingFileScanner firstScanner = new WordFrequencyCalculatingFileScanner(
                firstUrl, mostFrequentPercentage);
        firstScanner.scan();
        final WordFrequencyCalculatingFileScanner secondScanner = new WordFrequencyCalculatingFileScanner(
                secondUrl, mostFrequentPercentage);
        secondScanner.scan();

        firstScanner.determineCommonAndUniqueWords(secondScanner);

        System.out.println("Done");
    }
}
