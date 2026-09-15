public class TestDoubleMult {

    //Test 1: Double Bounds check
    public static void testBounds(double d) {
        if (d < -10000.0 || d > 10000.0) {
            assert false;
        }
    }

    //Test 2: Multiplication
    public static void testMul(double d) {
        double x = d * d;


        // should find d = 1 and d = -1
        if (x == 1.0) {
            assert false;
        }
    }



    public static void main(String[] args) {
        testBounds(0.0);
        testMul(0.0);
    }
}