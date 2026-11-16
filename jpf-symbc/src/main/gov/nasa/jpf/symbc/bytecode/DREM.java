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
// Copyright (C) 2006 United States Government as represented by the
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
//
package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.FPClassExpr;
import gov.nasa.jpf.symbc.numeric.GreenConstraint;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.symbc.veritesting.VeritestingUtil.ExprUtil;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.RealConstant;
import za.ac.sun.cs.green.expr.RealVariable;

/**
 * IEEE 754 double remainder, result = A % B.  With at least one symbolic
 * operand, the JVM's concrete drem semantics are explored as three outcome
 * arms, each guarded by unary operand-class predicates (FPClassExpr), so the
 * path condition carries no nondeterministic real-remainder term in the NaN
 * and dividend-preserving arms and, in the normal arm, a single fp.rem term:
 *
 *   1  NaN          isNaN(A) || isNaN(B) || isZero(B) || isInfinity(A)
 *   2  = A          isInfinity(B) && !isNaN(A) && !isInfinity(A)
 *   3  A % B        !isNaN(A) && !isNaN(B) && !isZero(B) &&
 *                   !isInfinity(A) && !isInfinity(B)
 *
 * The arms partition every (A, B) pair.  Choice 1 is a pure operand-class
 * precondition (fp.eq makes result == NaN unsatisfiable), so execute() pushes
 * a concrete NaN for it; choices 2 and 3 bind the result variable to A and to
 * A % B (fp.rem is exact, so it matches the JVM's fmod semantics).
 */
public class DREM extends gov.nasa.jpf.jvm.bytecode.DREM {

    private static int resultCounter = 0;

    private static final double SYM_MIN = -Double.MAX_VALUE;
    private static final double SYM_MAX = Double.MAX_VALUE;

