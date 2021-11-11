import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.*;
import java.util.regex.*;

public class StringCalculator {

    public static void main(String[] args) {

        StringCalculatorTests sct = new StringCalculatorTests();
        sct.enbleUnitTest();

        
    }

    public static int add(String input) {
        return getIntStreamFromStringInput(input).sum();
    }

    private static IntStream getIntStreamFromStringInput(String input) {
        if(input.length() == 0)
            return IntStream.of(new int[]{0});

        String seperator;
        if(input.startsWith("//")) {
            seperator = getSeperator(input);
            input = input.substring(2 + seperator.length() + 1); //Cuts of the seperation indicator in the beginning. 2 (for //) + sep.length + 2 (for \n)
        }
        else
            seperator = ",";
        return splitToIntStream(input, seperator);
    }

    //First call is usually hundreds of times slower than the other one. Later calls are only 20x slower.
    private static String getSeperatorRegex(String input) {
        Matcher matcher = Pattern.compile("(?<=\\/\\/)(.*?)(?=\\\n)").matcher(input);
        if(matcher.find())
            if(!matcher.group(0).isBlank())
                return matcher.group(0);
        System.out.println("Seperator not found; resolved to ','");
        return ",";
    }

    //This one is a lot faster.
    private static String getSeperator(String input) {
        String seperator = input.substring(2, input.indexOf("\n"));
        if(!seperator.isBlank())
            return seperator;
        System.out.println("Seperator not found; resolved to ','");
        return ",";
    }

    private static IntStream splitToIntStream(String string, String seperator) {
        char[] chars = string.toCharArray();
        char[] sep = seperator.toCharArray();
        ArrayList<Integer> ints = new ArrayList<Integer>();
        int start = 0;
        for(int i = 0; i < chars.length; i++)
            for(int end = i, j = 0; i < chars.length && j < sep.length; j++)
                if(chars[i] != sep[j]) {
                    if(!Character.isDigit(chars[i]) && chars[i] != '-')
                        throw new IllegalArgumentException("Expected '" + seperator + "' but found '" + chars[i] + "' at index " + i + ".");
                }
                else if(j + 1 == sep.length) {
                    addSubstringToList(string, start, end, ints);
                    start = i + 1;
                    break;
                }
                else
                    i++;
        addSubstringToList(string, start, string.length(), ints);
        disallowNegatives(ints);
        return ints.stream().mapToInt(Integer::valueOf);
    }

    private static void addSubstringToList(String string, int start, int end, List<Integer> list) {
        list.add(Integer.parseInt(string.substring(start, end)));
    }

    private static void disallowNegatives(List<Integer> list) {
        Object[] negatives = list.stream().filter(i -> i < 0).toArray();
        if(negatives.length > 0)
            throw new IllegalArgumentException("Negative numbers are not allowed but found: " + Arrays.toString(negatives));
    }

}

class StringCalculatorTests {

    StringCalculator sc = new StringCalculator();

    public void testEmpty() {
        if(StringCalculator.add("") != 0) error("Inputting empty does not return 0");
    }

    public void testOne() {
        if(StringCalculator.add("1") != 1) error("Inputting 1 does not return 1");
    }

    public void testOneAndTwo() {
        if(StringCalculator.add("1,2") != 3) error("Inputting 1,2 does not return 3");
    }

    public void testMultiple() {
        if(StringCalculator.add("1,2,3") != 6) error("Inputting 1,2,3 does not return 6");
    }

    public void testDelimeter1() {
        if(StringCalculator.add("//;\n1;2;3") != 6) error("Inputting //;\n1;2;3 does not return 6");
    }

    public void testDelimeter2() {
        if(StringCalculator.add("//|\n1|2|3") != 6) error("Inputting //|\n1|2|3 does not return 6");
    }

    public void testDelimeter3() {
        if(StringCalculator.add("//sep\n1sep2sep3") != 6) error("Inputting //sep\n1sep2sep3 does not return 6");
    }

    public void testDelimeterMix() {
        try{
            StringCalculator.add("//|\n1|2,3sep4,5");
        }
        catch(Exception e) {
            if(!(e.getClass() == IllegalArgumentException.class))
                error("Inputting //|\n1|2,3sep4,5 should throw IllegalArgumentException saying expected '|' but found ','");
        }
    }

    public void testNegative() {
        try{
            StringCalculator.add("1,-2,3");
        }
        catch(Exception e) {
            if(!(e.getClass() == IllegalArgumentException.class))
                error("Inputting 1,-2 should throw IllegalArgumentException saying negative numbers not allowed but found: <list of negative number(s)>");
        }
    }

    private void error(String message) {
        System.out.println(message);
    }

    void enbleUnitTest(){
        System.out.println("-------------UNIT TEST-----------------");
        String name = "";
        Class<?> clazz = getClass();
        Method[] methods = clazz.getMethods();
        for(Method method : methods)
            try{
                if(method.getName().contains("test")) {
                    name = method.getName();
                    Method unitTest = clazz.getDeclaredMethod(name, null);
                    unitTest.invoke(this);
                }
            }
            catch(Exception e) {
                System.out.println(name + ":" + e.getCause());
            }
    }
}