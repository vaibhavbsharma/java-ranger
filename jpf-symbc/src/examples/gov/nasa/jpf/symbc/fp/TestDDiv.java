package gov.nasa.jpf.symbc.fp;

import org.sosy_lab.sv_benchmarks.Verifier;

public class TestDDiv {

    public static void main(String[] args) {
        TestDDiv t = new TestDDiv();

        // 1. Regular Symcrete Matrix (Concrete / Symbolic combinations)
        t.test(5.0, 6.0);                             // Concrete - Concrete
        t.test(5.0, Verifier.nondetDouble());         // Concrete - Symbolic
        t.test(Verifier.nondetDouble(), 6.0);         // Symbolic - Concrete
        t.test(Verifier.nondetDouble(), Verifier.nondetDouble()); // Symbolic - Symbolic

        // 2. IEEE 754 Edge Case Injections
        t.test(Double.NaN, Verifier.nondetDouble());              // NaN dividend
        t.test(Verifier.nondetDouble(), Double.POSITIVE_INFINITY); // +Inf divisor
        t.test(Double.NEGATIVE_INFINITY, Verifier.nondetDouble()); // -Inf dividend
        t.test(Verifier.nondetDouble(), 0.0);                   // Zero divisor
        t.test(-0.0, Verifier.nondetDouble());                  // Negative zero dividend

        // 3. NaN Unordered Comparison Check (DCMP Instruction)
        t.testNaN(Double.NaN);
        t.testNaN(Verifier.nondetDouble());
    }

    // DDIV with symbolic operands: branches on result classification
    public void test(double x, double y) {
        double res = x / y;
        if (res == Double.POSITIVE_INFINITY) {
            System.out.println("+inf");
        } else if (res == Double.NEGATIVE_INFINITY) {
            System.out.println("-inf");
        } else if (res == 0.0) {
            System.out.println("zero");
        } else if (res != res) {
            System.out.println("nan");
        } else {
            System.out.println("normal: " + res);
        }
    }

    // Exercises DCMP semantics for NaN unordered comparison
    public void testNaN(double x) {
        if (x != x) {
            System.out.println("x is NaN");
        } else {
            System.out.println("x is not NaN");
        }
    }
}