package erick.santos.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
    /*
        Valida a input baseado no regex $\w+, *\d+, *\d+$ com isso aceita "processo,213,1" ou "processo2, 3, 0"
     */
    public static boolean validateInput(String input) {
        String regex = "(^\\w+, *\\d+, *\\d+$)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        return matcher.find();
    }
}
