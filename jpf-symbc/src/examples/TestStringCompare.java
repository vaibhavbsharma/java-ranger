public class TestStringCompare {

    public static void test(String s1, String s2) {
        int res = s1.compareTo(s2);

        if (res == 0) {
            assert s1.equals(s2);
        }
    }

    public static void main(String[] args) {
        test("A", "B");
    }
}