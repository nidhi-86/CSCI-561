
import java.util.*;

public class CNFConvertor {
    Map<String, String> predicateToName = new HashMap<>();
    Map<String, String> nameToPredicate = new HashMap<>();
    int count;
    public static final int PREDICATE_LENGTH = 4;

    /**
     * Converts the sentences into CNF
     * For sentences of form a -> b return ~a | b
     * a & b -> c return ~a | ~b | c
     * a return a
     * ~a return ~a
     * Step-1 Remove additional Spaces in between sentences
     * Step-2 Identify predicates and constants
     * @param knowledgeBase
     * @return
     */
    public Map<String, List<String>> convertKBSentencestoCNF(List<String> knowledgeBase) {
        Map<String, List<String>> predicateToProcessedKnowledgeBase = new HashMap<>();
        for(String s : knowledgeBase) {
            //String sentence = s.replaceAll("\\s+", "");
            String sentence = s.replaceAll("=>", "@");

            // Step-1 Convert to postfix expression
            sentence = convertInfixToPostfix(sentence);
            //System.out.println("Postfix- " + sentence);
            // Step-2 Remove implication
            if(sentence.contains("@")) {
                sentence = removeImplicationOperator(sentence);
            }
            //System.out.println("After removing implication- " + sentence);
            // Step-3 Move negation inwards
            if(sentence.contains("~")) {
                sentence = moveNegationInwards(convertInfixToPostfix(sentence));
            }
            //System.out.println("After moving negation inwards- " + sentence);
            // Step-4 Distributivity
            sentence = distribute(convertInfixToPostfix(sentence));

            // Step-5 Replace actual predicateNames
            StringBuilder str = new StringBuilder();
            for(int i = 0; i < sentence.length(); i++) {
                if(sentence.charAt(i) == 'p') {
                    String key = sentence.substring(i, i + PREDICATE_LENGTH);
                    str.append(nameToPredicate.get(key));
                    i += (PREDICATE_LENGTH-1);
                } else {
                    str.append(sentence.charAt(i));
                }
            }
            //Step-6 Standardize variable names
            count = 0;
            sentence = standardizeVariables(str.toString());

            List<String> CNFSentences = Arrays.asList(sentence.split("&"));
            for(String cnfSentence : CNFSentences) {
                Set<String> predicates = getAllPredicateNames(cnfSentence);
                for(String predicate : predicates) {
                    List<String> list = predicateToProcessedKnowledgeBase.getOrDefault(predicate, new ArrayList<>());
                    list.add(cnfSentence);
                    predicateToProcessedKnowledgeBase.put(predicate, list);
                }
            }
            for(String key : predicateToProcessedKnowledgeBase.keySet()) {
                List<String> allPredicates = predicateToProcessedKnowledgeBase.get(key);
                Collections.sort(allPredicates, (o1, o2) -> {
                    String[] str1 = o1.split("\\|");
                    String[] str2 = o2.split("\\|");
                    return str1.length - str2.length;
                });
                predicateToProcessedKnowledgeBase.put(key, allPredicates);
            }
        }
        //printKnowledgeBase(predicateToProcessedKnowledgeBase);
        return predicateToProcessedKnowledgeBase;
    }

    private Set<String> getAllPredicateNames(String sentence) {
        Set<String> predicateList = new HashSet<>();
        StringTokenizer stringTokenizer = new StringTokenizer(sentence, "@&|");
        while(stringTokenizer.hasMoreElements()) {
            String predicateName = stringTokenizer.nextToken();
            int i = 0;
            for(; i < predicateName.length(); i++) {
                if(predicateName.charAt(i) == '(') break;
            }
            predicateList.add(predicateName.substring(0, i));
        }
        return predicateList;
    }

