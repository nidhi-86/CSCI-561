
import java.util.*;

public class Resolution {

    private Map<String, List<String>> predicateToKBSentences;
    private Unification unification;
    public static int LIMIT = 800;

    public Resolution(Map<String, List<String>> predicateToKBSentences) {
        this.predicateToKBSentences = predicateToKBSentences;
        this.unification = new Unification();
    }

    public String checkIfEntails(List<String> queries) {
        StringBuilder output = new StringBuilder();
        for(String q : queries) {
            String query = q.replaceAll("\\s+", "");
            String negatedQuery = negateQuery(query);
            Stack<String> stack = new Stack<>();
            stack.push(negatedQuery);
            String queryKey = getPredicate(negatedQuery, 0);
            List<String> queryList = predicateToKBSentences.getOrDefault(queryKey, new ArrayList<>());
            queryList.add(negatedQuery);
            Collections.sort(queryList, (o1, o2) -> {
                String[] str1 = o1.split("\\|");
                String[] str2 = o2.split("\\|");
                return str1.length - str2.length;
            });
            predicateToKBSentences.put(queryKey, queryList);
            boolean result = depthFirstSearch(stack, 0);
            if(result) output.append("TRUE" + "\n");
            else output.append("FALSE" + "\n");
            queryList.remove(negatedQuery);
            predicateToKBSentences.put(queryKey, queryList);
        }
        String res = output.toString();
        return res.substring(0, res.length()-1);
    }

//    private boolean depthFirstSearch(Stack<String> stack, int limit) {
//        while(!stack.isEmpty()) {
//            String stackTop = stack.pop();
//            String[] topElements = stackTop.split("\\|");
//            if(topElements.length == 0) {
//                System.out.println("Found error, no element to unify");
//                return false;
//            }
//            String queryToProcess = topElements[topElements.length-1];
//            String query = negateQuery(queryToProcess); // Negate the query to find the unifier
//            String predicate = getPredicate(query, 0);
//            String[] queryArguments = getArguments(query);
//
//            if(!predicateToKBSentences.containsKey(predicate)) {
//                // No unifier present
//                return false;
//            }
//            List<String> sentences = predicateToKBSentences.get(predicate);
//            // Out of all these sentences, find which one could be unified
//            for(String sentence : sentences) {
//
//                StringBuilder remaining = new StringBuilder();
//                for(int i = 0; i < topElements.length-1; i++) {
//                    remaining.append(topElements[i] + "|");
//                }
//                if(remaining.length() >= 1 && remaining.charAt(remaining.length()-1) == '|') {
//                    remaining.deleteCharAt(remaining.length() - 1);
//                }
//                String remainingStr = remaining.toString();
//                if(remainingStr.contains("On(x000,x001)|~Above(x001,C)")) {
//                    System.out.println("here");
//                }
//                if(limit > LIMIT) {
//                    //System.out.println("Limit reached, Cutting off the search!");
//                    return false;
//                }
//
//                List<String> orStatementsList = Arrays.asList(sentence.split("\\|"));
//                String unifier = null;
//                for(String orStatement : orStatementsList) {
//                    if(orStatement.contains(predicate)) {
//                        unifier = orStatement;
//                    }
//                }
//                if(unifier == null) {
//                    System.out.println("No unifier found in - " + sentence);
//                    continue;
//                }
//                String[] unifierArguments = getArguments(unifier);
//                boolean unificationResult = unification.unify(queryArguments, unifierArguments);
//                if(unificationResult) {
//                    Map<String, String> unifierValuesMap = unification.unifyPredicates(queryArguments, unifierArguments);
//                    StringBuilder finalResolvedStatement = new StringBuilder();
//                    List<String> stackList = new ArrayList<>();
//                    for(String content : stack.toArray(new String[stack.size()])) {
//                        stackList.add(content);
//                    }
//                    boolean resolvedOnce = false;
//                    for(String otherPredicate : orStatementsList) {
//                        otherPredicate = otherPredicate.replaceAll("\\s+", "");
//                        // Replace all variables with unifier values
//                        Iterator<Map.Entry<String, String>> unifierMapIterator = unifierValuesMap.entrySet().iterator();
//                        while(unifierMapIterator.hasNext()) {
//                            Map.Entry<String, String> element = unifierMapIterator.next();
//                            String key = element.getKey();
//                            String value = element.getValue();
//                            if(otherPredicate.contains(key)) {
//                                otherPredicate = otherPredicate.replace(key, value);
//                            }
//                            if(remainingStr.contains(key)) {
//                                remainingStr = remainingStr.replace(key,value);
//                            }
//                        }
//                        String currPredicate = getPredicate(otherPredicate, 0);
//                        if(!currPredicate.equals(predicate) || resolvedOnce) {
//                            finalResolvedStatement.append(otherPredicate + "|");
//                        } else if(currPredicate.equals(predicate)) {
//                            resolvedOnce = true;
//                        }
//                    }
//                    if(finalResolvedStatement.length() > 1 && finalResolvedStatement.charAt(finalResolvedStatement.length()-1) == '|') {
//                        finalResolvedStatement.deleteCharAt(finalResolvedStatement.length()-1);
//                    }
//                    if(finalResolvedStatement.length() != 0) {
//                        if(remaining.length() != 0)
//                            finalResolvedStatement.append("|" + remainingStr);
//                    } else {
//                        finalResolvedStatement.append(remainingStr);
//                    }
//                    System.out.println("Final Resolved Statement - " + finalResolvedStatement);
//                    if(finalResolvedStatement.length() != 0)
//                        stackList.add(finalResolvedStatement.toString());
//
//                    Stack<String> stackAfterResolution = new Stack<>();
//                    for(String question : stackList) {
//                        stackAfterResolution.push(question);
//                    }
//                    System.out.println(stackAfterResolution);
//                    boolean result = depthFirstSearch(stackAfterResolution, limit+1);
//                    if(result) return true;
//                }
//            }
//            return false;
//        }
//        return true;
//    }

