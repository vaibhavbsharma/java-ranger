package gov.nasa.jpf.symbc.fp;

public class TestRem {
    public static void main(String[] args) {
        TestRem t = new TestRem();
        //t.testFDivChain(5.0f, 6.0f);
        t.testNaN(5.0f);

    }

    public void testNaN(float s){
        float x = s % 0.0f;
        assert x != x;
        if (x == x){
            assert (x / 0.0f) != (x / 0.0f);
        }
    }

    public void testStr(int x){
        String s = "Hello, world";
        if (x <=  s.length()){
            System.out.println("Smaller");
        } else {
            System.out.println("Larger");
        }
    }
}