    private String standardizeVariables(String sentence) {
        Map<String, String> variableMapping = new HashMap<>();
        StringBuilder variableNames = new StringBuilder();
        for(int i = 0; i < sentence.length(); i++) {
            if (sentence.charAt(i) == '(') {
                i++;
                while (sentence.charAt(i) != ')') {
                    variableNames.append(sentence.charAt(i++));
                }
                variableNames.append(",");
            }
            if(variableNames.length() != 0) {
                String[] variablesList = variableNames.toString().split(",");
                for (String variable : variablesList) {
                    if (variable.charAt(0) >= 'a' && variable.charAt(0) <= 'z') {
                        String value;
                        if (variableMapping.containsKey(variable)) {
                            value = variableMapping.get(variable);
                        } else {
                            value = "x" + getPredicateValue(count++);
                        }
                        variableMapping.put(variable, value);
                    }
                }
                variableNames = new StringBuilder();
            }
        }
        StringBuilder standardizedSentence = new StringBuilder();
        for(int i = 0; i < sentence.length(); i++) {
            if(sentence.charAt(i) == '(' || sentence.charAt(i) == ',') {
                String variable = "";
                standardizedSentence.append(sentence.charAt(i));
                i++;
                while(sentence.charAt(i) != ',' && sentence.charAt(i) != ')') {
                    variable += sentence.charAt(i++);
                }
                if(variable.charAt(0) >= 'a' && variable.charAt(0) <= 'z') {
                    if (variableMapping.containsKey(variable)) {
                        standardizedSentence.append(variableMapping.get(variable));
                    } else {
                        System.out.println("Something wrong in CNF Convertor- Variable mapping not found for " + variable);
                    }
                } else {
                    standardizedSentence.append(variable);
                }
                i--;
            } else {
                standardizedSentence.append(sentence.charAt(i));
            }
        }
        return standardizedSentence.toString();
    }

    private String distribute(String sentence) {
        Stack<String> stack = new Stack<>();
        for(int i = 0; i < sentence.length(); i++) {
            char ch = sentence.charAt(i);
            if(ch == 'p') {
                stack.push(sentence.substring(i, i + PREDICATE_LENGTH));
                i += (PREDICATE_LENGTH-1);
            } else if(ch == '~') {
                stack.push("~" + stack.pop());
            } else if(ch == '&'){
                String pred1 = stack.pop();
                String pred2 = stack.pop();
                stack.push(pred2 + ch + pred1);
            } else if(ch == '|') {
                String pred1 = stack.pop();
                String pred2 = stack.pop();
                StringBuilder newTop = new StringBuilder();
                List<String> leftPredicates = Arrays.asList(pred1.split("&"));
                List<String> rightPredicates = Arrays.asList(pred2.split("&"));
                if(leftPredicates.isEmpty() && rightPredicates.isEmpty()) {
                    newTop.append(pred2 + "|" + pred1 + "&");
                } else if(leftPredicates.isEmpty()) {
                   for(String right : rightPredicates) {
                       newTop.append(right + "|" + pred2);
                       newTop.append("&");
                   }
                } else if(rightPredicates.isEmpty()) {
                    for(String left : leftPredicates) {
                        newTop.append(left + "|" + pred1);
                        newTop.append("&");
                    }
                } else {
                    for (String left : leftPredicates) {
                        for (String right : rightPredicates) {
                            newTop.append(right + "|" + left);
                            newTop.append("&");
                        }
                    }
                }
                newTop = newTop.deleteCharAt(newTop.length()-1);
                stack.push(newTop.toString());
            }
        }
        return stack.pop();
    }

    private String moveNegationInwards(String sentence) {
        Stack<String> stack = new Stack<>();
        for(int i = 0; i < sentence.length(); i++) {
            char ch = sentence.charAt(i);
            if(ch == 'p') {
                stack.push(sentence.substring(i, i + PREDICATE_LENGTH));
                i += (PREDICATE_LENGTH-1);
            } else if(ch == '&' || ch == '|') {
                String pred1 = stack.pop();
                String pred2 = stack.pop();
                stack.push(pred2 + ch + pred1);
            } else if(ch == '~') {
                StringBuilder stackTop = new StringBuilder();
                String first = stack.pop();
                for (int j = 0; j < first.length(); j++) {
                    // Cases -> predicate-> ~predicate, | -> &, & -> |, ~predicate-> predicate
                    if (j == 0 && first.charAt(j) == 'p') {
                        stackTop.append("~" + first.substring(j, j + PREDICATE_LENGTH));
                    } else if (first.charAt(j) == '|') {
                        stackTop.append("&");
                    } else if (first.charAt(j) == '&') {
                        stackTop.append("|");
                    } else if (first.charAt(j) == 'p') {
                        if (first.charAt(j - 1) != '~') {
                            stackTop.append("~");
                        }
                        stackTop.append(first, j, j + PREDICATE_LENGTH);
                    }
                }
                // If there is an OR in previous expression, which is now &
                String newTop = stackTop.toString();
                if (newTop.contains("&")) {
                    List<String> elements = new ArrayList<>();
                    for (String element : newTop.split("&")) {
                        elements.add("(" + element + ")");
                    }
                    newTop = "(" + String.join("&", elements) + ")";

                }

                stack.push(newTop);
            }
        }
        return stack.pop();
    }

