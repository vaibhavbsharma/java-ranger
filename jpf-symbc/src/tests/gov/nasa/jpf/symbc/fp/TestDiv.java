package gov.nasa.jpf.symbc.fp;

public class TestDiv {

    public static void main(String[] args) {
        TestDiv t = new TestDiv();
        //t.testFDivChain(5.0f, 6.0f);
        t.testNaN(5.0f);

    }

    public void testNaN(float x){
        // assert x != x; // would fire if NaN comparisons work (x!=x true for NaN)
        //float res = x / 0.0f;
       // System.out.println(res);
        float s = x / 0.0f;
        assert !(s != s); // fires because NaN != NaN, so x==x is false for NaN

        // Expected: fires only when symbolic.nan=true (NaN values in domain)
        // FCMP 4th branch controls choice encoding, not whether NaN exists
        // assert x == x || x != x;  // law of excluded middle, should never fire
    }

    public void test(float x, float y) {
        float res = x / y;
        // Branch on FDIV result classification
        if (res == Float.POSITIVE_INFINITY) {
            System.out.println("+inf");
        } else if (res == Float.NEGATIVE_INFINITY) {
            System.out.println("-inf");
        } else if (res == 0.0f) {
            System.out.println("zero");   // 0/x, x/inf
        } else if (res != res) {
            System.out.println("nan");
        } else {
            System.out.println("normal: " + res);
        }
    }



    public void testFDivChain(float x, float y) {
        float q = x / y;

        // Chain 1: 0/0 = NaN
        assert !(x == 0.0f && y == 0.0f && q == q);


        // Chain 2: 0/0 produces q != q (NaN)
       // assert !(x == 0.0f && y == 0.0f && !(q != q));


        // Chain 3: positive/0 = +inf
        //assert !(x > 0.0f && y == 0.0f && q != Float.POSITIVE_INFINITY);


        // Chain 4: negative/0 = -inf
        //assert !(x < 0.0f && y == 0.0f && q != Float.NEGATIVE_INFINITY);


    }
}
