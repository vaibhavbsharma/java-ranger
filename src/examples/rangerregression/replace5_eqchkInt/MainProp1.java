package rangerregression.replace5_eqchkInt;

import org.sosy_lab.sv_benchmarks.Verifier;

public class MainProp1 {

  public static void main(String[] args) {
    int c0 = Verifier.nondetInt();
    int c1 = Verifier.nondetInt();
    Replace r = new Replace();
    r.mainProcess(c0, c1);
//    Replace.reset(); // not resetting the internal state of replace causes a verification failure
    r.mainProcess(c0, c1);
//    Outputs out1 = new Outputs(r.mainProcess(c0, c1, c2, c3, c4));
//
//    Outputs out2 = new Outputs();
//    checkEquality(out1, out2);
  }
}
