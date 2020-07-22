package gov.nasa.jpf.symbc;

import com.ibm.wala.util.WalaException;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.bytecode.IfInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.branchcoverage.BranchCoverage;
import gov.nasa.jpf.symbc.branchcoverage.CoverageMode;
import gov.nasa.jpf.symbc.branchcoverage.obligation.ObligationMgr;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.veritesting.VeritestingMain;
import gov.nasa.jpf.symbc.veritesting.VeritestingUtil.SpfUtil;
import gov.nasa.jpf.symbc.veritesting.branchcoverage.obligation.CollectObligationsVisitor;
import gov.nasa.jpf.symbc.veritesting.branchcoverage.obligation.VeriObligationMgr;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import com.ibm.wala.ipa.callgraph.*;

import java.io.IOException;

import static gov.nasa.jpf.symbc.branchcoverage.obligation.ObligationMgr.*;

public class VeriBranchListener extends BranchListener {

    public VeriBranchListener(Config conf, JPF jpf) {
        super(conf, jpf);
        if (conf.hasValue("coverageMode")) {
            int coverageNum = conf.getInt("coverageMode");
            assert coverageNum > 4 : "coverageMode must be greater that 4 to support Veritesting";
            if (conf.getInt("coverageMode") == 5) coverageMode = CoverageMode.JRCOLLECT_COVERAGE;
            else {
                System.out.println("unknown mode. Failing");
                assert false;
            }
        }
    }


    public void executeInstruction(VM vm, ThreadInfo ti, Instruction instructionToExecute) {
        /*if (coverageMode == CoverageMode.COLLECT_PRUNE || coverageMode == CoverageMode.COLLECT_PRUNE_GUIDE) // pruning only in pruning mode
            if (allObligationsCovered) {
                if (!evaluationMode)
                    System.out.println("all obligation covered, ignoring all paths.");
                ti.getVM().getSystemState().setIgnored(true);
                return;
            }
*/
        try {
            if (firstTime) {
                System.out.println("---- CoverageMode = " + coverageMode + ", solver = " + solver + ", benchmark= " + benchmarkName + (System.getenv("MAX_STEPS") != null ? ", STEPS " + System.getenv("MAX_STEPS") : ""));
                BranchCoverage.createObligations(ti);
                ObligationMgr.finishedCollection();
                BranchCoverage.finishedCollection();
                firstTime = false;
                timeZero = System.currentTimeMillis();
                if (!evaluationMode) {
                    printCoverage();
                    printReachability();
                    printOblgToBBMap();
                }
                System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-finished obligation collection|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-");
            } else {
                /*if (instructionToExecute instanceof IfInstruction && (VeritestingListener.veritestingSuccessful)) {
                    VeriObligationMgr.addSymbolicOblgMap(CollectObligationsVisitor.oblgToExprsMap);
                }*/
            }
        } catch (IOException | CallGraphBuilderCancelException | WalaException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void instructionExecuted(VM vm, ThreadInfo currentThread, Instruction nextInstruction, Instruction executedInstruction) {

        //if veritesting was not successful then we must have encountered a branch and so there is a pcDepth at that point that we need to account for.
        if ((executedInstruction instanceof IfInstruction)) {
//            isSymBranchInst = SpfUtil.isSymCond(currentThread, instructionToExecute);
//            if (isSymBranchInst && !VeritestingListener.veritestingSuccessful)
            if (!VeritestingListener.veritestingSuccessful)
                super.instructionExecuted(vm, currentThread, nextInstruction, executedInstruction);
        }
    }

    @Override
    public void threadTerminated(VM vm, ThreadInfo terminatedThread) {
        if (!evaluationMode) System.out.println("end of thread");
//        newCoverageFound = false;
//        allObligationsCovered = ObligationMgr.isAllObligationCovered();
        coverageStatistics.recordCoverageForThread();
    }

    @Override
    public void choiceGeneratorAdvanced(VM vm, ChoiceGenerator<?> currentCG) {
        if (currentCG instanceof PCChoiceGenerator) {
            if (!evaluationMode) {
//                System.out.println("choiceGeneratorAdvanced: at " + currentCG.getInsn().getMethodInfo() + "#" + currentCG.getInsn().getPosition());
                System.out.println("pcDepth = " + VeriObligationMgr.getPcDepth());
            }
            VeriObligationMgr.incrementPcDepth();
        }
    }


    @Override
    public void stateBacktracked(Search search) {
        if (search.getVM().getSystemState().getChoiceGenerator() instanceof PCChoiceGenerator) {
            VeriObligationMgr.popDepth();
            if (!evaluationMode) {
//                System.out.println("backtracking now");
                System.out.println("pcDepth = " + VeriObligationMgr.getPcDepth());
            }
        }
    }
}