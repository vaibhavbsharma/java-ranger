package gov.nasa.jpf.symbc.fp;

public class TestD2I {

    public static void main(String[] args) {
        System.out.println("-------- In main!");
        float x = 5.0f;
        foo(x);
    }

    public static double bar(long l) {
        assert l < 100L && l > -100L;
        return (double) l;
    }

    public static double foo(float x) {
        if (x < 0.0) {
            x *= 10.0f;
        } else {
            x /= 10.0f;
        }
        long l = (long) x;
        assert l < 100L && l > -100L;
        return bar(l);
    }


}