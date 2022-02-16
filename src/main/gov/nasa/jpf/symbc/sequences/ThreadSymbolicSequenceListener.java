/**
 * Soha Hussein: The code here is based on SymbolicSequenceListener but is tuned to work with
 * increment solver and it is optimized to generate tests at the end of the execution path rather
 * than when the choice generator backtracks.
 */

/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//
//Copyright (C) 2007 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.symbc.sequences;

// does not work well for static methods:summary not printed for errors

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.report.PublisherExtension;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.BranchListener;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.VeriBranchListener;
import gov.nasa.jpf.symbc.VeritestingListener;
import gov.nasa.jpf.symbc.branchcoverage.TestCaseGenerationMode;
import gov.nasa.jpf.symbc.concolic.PCAnalyzer;
import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.symbc.numeric.solvers.IncrementalListener;
import gov.nasa.jpf.symbc.veritesting.branchcoverage.CoverageCriteria;
import gov.nasa.jpf.vm.*;

import java.util.*;

import static gov.nasa.jpf.symbc.BranchListener.tcgOnTheGo;
import static gov.nasa.jpf.symbc.VeriBranchListener.recordSolvingInStatistics;

public class ThreadSymbolicSequenceListener extends SymbolicSequenceListener implements PublisherExtension {

    private static Vector<String> lastTestCaseOnThread = null;

    public ThreadSymbolicSequenceListener(Config conf, JPF jpf) {
        super(conf, jpf);
    }

    /*@Override
    public void propertyViolated(Search search) {
        System.out.println("--------->property violated");
        VM vm = search.getVM();
        SystemState ss = vm.getSystemState();
        ChoiceGenerator<?> cg = vm.getChoiceGenerator();
        if (!(cg instanceof PCChoiceGenerator)) {
            ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
            while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
                prev_cg = prev_cg.getPreviousChoiceGenerator();
            }
            cg = prev_cg;
        }
        Property prop = search.getLastError().getProperty();
        String errAnn = "";
        if (prop instanceof NoUncaughtExceptionsProperty) {
            String exceptionClass = ((NoUncaughtExceptionsProperty) prop).getUncaughtExceptionInfo().getExceptionClassname();
            errAnn = "(expected = " + exceptionClass + ".class)";
        }

        String error = search.getLastError().getDetails();
        error = "\"" + error.substring(0, error.indexOf("\n")) + "...\"";

        if ((cg instanceof PCChoiceGenerator) &&
                ((PCChoiceGenerator) cg).getCurrentPC() != null) {

            PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
            System.out.println("pc " + pc.count() + " " + pc);

            //solve the path condition
            if (SymbolicInstructionFactory.concolicMode) { //TODO: cleaner
                *//*if (IncrementalListener.solver == null)
     *//*    assert false : "concolic mode is not supported with incremental solver. Failing";
                SymbolicConstraintsGeneral solver = new SymbolicConstraintsGeneral();
                PCAnalyzer pa = new PCAnalyzer();
                pa.solve(pc, solver);
            } else
                pc.solve();

            // get the chain of choice generators.
            ChoiceGenerator<?>[] cgs = ss.getChoiceGenerators();
            Vector<String> methodSequence = getMethodSequence(cgs);
            // Now append the error String and then add methodSequence to methodSequences
            // prefix the exception marker to distinguish this from
            // an invoked method.
            if (errAnn != "")
                methodSequence.add(0, errAnn);
            methodSequence.add(exceptionMarker + error);
            methodSequences.add(methodSequence);
        }
    }
*/

    @Override
    public void stateBacktracked(Search search) {//do nothing
    }

    @Override
    public void threadTerminated(VM vm, ThreadInfo terminatedThread) {
        if (VeriBranchListener.ignoreCoverageCollection)
            return;
        if (!BranchListener.pathCoverage)
            if (!BranchListener.newCoverageFound) {
                if (tcgOnTheGo) { // add the last test case, and return
                    if (lastTestCaseOnThread != null)
                        methodSequences.add(lastTestCaseOnThread);
                    lastTestCaseOnThread = null;
                }
                return;
            }
        assert VeritestingListener.coverageCriteria == CoverageCriteria.BRANCHCOVERAGE : "Listener should only be used if we are generating branch coverage/testcases. Failing.";
        assert !BranchListener.tcgOnTheGo : "if onTheGo is turned on then, we must have covered JR obligations in the VeribranchListner, and covered the spf obligations when they are covered." +
                "thus we cannot get to that point of execution, as newCoverageFound should be false.";
       /* // if we are using on the go in spf mode, then we must have collected the unit tests and the coverages as well, without getting to the end of the path
        if(BranchListener.tcgOnTheGo && BranchListener.coverageMode == CoverageMode.SPF)
            return;*/

//        if (IncrementalListener.solver == null) {//call super to generate test cases in case it is non-incremental mode and we do want to generate testcases.
//            if (BranchListener.testCaseGenerationMode != TestCaseGenerationMode.NONE)
//                super.threadTerminated(vm, terminatedThread);
//            return;
//        }
        SystemState ss = vm.getSystemState();

        ChoiceGenerator<?> cg = vm.getChoiceGenerator();

        if (!(cg instanceof PCChoiceGenerator)) {
            ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
            while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
                prev_cg = prev_cg.getPreviousChoiceGenerator();
            }
            cg = prev_cg;
        }

