package gov.nasa.jpf.symbc.fp;

import org.sosy_lab.sv_benchmarks.Verifier;

public class TestDREM {

    public static void main(String[] args) {
        TestDREM t = new TestDREM();

        // 1. Regular Symcrete Matrix (Concrete / Symbolic combinations)
        t.test(5.0d, 6.0d);                           // Concrete - Concrete
        t.test(5.0d, Verifier.nondetDouble());          // Concrete - Symbolic
        t.test(Verifier.nondetDouble(), 6.0d);          // Symbolic - Concrete
        t.test(Verifier.nondetDouble(), Verifier.nondetDouble()); // Symbolic - Symbolic

        // 2. IEEE 754 Edge Case Injections
        t.test(Double.NaN, Verifier.nondetDouble());              // NaN dividend
        t.test(Verifier.nondetDouble(), Double.POSITIVE_INFINITY); // +Inf divisor
        t.test(Double.NEGATIVE_INFINITY, Verifier.nondetDouble()); // -Inf dividend
        t.test(Verifier.nondetDouble(), 0.0d);                   // Zero divisor
        t.test(-0.0d, Verifier.nondetDouble());                  // Negative zero dividend

        // 3. NaN Unordered Comparison Check (DCMP Instruction)
        t.testNaN(Double.NaN);
        t.testNaN(Verifier.nondetDouble());
    }

    // DREM with symbolic operands: the result can be NaN, the dividend itself
    // (infinite divisor), or the exact IEEE 754 remainder. This branches on
    // the result classification so the symbolic engine must explore all three
    // outcome arms.
    public void test(double x, double y) {
        double res = x % y;
        if (res != res) {
            System.out.println("nan");
        } else if (res == x) {
            System.out.println("dividend");
        } else {
            System.out.println("rem: " + res);
        }
    }

    // NaN is the only value for which x != x (IEEE 754). This assertion
    // fires whenever x is not NaN, exercising the NaN semantics of DCMP.
    public void testNaN(double x) {
        if (x != x) {
            System.out.println("x is NaN");
        } else {
            System.out.println("x is not NaN");
        }
    }
}