package md2html;

import java.io.*;
import java.lang.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Scanner;

public class Md2Html {
    public static void main(String[] args) {
        try (FileReader input = new FileReader(args[0], StandardCharsets.UTF_8)) {
            Scanner scanner = new Scanner(input);

            StringBuilder now = new StringBuilder(); // Text in now paragraph

            try (FileWriter writer = new FileWriter(args[1], StandardCharsets.UTF_8)) {
                while (scanner.hasNextLine()) {
                    String s = scanner.nextLine(); // Read a new line from input file

                    // Add new non-empty string in current paragraph
                    if (!s.equals("")) {
                        now.append(s + System.lineSeparator());

                        //Check is this string end of file, if so, we need to process it now
                        if (scanner.hasNextLine()) {
                            continue;
                        }
                    }

                    //This is block of empty strings in input file, we should ignore it
                    if (now.length() == 0 && scanner.hasNextLine()) {
                        continue;
                    }

                    StringBuilder answer = toHtml(now);

                    writer.write(answer.toString());
                    writer.write(System.lineSeparator());

                    now = new StringBuilder(); // Zeroed out now for new paragraph
                }
            } catch (IOException e) {
                System.out.println("File Writing Error " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("File Reading Error " + e.getMessage());
        }
    }

    //This map contains specials symbols in Markdown and their translations to HTML
    private static final Map<Character, String> SPECIAL_SYMBOLS = Map.of(
            '<', "&lt;",
            '>', "&gt;",
            '&', "&amp;"
    );

    //This map contains flag for every teg, is this teg open, or is it close.

    private static Map isTegOpen = new HashMap<String, Boolean>();

    private static final Set<Character> SINGLE_TAGS = new HashSet(Arrays.asList('*', '_', '\''));

    private static final Map<String, String> TEG_OPEN = Map.of(
            "*", "<em>",
            "**", "<strong>",
            "_", "<em>",
            "__", "<strong>",
            "--", "<s>",
            "`", "<code>",
            "''", "<q>"
    );

    private static final Map<String, String> TEG_CLOSE = Map.of(
            "*", "</em>",
            "**", "</strong>",
            "_", "</em>",
            "__", "</strong>",
            "--", "</s>",
            "`", "</code>",
            "''", "</q>"
    );

    private static StringBuilder convert(StringBuilder s) {
        StringBuilder ans = new StringBuilder();
        StringBuilder openTeg = new StringBuilder();
        StringBuilder closeTeg = new StringBuilder();

        //Fill mutable map isTegOpen. This map contains flag is this teg open.
        fillIsTegOpen();

        //Put correct tags to openTeg and closeTeg
        determinateTeg(s, openTeg, closeTeg);

        ans.append(openTeg);

        Map<String, Integer> qStarsUnderscores = new HashMap<>();

        //Count quantity single '*' (without neighbor '*')
        qStarsUnderscores.put("*", countSingleChars(s, '*'));

        //Count quantity single '_' (without neighbor '_')
        qStarsUnderscores.put("_", countSingleChars(s, '_'));

        for (int i = 0; i < s.length(); i++) {
            //Check and write special screen symbols
            if (SPECIAL_SYMBOLS.containsKey(s.charAt(i))) {
                ans.append(SPECIAL_SYMBOLS.get(s.charAt(i)));
                continue;
            }

            //Process single chars '*', '_', '`'
            if ((s.charAt(i) == '*' || s.charAt(i) == '_') && isSingleCharacter(s, i, s.charAt(i))) {
                String nowTeg = Character.toString(s.charAt(i));

                int qElements = qStarsUnderscores.get(nowTeg);

                if (qElements == 1 && (isTegOpen.get(nowTeg).equals(false))) {
                    ans.append(s.charAt(i));
                } else {
                    changeTeg(nowTeg, ans);
                }

                qStarsUnderscores.put(nowTeg, qElements - 1);
                continue;
            }

            if (s.charAt(i) == '`') {
                String nowTeg = "`";
                changeTeg(nowTeg, ans);
                continue;
            }

            //Process double symbols tags "**", "__", "--", "\'"
            char[] doubleSymbols = {'*', '_', '-', '\''};

            int sum = checkSum(s, ans, i, doubleSymbols);

            //Sum > 0 say, that we find need char, and we need to do 'continue'.
            if (sum > 0) {
                i += 1;
                continue;
            }

            //Check and write special screen '*', '_' and '\''
            if (showedSingleTag(s, i)) {
                ans.append(s.charAt(i + 1));
                i += 1;
                continue;
            }

            //Text symbols
            ans.append(s.charAt(i));
        }
        ans.append(closeTeg);
        return ans;
    }

    private static int checkSum(StringBuilder s, StringBuilder ans, int i, char[] doubleSymbols) {
        int answer = 0;

        for (int ind = 0; ind < doubleSymbols.length; ind++) {
            answer += check(s, ans, i, doubleSymbols[ind]);
        }
        return answer;
    }

    // This method checks is s[pos] symbol is single (not twice, and not screen showed)
    private static boolean isSingleCharacter(StringBuilder s, int pos, char c) {
        return notEqualChar(s, pos - 1, c)
                && notEqualChar(s, pos + 1, c)
                && notEqualChar(s, pos - 1, '\\');
    }

    private static int countSingleChars(StringBuilder s, char c) {
        int ans = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c && isSingleCharacter(s, i, c)) {
                ans++;
            }
        }

        return ans;
    }

