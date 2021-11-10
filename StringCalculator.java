import java.lang.reflect.Method;

public class StringCalculator {

    public static void main(String[] args) {

        StringCalculatorTests sct = new StringCalculatorTests();
        sct.enbleUnitTest();
    }

    int add(String input) {
        return 0;
    }

}

class StringCalculatorTests {

    StringCalculator sc = new StringCalculator();

    void testEmpty() {
        if(sc.add("") != 0) error("Inputting empty does not return 0");
    }

    void testOne() {
        if(sc.add("1") != 1) error("Inputting 1 does not return 1");
    }

    void testOneAndTwo() {
        if(sc.add("1,2") != 3) error("Inputting 1,2 does not return 3");
    }

    void testMultiple() {
        if(sc.add("1,2,3") != 6) error("Inputting 1,2,3 does not return 6");
    }

    void testDelimeter1() {
        if(sc.add("//;\n1;2;3") != 6) error("Inputting //;\n1;2;3 does not return 6");
    }

    void testDelimeter2() {
        if(sc.add("//|\n1|2|3") != 6) error("Inputting //|\n1|2|3 does not return 6");
    }

    void testDelimeter3() {
        if(sc.add("//sep\n1sep2sep3") != 6) error("Inputting //sep\n1sep2sep3 does not return 6");
    }

    void testDelimeterMix() {
        try{
            sc.add("//|\n1|2,3sep4,5");
        }
        catch(Exception e) {
            if(!(e instanceof IllegalArgumentException))
                error("Inputting //|\n1|2,3sep4,5 should return error saying expected '|' but found ','");
        }
    }

    void testNegative() {
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
        try {
            Class<?> clazz = getClass();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                System.out.println(method.getName());
                if (method.getName().contains("test")) {
                    name = method.getName();
                    Method unitTest = clazz.getDeclaredMethod(name, null);
                    unitTest.invoke(this);
                }
            }
        }
        catch(Exception e) {
            System.out.println(name + ":" + e.getCause());
        }
    }
}