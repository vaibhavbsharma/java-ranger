package gov.nasa.jpf.symbc.fp;

public class TestMult {

    public static void main(String[] args) {
        TestMult t = new TestMult();
        t.testMultZero(0.0f, 0.0f);
    }

    public void testMultZero(float x, float y) {
        // 0 * anything = 0
        assert (x == 0f && y != 0f && x*y != 0f);  // 0*nonzero => 0
        assert !(x != 0f && y == 0f && x*y != 0f);  // nonzero*0 => 0
        assert !(x == 0f && y == 0f && x*y != 0f);  // 0*0 => 0
    }

    public void testNaN(float x, float y){
        assert ((x / 0.0f) * y) != ((x / 0.0f) * y);
    }


}