    private String removeImplicationOperator(String sentence) {
        Stack<String> stack = new Stack<>();
        for(int i = 0; i < sentence.length(); i++) {
            char ch = sentence.charAt(i);
            if(ch == 'p') {
                stack.push(sentence.substring(i, i + PREDICATE_LENGTH));
                i += (PREDICATE_LENGTH -1);
            } else if(ch == '&' || ch == '|') {
                String pred1 = stack.pop();
                String pred2 = stack.pop();
                stack.push(pred2 + ch + pred1);
            } else if(ch == '~') {
                String newPred = "~(" + stack.pop() + ")";
                stack.push(newPred);
            } else if(ch == '@') {
                // a => b is ~a | b
                String first = stack.pop();
                String second = stack.pop();
                stack.push("~(" + second + ")|" + first);
            }
        }
        return stack.pop();
    }

    /**
     * Maintain 2 stacks predicate and operator stack
     * If a character is operator, then if empty push it onto stack, then check its priority wrt element on top of stack
     * while this element's priority is higher, push it onto stack, else pop two operands from operand stack and push operator from top
     * of stack + operand1 + operand2 onto stack
     * @param infix
     * @return
     */
    private String convertInfixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        infix = replacePredicates(infix);
        Stack<Character> operatorStack = new Stack<>();

        for(int i = 0; i < infix.length(); i++) {
            char ch = infix.charAt(i);
            if (ch == '(') {
                operatorStack.push(ch);
            } else if(ch == 'p') {
                StringBuilder pred = new StringBuilder(infix.substring(i, i + PREDICATE_LENGTH));
                postfix.append(pred);
                i += (PREDICATE_LENGTH-1);
            } else if(ch == '&' || ch == '@' || ch == '|' || ch == '~') {
                if(operatorStack.isEmpty()) {
                    operatorStack.push(ch);
                } else {
                    int op1 = getOperatorPrecedence(ch);
                    int op2 = getOperatorPrecedence(operatorStack.peek());
                    while (op1 <= op2 && !operatorStack.isEmpty()) {
                        postfix.append(operatorStack.pop());
                        if (!operatorStack.isEmpty())
                            op2 = getOperatorPrecedence(operatorStack.peek());
                    }
                    operatorStack.push(ch);
                }
            } else if(ch == ')') {
                while(!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    postfix.append(operatorStack.pop());
                }
                operatorStack.pop();
            }
        }
        while(!operatorStack.isEmpty()) {
            postfix.append(operatorStack.pop());
        }
        return postfix.toString();
    }

    private int getOperatorPrecedence(char operator) {
        switch (operator) {
            case '~' : return 4;
            case '&' : return 3;
            case '|' : return 2;
            case '@' : return 1;
            case ')' :
            case '(' :
                return 0;
        }
        System.out.println("Unexpected operator found- " + operator);
        return -1;
    }

    /**
     * Replace the predicates with names as p000 to easily convert them into prefix notation
     * @param infix
     * @return
     */
    private String replacePredicates(String infix) {
        StringBuilder newStr = new StringBuilder();
        for(int i = 0; i < infix.length(); i++) {
            if(infix.charAt(i) == ' ') continue;
            if(infix.charAt(i) >= 'A' && infix.charAt(i) <= 'Z') {
                String predicate = "";
                while(infix.charAt(i) != ')') {
                    if(infix.charAt(i) != ' ') {
                        predicate += infix.charAt(i);
                    }
                    i++;
                }
                predicate += ")";
                String value = "p" + getPredicateValue(count++);
                predicateToName.put(predicate, value);
                nameToPredicate.put(value, predicate);
                newStr.append(value);
            } else {
                newStr.append(infix.charAt(i));
            }
        }
        return newStr.toString();
    }

    private String getPredicateValue(int count) {
        if(count >= 0 && count < 10) return "00" + count;
        if(count >= 10 && count < 100) return "0" + count;
        return "" + count;
    }

    private void printKnowledgeBase(Map<String, List<String>> predicateToProcessedKnowledgeBase) {
        Set<String> set = new HashSet<>();
        for(String key : predicateToProcessedKnowledgeBase.keySet()) {
            set.addAll(predicateToProcessedKnowledgeBase.get(key));
        }
        for(String sentence : set) {
            System.out.println(sentence);
        }
    }
}
