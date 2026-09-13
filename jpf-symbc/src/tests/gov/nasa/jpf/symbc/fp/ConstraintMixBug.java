package gov.nasa.jpf.symbc.fp;

public class ConstraintMixBug {
    public static void main(String[] args){
        ConstraintMixBug test = new ConstraintMixBug();
        test.testNaN(5.0f);
    }
    public void testNaN(float x) {
        assert x != x;
    }
}
