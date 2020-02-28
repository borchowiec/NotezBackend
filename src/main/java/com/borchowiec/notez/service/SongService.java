package com.borchowiec.notez.service;

import com.borchowiec.notez.model.Song;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SongService {
    private final Map<String, Integer> TONES_VALUE;

    private static final String LYRICS_SIGN = "$ly$";
    private static final String CHORDS_SIGN = "$ch$";

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
    }

    /**
     * <p>This method converse text to html. Removes lines signs and chords signs. Replaces tones to specific span html
     * element. Thanks to it, it will be easy to change music key, because every span has info about it's tone.</p>
     *
     * before: <code>C#</code> <br />
     * after: < span class="t1 tone"></span> <br />
     *
     * < span class="t<code>(tone value)</code> tone><span>
     * @param text Text that will be converted to html.
     * @return Text with replaced tones to html elements.
     */
    public String textToHtml(String text) {
        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String readyLine;

            if (line.indexOf(LYRICS_SIGN) == 0) { // lyrics line
                readyLine = line.replace(LYRICS_SIGN, ""); // remove lyrics sign at the beginning
                result.append(readyLine);
                if (i != lines.length - 1) {
                    result.append('\n'); // add sign of new line if it's not a last line
                }
            } else if (line.indexOf(CHORDS_SIGN) == 0) { // chords line
                readyLine = line.replace(CHORDS_SIGN, "");
                for (Map.Entry<String, Integer> entry : TONES_VALUE.entrySet()) {
                    // replace all tones to html span with css class that represents tone
                    readyLine = readyLine.replaceAll(entry.getKey(),
                            "<span class=\"t" + entry.getValue() + " tone\"></span>");
                }
                result.append(readyLine);
                if (i != lines.length - 1) { // add sign of new line if it's not a last line
                    result.append('\n');
                }
            }
            // skip line without lyrics sign and chords sign at the beginning
        }

        return result.toString();
    }

    /**
     * Takes two lists and combine them, without duplicates. Final list won't be bigger than limit.
     * @param first First list.
     * @param second Second list.
     * @param limit Limit of size of final list.
     * @return Combined lists, without duplicates, not bigger than limit of size.
     */
    public List<Song> combineTwoListsWithoutDuplicatesAndWithSizeLimit(List<Song> first, List<Song> second, int limit) {

        // if first list is bigger than limit, return trimmed first list
        if (first.size() > limit) {
            return first.subList(0, limit);
        }

        for (Song obj : second) {
            if (first.size() >= limit) {
                break;
            }
            if (!first.contains(obj)) {
                first.add(obj);
            }
        }
        return first;
    }

    public Song incrementViews(Song song) {
        int views = song.getViews() + 1;
        song.setViews(views);
        return song;
    }
}