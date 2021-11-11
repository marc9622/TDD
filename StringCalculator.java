import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.regex.*;

public class StringCalculator {

    public static void main(String[] args) {

        //StringCalculatorTests sct = new StringCalculatorTests();
        //sct.enbleUnitTest();
        System.out.println(add("//;\n1;2;3"));
    }

    public static int add(String input) {

        if(input.length() == 0)
            return 0;

        IntStream ints;
        String seperator;

        if(input.startsWith("//")) {
            seperator = getSeperator(input);
            input = input.replaceFirst(seperator + "\n", "").substring(2);
        } else {
            seperator = ",";
        }
        ints = splitInput(input, seperator);
        
        return ints.sum();
    }

    private static String getSeperator(String input) {

        Pattern p = Pattern.compile("(?<=\\/\\/)(.*?)(?=\\\n)");
        Matcher m = p.matcher(input);
        
        if(!m.find()) {
            System.out.println("Seperator not found; resolved to ','");
            return ",";
        }
        return m.group(0);
    }

    private static IntStream splitInput(String string, String seperator) {
        return Arrays.stream(string.split(seperator)).mapToInt(Integer::parseInt);
    }

}

class StringCalculatorTests {

    StringCalculator sc = new StringCalculator();

    public void testEmpty() {
        if(sc.add("") != 0) error("Inputting empty does not return 0");
    }

    public void testOne() {
        if(sc.add("1") != 1) error("Inputting 1 does not return 1");
    }

    public void testOneAndTwo() {
        if(sc.add("1,2") != 3) error("Inputting 1,2 does not return 3");
    }

    public void testMultiple() {
        if(sc.add("1,2,3") != 6) error("Inputting 1,2,3 does not return 6");
    }

    public void testDelimeter1() {
        if(sc.add("//;\n1;2;3") != 6) error("Inputting //;\n1;2;3 does not return 6");
    }

    public void testDelimeter2() {
        if(sc.add("//|\n1|2|3") != 6) error("Inputting //|\n1|2|3 does not return 6");
    }

    public void testDelimeter3() {
        if(sc.add("//sep\n1sep2sep3") != 6) error("Inputting //sep\n1sep2sep3 does not return 6");
    }

    public void testDelimeterMix() {
        try{
            sc.add("//|\n1|2,3sep4,5");
        }
        catch(Exception e) {
            if(!(e instanceof IllegalArgumentException))
                error("Inputting //|\n1|2,3sep4,5 should return error saying expected '|' but found ','");
        }
    }

    public void testNegative() {
        try{
            sc.add("1,-2");
        }
        catch(Exception e) {
            if(!(e instanceof IllegalAccessException))
                error("Inputting 1,-2 should return error saying negative numbers not allowed but found: <list of negative number(s)>");
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