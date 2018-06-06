package cs310.fidgetspinners.grubmate;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PimDewitt 11/5/2017.
 * Original Source: https://gist.github.com/PimDeWitte/c04cc17bc5fa9d7e3aee6670d4105941
 */

public class ProfanityFilter{

    static int largestWordLength = 0;
    static Map<String, String[]> words = new HashMap<>();
    String line = "fuck,shit,ass,cunt,bitch,motherfucker,whore,dick";
    final List<String> swearWords = Arrays.asList(line.split(","));

    /**
     * Iterates over a String input and checks whether a cuss word was found in a list, then checks if the word should be ignored (e.g. bass contains the word *ss).
     * @param input
     * @return
     */
    public ArrayList<String> badWordsFound(String input) {
        if(input == null) {
            return new ArrayList<>();
        }

        ArrayList<String> matches = new ArrayList<String>();

        // remove leetspeak
        input = input.replaceAll("1","i");
        input = input.replaceAll("!","i");
        input = input.replaceAll("3","e");
        input = input.replaceAll("4","a");
        input = input.replaceAll("@","a");
        input = input.replaceAll("5","s");
        input = input.replaceAll("7","t");
        input = input.replaceAll("0","o");
        input = input.replaceAll("9","g");

        for (int i=0; i < swearWords.size(); i++) {
            if (input.contains(swearWords.get(i))) {
                matches.add(input);
            }
        }

        return matches;
    }

}