    /*
      Checks and works with two chars symbols:
      Changes **, __ -> strong, -- -> s.
    */
    private static int check(StringBuilder s, StringBuilder ans, int i, char c) {
        if (s.charAt(i) == c && (i + 1 < s.length() && s.charAt(i + 1) == c)) {
            String nowTeg = "" + c + c;
            changeTeg(nowTeg, ans);
            return 1;
        } else {
            return 0;
        }
    }

    //This method fill isTegOpen.
    private static void fillIsTegOpen() {
        isTegOpen.put("*", false);
        isTegOpen.put("**", false);
        isTegOpen.put("_", false);
        isTegOpen.put("__", false);
        isTegOpen.put("--", false);
        isTegOpen.put("`", false);
        isTegOpen.put("''", false);
    }

    //This method check is now symbols showed screen star, underscore, quote
    private static boolean showedSingleTag(StringBuilder s, int i) {
        if (s.charAt(i) != '\\' || i + 1 >= s.length()) {
            return false;
        }

        char nowSymbol = s.charAt(i + 1);

        return SINGLE_TAGS.contains(nowSymbol);//nowSymbol == '*' || nowSymbol == '_' || nowSymbol == '\'';
    }

    //This method determinate is exist pos index in s, and is it equals ch
    private static boolean notEqualChar(StringBuilder s, int pos, char ch) {
        return pos < 0 || pos >= s.length() || s.charAt(pos) != ch;
    }

    //Add write teg (begin or and) and change flag state
    private static void changeTeg(String nowTeg, StringBuilder ans) {
        boolean isNowTegOpen = (boolean) isTegOpen.get(nowTeg);

        if (isNowTegOpen) {
            ans.append(TEG_CLOSE.get(nowTeg));
        } else {
            ans.append(TEG_OPEN.get(nowTeg));
        }

        isTegOpen.put(nowTeg, !isNowTegOpen);
    }

    //This method determinate and put the correct tag to header (and deleting hashes) and paragraph.
    private static void determinateTeg(StringBuilder s, StringBuilder openTeg, StringBuilder closeTeg) {
        if (isHeader(s)) {
            int qHashes = count(s, '#');
            openTeg.append("<h" + qHashes + ">");
            closeTeg.append("</h" + qHashes + ">");
            s.delete(0, qHashes + 1);
        } else {
            openTeg.append("<p>");
            closeTeg.append("</p>");
        }
    }

    //Check existence header in string
    private static boolean isHeader(StringBuilder s) {
        int qHashes = count(s, '#');

        if (qHashes <= 0 || qHashes > 6 || qHashes == s.length()) {
            return false;
        }

        return s.charAt(qHashes) == ' ';
    }

    //Count the quantity of prefix 'ch'
    static int count(StringBuilder s, char ch) {
        int ans = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ch) {
                ans++;
            } else {
                break;
            }
        }
        return ans;
    }

    //Deleting non using symbols from now
    private static void prepare(StringBuilder now) {
        if (now.charAt(now.length() - 1) == '\n') {
            now.delete(now.length() - 1, now.length());
        }
        if (now.length() > 0 && now.charAt(now.length() - 1) == '\r') {
            now.delete(now.length() - 1, now.length());
        }
    }

    //Convert string now from Markdown to Html
    private static StringBuilder toHtml(StringBuilder now) {
        prepare(now);
        return convert(now);
    }
}
