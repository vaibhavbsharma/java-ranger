package overflow;

import org.sosy_lab.sv_benchmarks.Verifier;

public class SubtractionOverflowTest {

  public static void main(String[] args) {
    int a = Verifier.nondetInt();
    SubtractionOverflowTest inst = new SubtractionOverflowTest();
    inst.testTrueP(a);
    inst.testTrueN(a);
  }

  /*
   * Test ISUB with overflow: MAX_VALUE - (-1) overflows to MIN_VALUE.
   * Without overflow simulation, r = 2147483648 which is NOT MIN_VALUE.
   * Since a is forced to MAX_VALUE, r can only ever be MIN_VALUE.
   * Expected verdict: TRUE
   */
  public void testTrueN(int a) {
    if (a != Integer.MAX_VALUE) return; // force a to MAX_VALUE to trigger overflow
    int r = a - (-1);
    assert r == Integer.MIN_VALUE : "overflow not simulated: MAX_VALUE - (-1) should wrap to MIN_VALUE";
  }

  /*
   * Test ISUB with overflow: MIN_VALUE - 1 underflows to MAX_VALUE.
   * Without overflow simulation, r = -2147483649 which is NOT MAX_VALUE.
   * Since a is forced to MIN_VALUE, r can only ever be MAX_VALUE.
   * Expected verdict: TRUE
   */
  public void testTrueP(int a) {
    if (a != Integer.MIN_VALUE) return; // force a to MIN_VALUE to trigger overflow
    int r = a - 1;
    assert r == Integer.MAX_VALUE : "overflow not simulated: MIN_VALUE - 1 should wrap to MAX_VALUE";
  }
}
