
import java.util.*;

public class Unification {

    public boolean unify(String[] queryArguments, String[] unifierArguments) {
        if (queryArguments.length != unifierArguments.length) return false;
        List<String> unifiedVariables = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < queryArguments.length; i++) {
            String queryArg = queryArguments[i];
            String unifierArg = unifierArguments[i];
            // Case -1 Both args are variables
            // Case - 2 Query has variable and unifier has a constant
            // Case -3 Query has constant and unifier has variable
            if (queryArg.charAt(0) >= 'a' && queryArg.charAt(0) <= 'z' && unifierArg.charAt(0) >= 'a' && unifierArg.charAt(0) <= 'z')
                count++;
            else if (queryArg.charAt(0) >= 'a' && queryArg.charAt(0) <= 'z' && unifierArg.charAt(0) >= 'A' && unifierArg.charAt(0) <= 'Z') {
                if (unifiedVariables.contains(unifierArg)) {
                    return false;
                }
                count++;
            } else if (queryArg.charAt(0) >= 'A' && queryArg.charAt(0) <= 'Z' && unifierArg.charAt(0) >= 'a' && unifierArg.charAt(0) <= 'z') {
                unifiedVariables.add(unifierArg);
                count++;
            } else if (queryArg.equals(unifierArg)) {
                count++;
            }
        }
        return count == queryArguments.length;
    }

    public static Map<String, String> unifyPredicates(String[] arguments1, String[] arguments2) {
        Map<String, String> substitutionMap = new HashMap<>();
        Map<String, List<String>> equivalent = new HashMap<>();
        for (int i = 0; i < arguments1.length; i++) {
            String arg1 = arguments1[i];
            String arg2 = arguments2[i];
            if (isVariable(arg1) && isVariable(arg2)) {
                if (arg1.compareTo(arg2) < 0) {
                    for (String key : equivalent.keySet()) {
                        if (equivalent.get(key).contains(arg1)) {
                            arg1 = key;
                            break;
                        }
                    }
                    List<String> list = equivalent.getOrDefault(arg1, new ArrayList<>());
                    list.add(arg2);
                    equivalent.put(arg1, list);
                } else {
                    for (String key : equivalent.keySet()) {
                        if (equivalent.get(key).contains(arg2)) {
                            arg2 = key;
                            break;
                        }
                    }
                    List<String> list = equivalent.getOrDefault(arg2, new ArrayList<>());
                    list.add(arg1);
                    equivalent.put(arg2, list);
                }
            } else if (isVariable(arg1) && isConstant(arg2)) {
                List<String> allTerms = null;
                for(String key : equivalent.keySet()) {
                    if(equivalent.get(key).contains(arg1)) {
                        allTerms = equivalent.get(key);
                        substitutionMap.put(key, arg2);
                    }
                }
                substitutionMap.put(arg1, arg2);
                if (allTerms != null) {
                    for (String eachTerm : allTerms)
                        substitutionMap.put(eachTerm, arg2);
                }
            } else if (isVariable(arg2) && isConstant(arg1)) {
                List<String> allTerms = null;
                for(String key : equivalent.keySet()) {
                    if(equivalent.get(key).contains(arg2)) {
                        allTerms = equivalent.get(key);
                        substitutionMap.put(key, arg1);
                    }
                }
                substitutionMap.put(arg2, arg1);
                if (allTerms != null) {
                    for (String eachTerm : allTerms)
                        substitutionMap.put(eachTerm, arg1);
                }
            }
        }
        return substitutionMap;
    }

    private static boolean isVariable(String str) {
        char x = str.charAt(0);
        return x >= 'a' && x <= 'z';
    }

    private static boolean isConstant(String str) {
        char x = str.charAt(0);
        return x >= 'A' && x <= 'Z';
    }
}