        if ((cg instanceof PCChoiceGenerator) && ((PCChoiceGenerator) cg).getCurrentPC() != null) {

            PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
            //solve the path condition
            if (SymbolicInstructionFactory.concolicMode) { //TODO: cleaner
                assert false : "concolicMode is not supported with incremental solver. Failing.";
                SymbolicConstraintsGeneral solver = new SymbolicConstraintsGeneral();
                PCAnalyzer pa = new PCAnalyzer();
                pa.solve(pc, solver);
            }
//this is the place we want to get the attributes of the method calls that has occured so far in SequenceListener.
            List<String> attributes = new ArrayList<>();
            attributes = getMethodAttributes(vm.getChoiceGenerators());

            Map<String, Object> solution = null;
            if (IncrementalListener.solver != null) IncrementalListener.solver.push();
            long startTime = System.currentTimeMillis();
            solution = pc.solveWithValuations(attributes);
            long solvingTime = (System.currentTimeMillis() - startTime);
            recordSolvingInStatistics(terminatedThread.getPC(), solvingTime, terminatedThread.isTerminated(), false);
            assert (pc.count() == 0 || solution.size() > 0) : "At least one solution is expected. Something went wrong. Failing.";
            if (IncrementalListener.solver != null) IncrementalListener.solver.pop();

            // get the chain of choice generators.
            ChoiceGenerator<?>[] cgs = ss.getChoiceGenerators();
            methodSequences.add(getMethodSequence(cgs, solution));
        }
        BranchListener.newCoverageFound = false;
        //	}
    }

    public static void addOnTheGoMethodSequence(VM vm, Map<String, Object> solution) {
        System.out.println("generating test case");
        ChoiceGenerator<?>[] cgs = vm.getSystemState().getChoiceGenerators();
        lastTestCaseOnThread = getMethodSequence(cgs, solution);
//        methodSequences.add();
    }

    /**
     * traverses the ChoiceGenerator chain to get the method sequence
     * looks for SequenceChoiceGenerator in the chain
     * SequenceChoiceGenerators have information about the methods
     * executed and hence the method sequence can be obtained.
     * A single 'methodSequence' is a vector of invoked 'method's along a path
     * A single invoked 'method' is represented as a String.
     */
    public static List<String> getMethodAttributes(ChoiceGenerator[] cgs) {
        // A method sequence is a vector of strings
        ArrayList<String> methodSequence = new ArrayList<>();
        ChoiceGenerator cg = null;
        if (BranchListener.testCaseGenerationMode == TestCaseGenerationMode.UNIT_LEVEL)
            // explore the choice generator chain - unique for a given path.
            for (int i = 0; i < cgs.length; i++) {
                cg = cgs[i];
                if ((cg instanceof SequenceChoiceGenerator))
                    methodSequence.addAll(getInvokedMethodAttributes((SequenceChoiceGenerator) cg));
            }
        else if (BranchListener.testCaseGenerationMode == TestCaseGenerationMode.SYSTEM_LEVEL)
            methodSequence.addAll(getInvokedMethodAttributes(getFirstSequenceCG(cgs)));
        else return methodSequence;
        return methodSequence;
    }

    private static SequenceChoiceGenerator getFirstSequenceCG(ChoiceGenerator[] cgs) {
        for (int i = 0; i < cgs.length; i++)
            if (cgs[i] instanceof SequenceChoiceGenerator) return (SequenceChoiceGenerator) cgs[i];
        assert false : "at least one SequenceChoiceGenerator should be in the sequence, but found zero. Something went wrong. Failing.";
        return null;
    }

    /**
     * A single invoked 'method' is represented as a String.
     * information about the invoked method is got from the SequenceChoiceGenerator
     *
     * @return
     */
    private static List<String> getInvokedMethodAttributes(SequenceChoiceGenerator cg) {

        if (cg == null)
            assert false : "a null first sequence choice generator can only happen in the case we are having equivleance checking and executing the system level method concretely. Assumption Violated. Failing.";

        List<String> attributeNames = new ArrayList<>();

        // get symbolic attributes
        Object[] attributes = cg.getArgAttributes();

        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i] != null) { // parameter symbolic
                assert attributes[i] instanceof SymbolicInteger;
                attributeNames.add((attributes[i]).toString());
            }
        }
        return attributeNames;
    }

    /**
     * traverses the ChoiceGenerator chain to get the method sequence
     * looks for SequenceChoiceGenerator in the chain
     * SequenceChoiceGenerators have information about the methods
     * executed and hence the method sequence can be obtained.
     * A single 'methodSequence' is a vector of invoked 'method's along a path
     * A single invoked 'method' is represented as a String.
     */
    static Vector<String> getMethodSequence(ChoiceGenerator[] cgs, Map<String, Object> solution) {
        // A method sequence is a vector of strings
        Vector<String> methodSequence = new Vector<String>();
        ChoiceGenerator cg = null;

        if (BranchListener.testCaseGenerationMode == TestCaseGenerationMode.UNIT_LEVEL)
            // explore the choice generator chain - unique for a given path.
            for (int i = 0; i < cgs.length; i++) {
                cg = cgs[i];
                if ((cg instanceof SequenceChoiceGenerator)) {
                    methodSequence.add(getInvokedMethod((SequenceChoiceGenerator) cg, solution));
                }
            }
        else if (BranchListener.testCaseGenerationMode == TestCaseGenerationMode.SYSTEM_LEVEL)
            methodSequence.add(getInvokedMethod(getFirstSequenceCG(cgs), solution));

        return methodSequence;
    }

    /**
     * A single invoked 'method' is represented as a String.
     * information about the invoked method is got from the SequenceChoiceGenerator
     */
    private static String getInvokedMethod(SequenceChoiceGenerator cg, Map<String, Object> solutionMap) {
        String invokedMethod = "";

        // get method name
        String shortName = cg.getMethodShortName();
        invokedMethod += shortName + "(";

        // get argument values
        Object[] argValues = cg.getArgValues();

        // get number of arguments
        int numberOfArgs = argValues.length;

        // get symbolic attributes
        Object[] attributes = cg.getArgAttributes();

        // get the solution
        for (int i = 0; i < numberOfArgs; i++) {
            Object attribute = attributes[i];
            if (attribute != null) { // parameter symbolic
                // here we should consider different types of symbolic arguments
                //IntegerExpression e = (IntegerExpression)attributes[i];
                Object e = attributes[i];
                String solution = "";


                if (e instanceof IntegerExpression) {
                    // trick to print bools correctly
                    assert e instanceof SymbolicInteger : "unexpected symbolic type. Failing.";
                    Long value = ((Long) solutionMap.get(((SymbolicInteger) e).getName()));
                    if (argValues[i].toString().equals("true") || argValues[i].toString().equals("false")) {
                        if (value == null || value.intValue() == 0) solution = solution + "false";
                        else solution = solution + "true";
                    } else {
                        if (argValues[i] instanceof Character) { //converting the int value to its corresponding character.
                            if (value != null) {
                                String solverChar = specialcharToStr((char) value.intValue());
                                solution = solution + "'" + solverChar + "'";
                            } else solution = solution + "_"; //underscore presents dont care character.
                        } else {
                            if (value != null) solution = solution + solutionMap.get(((SymbolicInteger) e).getName());
                            else solution = solution + Integer.MIN_VALUE;
                        }
                    }
                } else assert false : "unsupported type";


                invokedMethod += solution + ",";
            } else { // parameter concrete - for a concrete parameter, the symbolic attribute is null
                if (argValues[i] instanceof Character) {
                    String outputChar = specialcharToStr((Character) argValues[i]);
                    invokedMethod += "'" + outputChar + "'" + ",";
                } else
                    invokedMethod += argValues[i] + ",";
            }
        }

        // remove the extra comma
        if (invokedMethod.endsWith(",")) invokedMethod = invokedMethod.substring(0, invokedMethod.length() - 1);
        invokedMethod += ")";

        return invokedMethod;
    }

//    representing the hexadecimal character in Java, which used \u0000 with
//      4 digits to represent the hexadecimal number. This choice was made
//      to avoid breaking the
//      backward compatibility of C which used \X00 with two hexadecimal numbers


    static String specialcharToStr(Character charInt) {
        String outputChar;
        switch (charInt) {
            case '\t':
                outputChar = "\\t";
                break;
            case '\u0000':
                outputChar = "\\u0000";
                break;
            case '\n':
                outputChar = "\\n";
                break;
            case '\b':
                outputChar = "\\b";
                break;
            case '\r':
                outputChar = "\\r";
                break;
            case '\\':
                outputChar = "\\\\";
                break;
            case '\'':
                outputChar = "\\'";
                break;
            default:
                outputChar = charInt.toString();
        }

        return outputChar;
    }

}
