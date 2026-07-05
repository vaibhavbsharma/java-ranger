package gov.nasa.jpf.symbc.fp;

public class TestFRem {
    public static void main(String[] args) {
        TestFRem t = new TestFRem();
        t.testFSubNaN(5.0f);
    }

    public void testFSubNaN(float x) {
        // all of the below produces NaN, whre x is symbolic
        float r = x % 0.0f;
        assert r != r;
        r = (5.0f / 0.0f) % x;
        assert r != r;
        r = (-5.0f / 0.0f) % x;
        assert r != r;
    }

    public void testFSubInf(float x) {
        float r = (1.0f / 0.0f) - x;
        assert r == (1.0f / 0.0f);
    }
}