    private boolean depthFirstSearch(Stack<String> stack, int limit) {

        while(!stack.isEmpty()) {

            String queryToProcess = stack.pop();
            String query = negateQuery(queryToProcess); // Negate the query to find the unifier
            String predicate = getPredicate(query, 0);
            String[] queryArguments = getArguments(query);

            if(!predicateToKBSentences.containsKey(predicate)) {
                // No unifier present
                return false;
            }
            List<String> sentences = predicateToKBSentences.get(predicate);
            // Out of all these sentences, find which one could be unified
            for(String sentence : sentences) {
                if(limit > LIMIT) {
                    //System.out.println("Limit reached, Cutting off the search!");
                    return false;
                }

                List<String> orStatementsList = Arrays.asList(sentence.split("\\|"));
                String unifier = null;
                for(String orStatement : orStatementsList) {
                    if(orStatement.contains(predicate)) {
                        unifier = orStatement;
                    }
                }
                if(unifier == null) {
                    System.out.println("No unifier found in - " + sentence);
                    continue;
                }
                String[] unifierArguments = getArguments(unifier);
                boolean unificationResult = unification.unify(queryArguments, unifierArguments);
                if(unificationResult) {
                    Map<String, String> unifierValuesMap = new HashMap<>();
                    for(int i = 0; i < queryArguments.length; i++) {
                        if(!unifierValuesMap.containsKey(unifierArguments[i])) {
                            unifierValuesMap.put(unifierArguments[i], queryArguments[i]);
                        }
                    }
                    List<String> stackList = new ArrayList<>();
                    for(String content : stack.toArray(new String[stack.size()])) {
                        stackList.add(content);
                    }
                    boolean canPutIntoStack = false;
                    for(String otherPredicate : orStatementsList) {
                        otherPredicate = otherPredicate.replaceAll("\\s+", "");
                        // Replace all variables with unifier values
                        Iterator<Map.Entry<String, String>> unifierMapIterator = unifierValuesMap.entrySet().iterator();
                        while(unifierMapIterator.hasNext()) {
                            Map.Entry<String, String> element = unifierMapIterator.next();
                            String key = element.getKey();
                            String value = element.getValue();
                            if(otherPredicate.contains(key)) {
                                String[] strs = otherPredicate.split("\\(");
                                String part = strs[1].replace(key, value);
                                otherPredicate = strs[0] + "(" + part;
                            }
//                            Iterator<String> iterator = stackList.iterator();
//                            List<String> newArray = new ArrayList<>();
//                            while(iterator.hasNext()) {
//                                String stackElement = iterator.next();
//                                if(stackElement.contains(key)) {
//                                    stackElement = stackElement.replace(key, value);
//                                    iterator.remove();
//                                    newArray.add(stackElement);
//                                }
//                            }
//                            stackList.addAll(newArray);
                        }
                        //System.out.println("After replacing values by unification - " + otherPredicate);
                        String currPredicate = getPredicate(otherPredicate, 0);
                        // Do not push already processed query into stack again
                        if(canPutIntoStack || !currPredicate.equals(predicate)) {
                            String newQuery = negateQuery(otherPredicate);
                            Iterator<String> iterator = stackList.iterator();
                            boolean flag = true;
                            while(iterator.hasNext()) {
                                String stackContent = iterator.next();
                                if(stackContent.equals(newQuery)) {
                                    iterator.remove();
                                    flag = false;
                                    canPutIntoStack = true;
                                }
                            }
                            if(flag) {
                                stackList.add(otherPredicate);
                            }
                        } else if(currPredicate.equals(predicate)) {
                            canPutIntoStack = true;
                        }
                    }
                    Stack<String> stackAfterResolution = new Stack<>();
                    for(String question : stackList) {
                        stackAfterResolution.push(question);
                    }
                    boolean result = depthFirstSearch(stackAfterResolution, limit+1);
                    if(result) return true;
                }
            }
            return false;
        }
        return true;
    }

    private String getPredicate(String query, int index) {
        StringBuilder predicate = new StringBuilder();
        while(query.charAt(index) != '(') {
            predicate.append(query.charAt(index++));
        }
        return predicate.toString();
    }

    private String[] getArguments(String query) {
        StringBuilder variableNames = new StringBuilder();
        for(int i = 0; i < query.length(); i++) {
            if(query.charAt(i) == '(') {
                i++;
                while(query.charAt(i) != ')') {
                    variableNames.append(query.charAt(i++));
                }
                variableNames.append(",");
            }
        }
        return variableNames.toString().split(",");
    }

    private String negateQuery(String query) {
        if(query.contains("~")) {
            return query.substring(1);
        }
        return "~" + query;
    }
}