    @Override
    public Instruction execute(ThreadInfo th) {
        StackFrame sf = th.getModifiableTopFrame();

        RealExpression sym_v2 = (RealExpression) sf.getOperandAttr(3); // dividend
        double v2 = sf.peekDouble(2);
        RealExpression sym_v1 = (RealExpression) sf.getOperandAttr(1); // divisor
        double v1 = sf.peekDouble();

        // Both operands concrete: plain IEEE 754 drem.
        if (sym_v1 == null && sym_v2 == null)
            return super.execute(th);

        ChoiceGenerator<?> cg;

        if (!th.isFirstStepInsn()) { // first time around
            cg = new PCChoiceGenerator(SymbolicInstructionFactory.collect_constraints ? 1 : 3);
            ((PCChoiceGenerator) cg).setOffset(this.position);
            ((PCChoiceGenerator) cg).setMethodName(this.getMethodInfo().getFullName());
            th.getVM().getSystemState().setNextChoiceGenerator(cg);
            return this;
        }

        cg = th.getVM().getSystemState().getChoiceGenerator();
        assert (cg instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: " + cg;

        int choice;
        if (SymbolicInstructionFactory.collect_constraints) {
            // Replay the arm matching the concrete (random) trace.
            choice = classify(v2, v1);
            ((PCChoiceGenerator) cg).select(choice - 1);
        } else {
            choice = ((Integer) cg.getNextChoice()) + 1;
        }

        PathCondition pc;
        ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

        if (prev_cg == null)
            pc = new PathCondition();
        else
            pc = ((PCChoiceGenerator) prev_cg).getCurrentPC();

        assert pc != null;

        // Operands as green expressions (RealVariable for a symbolic operand,
        // RealConstant for a concrete sibling).
        Expression gA = toGreen(sym_v2, v2);
        Expression gB = toGreen(sym_v1, v1);

        // Bind the result by the chosen arm's flat formula (class javadoc).
        // A mismatched arm is unsatisfiable and dropped below.  The NaN arm
        // (choice 1) is a pure operand precondition and pushes a concrete NaN.
        String varId = "drem_" + resultCounter++;
        Expression resultGreen = ExprUtil.createGreenVar("float", varId);
        Expression identity = buildArmForChoice(choice, resultGreen, gA, gB);
        pc._addDet(new GreenConstraint(identity));

        if (pc.simplify()) { // arm satisfiable under the operand bounds
            ((PCChoiceGenerator) cg).setCurrentPC(pc);

            sf = th.getModifiableTopFrame();
            sf.popDouble();
            sf.popDouble();
            sf.pushDouble(resultValue(choice, v2, v1));
            if (choice != 1)
                sf.setLongOperandAttr(new SymbolicReal(varId, SYM_MIN, SYM_MAX));
            return getNext(th);
        } else { // infeasible arm
            th.getVM().getSystemState().setIgnored(true);
            return getNext(th);
        }
    }

    // ---------------- arm builders ----------------

    // Choice 1 (NaN).  Pure operand-class precondition: either operand is NaN,
    // the divisor is (signed) zero, or the dividend is infinite.  The result
    // is not bound here; execute() pushes a concrete NaN.
    private static Expression buildNaN(Expression gA, Expression gB) {
        return or(isNan(gA), isNan(gB), isZero(gB), isInfinity(gA));
    }

    // Choice 2 (result = dividend).  Infinite divisor with a finite, non-NaN
    // dividend: IEEE 754 remainder returns the dividend unchanged.
    private static Expression buildDividend(Expression result, Expression gA, Expression gB) {
        return and(isInfinity(gB), notNan(gA), notInfinity(gA),
                eq(result, gA));
    }

    // Choice 3 (normal) -- exact fp remainder.  fp.rem rounds the quotient
    // toward zero and is exact, so it matches Java's fmod semantics.
    private static Expression buildNormal(Expression result, Expression gA, Expression gB) {
        return and(notNan(gA), notNan(gB), notZero(gB), notInfinity(gA), notInfinity(gB),
                eq(result, new Operation(Operation.Operator.MOD, gA, gB)));
    }

    private static Expression buildArmForChoice(int choice, Expression result, Expression gA, Expression gB) {
        switch (choice) {
        case 1: return buildNaN(gA, gB);
        case 2: return buildDividend(result, gA, gB);
        default: return buildNormal(result, gA, gB); // choice 3
        }
    }

    // ---------------- expression helpers ----------------

    private static Expression toGreen(RealExpression sym, double v) {
        if (sym == null)
            return new RealConstant(v);
        if (sym instanceof SymbolicReal)
            return new RealVariable(((SymbolicReal) sym).getName(),
                    ((SymbolicReal) sym)._min, ((SymbolicReal) sym)._max);
        return ExprUtil.SPFToGreenExpr(sym);
    }

    // Unary FP class predicates -- FPClassExpr is a leaf that GreenPbTranslator
    // maps to Z3's mkFPIsNaN/mkFPIsInfinite/... (see postVisitFPClass).
    private static Expression isNan(Expression e)   { return new FPClassExpr(e, Comparator.IS_NAN); }
    private static Expression notNan(Expression e)  { return new FPClassExpr(e, Comparator.NOT_IS_NAN); }
    private static Expression isZero(Expression e)  { return new FPClassExpr(e, Comparator.IS_ZERO); }
    private static Expression notZero(Expression e) { return new FPClassExpr(e, Comparator.NOT_IS_ZERO); }
    private static Expression isInfinity(Expression e)   { return new FPClassExpr(e, Comparator.IS_INFINITY); }
    private static Expression notInfinity(Expression e)  { return new FPClassExpr(e, Comparator.NOT_IS_INFINITY); }

    private static Expression and(Expression... es) {
        Expression r = es[0];
        for (int i = 1; i < es.length; i++)
            r = new Operation(Operation.Operator.AND, r, es[i]);
        return r;
    }

    private static Expression or(Expression... es) {
        Expression r = es[0];
        for (int i = 1; i < es.length; i++)
            r = new Operation(Operation.Operator.OR, r, es[i]);
        return r;
    }

    private static Expression eq(Expression lhs, Expression rhs) {
        return new Operation(Operation.Operator.EQ, lhs, rhs);
    }

    // ---------------- concrete classification / replay ----------------

    private static int classify(double v2, double v1) {
        if (Double.isNaN(v2) || Double.isNaN(v1) || v1 == 0.0d || Double.isInfinite(v2))
            return 1;
        if (Double.isInfinite(v1))
            return 2;
        return 3;
    }

    private static double resultValue(int choice, double v2, double v1) {
        if (choice == 1)
            return Double.NaN;
        return v2 % v1;
    }
}