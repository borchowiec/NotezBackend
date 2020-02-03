package com.borchowiec.notez.service;

import java.util.LinkedHashMap;
import java.util.Map;

public class SongService {
    private final Map<String, Integer> TONES_VALUE;
    private final String[] VALUE_TONES;

    private final String LYRICS_SIGN = "$ly$";
    private final String CHORDS_SIGN = "$ch$";

    public SongService() {
        TONES_VALUE = new LinkedHashMap<>();
        TONES_VALUE.put("C#", 1);
        TONES_VALUE.put("Db", 1);
        TONES_VALUE.put("D#", 3);
        TONES_VALUE.put("Eb", 3);
        TONES_VALUE.put("F#", 6);
        TONES_VALUE.put("Gb", 6);
        TONES_VALUE.put("G#", 8);
        TONES_VALUE.put("Ab", 8);
        TONES_VALUE.put("A#", 10);
        TONES_VALUE.put("Bb", 10);
        TONES_VALUE.put("C", 0);
        TONES_VALUE.put("D", 2);
        TONES_VALUE.put("E", 4);
        TONES_VALUE.put("F", 5);
        TONES_VALUE.put("G", 7);
        TONES_VALUE.put("A", 9);
        TONES_VALUE.put("B", 11);
        VALUE_TONES = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    }

    public String textToHtml(String text) {
        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String l = lines[i];
            String readyLine;

            if (l.indexOf(LYRICS_SIGN) == 0) { // lyrics line
                readyLine = l.replace(LYRICS_SIGN, ""); // replace lyrics sign at the beginning
                result.append(readyLine);
                if (i != lines.length - 1) {
                    result.append('\n'); // add sign of new line if it's not a last line
                }
            } else if (l.indexOf(CHORDS_SIGN) == 0) { // chords line
                readyLine = l.replace(CHORDS_SIGN, "");
                for (Map.Entry<String, Integer> entry : TONES_VALUE.entrySet()) { // replace all tones to html span
                    readyLine = readyLine.replaceAll(entry.getKey(),
                            "<span class=\"t" + entry.getValue() + " tone\"></span>");
                }
                result.append(readyLine);
                if (i != lines.length - 1) { // add sign of new line if it's not a last line
                    result.append('\n');
                }
            }
        }

        return result.toString();
    }
}