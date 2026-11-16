package gov.nasa.jpf.symbc.fp;

import org.sosy_lab.sv_benchmarks.Verifier;

public class TestFREM {

    public static void main(String[] args) {
        TestFREM t = new TestFREM();

        // 1. Regular Symcrete Matrix (Concrete / Symbolic combinations)
        t.test(5.0f, 6.0f);                           // Concrete - Concrete
        t.test(5.0f, Verifier.nondetFloat());          // Concrete - Symbolic
        t.test(Verifier.nondetFloat(), 6.0f);          // Symbolic - Concrete
        t.test(Verifier.nondetFloat(), Verifier.nondetFloat()); // Symbolic - Symbolic

        // 2. IEEE 754 Edge Case Injections
        t.test(Float.NaN, Verifier.nondetFloat());              // NaN dividend
        t.test(Verifier.nondetFloat(), Float.POSITIVE_INFINITY); // +Inf divisor
        t.test(Float.NEGATIVE_INFINITY, Verifier.nondetFloat()); // -Inf dividend
        t.test(Verifier.nondetFloat(), 0.0f);                   // Zero divisor
        t.test(-0.0f, Verifier.nondetFloat());                  // Negative zero dividend

        // 3. NaN Unordered Comparison Check (FCMP Instruction)
        t.testNaN(Float.NaN);
        t.testNaN(Verifier.nondetFloat());
    }

    // FREM with symbolic operands: the result can be NaN, the dividend itself
    // (infinite divisor), or the exact IEEE 754 remainder. This branches on
    // the result classification so the symbolic engine must explore all three
    // outcome arms.
    public void test(float x, float y) {
        float res = x % y;
        if (res != res) {
            System.out.println("nan");
        } else if (res == x) {
            System.out.println("dividend");
        } else {
            System.out.println("rem: " + res);
        }
    }

    // NaN is the only value for which x != x (IEEE 754). This assertion
    // fires whenever x is not NaN, exercising the NaN semantics of FCMP.
    public void testNaN(float x) {
        if (x != x) {
            System.out.println("x is NaN");
        } else {
            System.out.println("x is not NaN");
        }
    }
}