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
 * YN: fixed choice selection in symcrete support (Yannic Noller <nolleryc@gmail.com>)
 *
 * IEEE 754 division result = A / B.  With at least one symbolic operand, the
 * JVM's concrete ddiv semantics (x/0 = +-Inf, 0/0 = NaN, x/+-Inf = +-0,
 * finite/finite = a rounded real) are explored as six outcome arms, each
 * guarded by unary *operand* class predicates (on A and B), so the path
 * condition never carries an fp.div term -- which the FCMP/veritesting
 * harness cannot solve efficiently:
 *
 *   1  NaN       isNaN(A) || isNaN(B) || (isZero(A)&&isZero(B)) ||
 *                (isInf(A)&&isInf(B))
 *   2  +Inf      zero divisor with non-zero, non-infinite dividend, or
 *                infinite dividend -- matching operand signs
 *   3  -Inf      the same shapes -- differing operand signs
 *   4  +0.0      infinite divisor with finite dividend -- matching signs
 *   5  -0.0      the same shape -- differing signs
 *   6  normal    finite / finite (non-zero divisor), a real number
 *
 * Each arm is posted to the path condition as one flat GreenConstraint:
 * Phi_i /\ (resultGreen == <class>), a conjunction of operand-class predicates
 * (FPClassExpr) with an equality -- no ITE.  Only the normal arm (choice 6)
 * carries an fp.div term.  Guards mirror the Phi spec above verbatim and
 * conjoin the operand sign relation that selects the result class; a concrete
 * class representative is pushed on the stack so downstream concrete execution
 * (and symcrete replay) sees the correct IEEE 754 sign.
 *
 * The arm formulas are load-bearing: a path whose arm Phi does not hold is
 * unsatisfiable and pruned by pc.simplify() in execute().  The Inf/(+-0) corner
 * (infinite dividend, zero divisor) is +-Inf under IEEE 754's sign rule, so it
 * is an extra disjunct in choices 2/3.  Choice 1 (NaN) cannot bind result ==
 * NaN by equality -- fp.eq makes it unsatisfiable (NaN != NaN) -- it binds via
 * the isNaN(result) predicate instead.
 */
public class DDIV extends gov.nasa.jpf.jvm.bytecode.DDIV {

    private static int resultCounter = 0;

    private static final double SYM_MIN = -Double.MAX_VALUE;
    private static final double SYM_MAX = Double.MAX_VALUE;

    @Override
    public Instruction execute(ThreadInfo th) {
        StackFrame sf = th.getModifiableTopFrame();

        // Both operands are 2-slot doubles: the divisor sits on top (slots
        // top,top-1), the dividend just below (slots top-2,top-3).  JPF stashes
        // the symbolic attribute on the second slot of each pair, so the
        // divisor's attr lives at offset 1 and the dividend's at offset 3.
        RealExpression sym_v1 = (RealExpression) sf.getOperandAttr(1); // divisor
        double v1 = sf.peekDouble();
        RealExpression sym_v2 = (RealExpression) sf.getOperandAttr(3); // dividend
        double v2 = sf.peekDouble(2);

        // Both operands concrete: plain IEEE 754 ddiv.
        if (sym_v1 == null && sym_v2 == null)
            return super.execute(th);

        ChoiceGenerator<?> cg;

        if (!th.isFirstStepInsn()) { // first time around
            cg = new PCChoiceGenerator(SymbolicInstructionFactory.collect_constraints ? 1 : 6);
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

        // Bind the result by the chosen arm's flat Phi formula (class javadoc):
        // operand-class predicates conjoined with result == <class>.  A
        // mismatched arm is unsatisfiable and dropped below.  The NaN arm
        // (choice 1) binds via the isNaN(result) predicate -- fp.eq makes
        // result == NaN unsatisfiable.
        String varId = "ddiv_" + resultCounter++;
        Expression resultGreen = ExprUtil.createGreenVar("double", varId);
        Expression identity = buildArmForChoice(choice, resultGreen, gA, gB);
        pc._addDet(new GreenConstraint(identity));

        if (pc.simplify()) { // arm satisfiable under the operand bounds
            ((PCChoiceGenerator) cg).setCurrentPC(pc);

            sf = th.getModifiableTopFrame();
            sf.popDouble();
            sf.popDouble();
            sf.pushDouble(resultValue(choice, v2, v1));
            sf.setLongOperandAttr(new SymbolicReal(varId, SYM_MIN, SYM_MAX));
            return getNext(th);
        } else { // infeasible arm
            th.getVM().getSystemState().setIgnored(true);
            return getNext(th);
        }
    }
// ---------------- arm builders ----------------

    // Each arm posts Phi_i /\ (result == <class>): a flat conjunction of unary
    // operand-class predicates (FPClassExpr) and an equality -- no ITE.  The
    // Phi_i formulas are listed in the class javadoc; a mismatched arm is
    // unsatisfiable and pruned by pc.simplify() in execute().  Only the normal
    // arm (choice 6) carries an fp.div term.

    // Choice 1 (NaN).  Bound by the isNaN(result) predicate, not an equality:
    // fp.eq makes result == NaN unsatisfiable (IEEE 754: NaN != NaN).
    private static Expression buildNaN(Expression result, Expression gA, Expression gB) {
        return and(new FPClassExpr(result, Comparator.IS_NAN),
                or(isNan(gA), isNan(gB),
                   and(isZero(gA), isZero(gB)),
                   and(isInf(gA), isInf(gB))));
    }

    // Choice 2 (+Inf).  The third guard disjunct is the (Inf)/(0) corner --
    // +Inf under IEEE 754's sign rule (JVM: x/0 = +-Inf).
    private static Expression buildPosInf(Expression result, Expression gA, Expression gB) {
        return and(notNan(gA), notNan(gB),
                or(and(isZero(gB), notZero(gA), notInf(gA)),
                   and(isInf(gA), notInf(gB), notZero(gB)),
                   and(isInf(gA), isZero(gB))),
                sameSign(gA, gB),
                eq(result, constOf(RealExpression.POS_INF)));
    }

    // Choice 3 (-Inf).
    private static Expression buildNegInf(Expression result, Expression gA, Expression gB) {
        return and(notNan(gA), notNan(gB),
                or(and(isZero(gB), notZero(gA), notInf(gA)),
                   and(isInf(gA), notInf(gB), notZero(gB)),
                   and(isInf(gA), isZero(gB))),
                diffSign(gA, gB),
                eq(result, constOf(RealExpression.NEG_INF)));
    }

    // Choice 4 (+0.0).
    private static Expression buildPosZero(Expression result, Expression gA, Expression gB) {
        return and(notNan(gA), notNan(gB), isInf(gB), notInf(gA),
                sameSign(gA, gB),
                eq(result, constOf(RealExpression.POS_ZERO)));
    }

    // Choice 5 (-0.0).
    private static Expression buildNegZero(Expression result, Expression gA, Expression gB) {
        return and(notNan(gA), notNan(gB), isInf(gB), notInf(gA),
                diffSign(gA, gB),
                eq(result, constOf(RealExpression.NEG_ZERO)));
    }

    // Choice 6 (normal) -- the only arm carrying an fp.div term.
    private static Expression buildNormal(Expression result, Expression gA, Expression gB) {
        return and(notNan(gA), notNan(gB), notZero(gB), notInf(gA), notInf(gB),
                eq(result, new Operation(Operation.Operator.DIV, gA, gB)));
    }

    private static Expression buildArmForChoice(int choice, Expression result, Expression gA, Expression gB) {
        switch (choice) {
        case 1: return buildNaN(result, gA, gB);
        case 2: return buildPosInf(result, gA, gB);
        case 3: return buildNegInf(result, gA, gB);
        case 4: return buildPosZero(result, gA, gB);
        case 5: return buildNegZero(result, gA, gB);
        default: return buildNormal(result, gA, gB); // choice 6
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

    private static Expression constOf(RealExpression r) {
        return ExprUtil.SPFToGreenExpr(r);
    }

    // Unary FP class predicates -- FPClassExpr is a leaf that GreenPbTranslator
    // maps to Z3's mkFPIsNaN/mkFPIsInfinite/... (see postVisitFPClass).
    private static Expression isNan(Expression e)   { return new FPClassExpr(e, Comparator.IS_NAN); }
    private static Expression notNan(Expression e)  { return new FPClassExpr(e, Comparator.NOT_IS_NAN); }
    private static Expression isZero(Expression e)  { return new FPClassExpr(e, Comparator.IS_ZERO); }
    private static Expression notZero(Expression e) { return new FPClassExpr(e, Comparator.NOT_IS_ZERO); }
    private static Expression isInf(Expression e)   { return new FPClassExpr(e, Comparator.IS_INF); }
    private static Expression notInf(Expression e)  { return new FPClassExpr(e, Comparator.NOT_IS_INF); }
    private static Expression isPos(Expression e)   { return new FPClassExpr(e, Comparator.IS_POSITIVE); }
    private static Expression isNeg(Expression e)   { return new FPClassExpr(e, Comparator.IS_NEGATIVE); }

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

    // Sign(A) == Sign(B).  isPos/isNeg read the sign bit (Z3 fp.isPositive /
    // fp.isNegative): isPositive(+0.0) is true, isNegative(-0.0) is true, and
    // NaN is neither, so the identities hold on the classes above.
    private static Expression sameSign(Expression a, Expression b) {
        return or(and(isPos(a), isPos(b)), and(isNeg(a), isNeg(b)));
    }

    // Sign(A) != Sign(B)
    private static Expression diffSign(Expression a, Expression b) {
        return or(and(isPos(a), isNeg(b)), and(isNeg(a), isPos(b)));
    }

    // ---------------- concrete classification / replay ----------------

    private static int classify(double v2, double v1) {
        double r = v2 / v1;
        if (Double.isNaN(r))
            return 1;
        if (Double.isInfinite(r))
            return r > 0 ? 2 : 3;
        if (r == 0.0d)
            return Double.doubleToRawLongBits(r) >= 0 ? 4 : 5;
        return 6;
    }

    private static double resultValue(int choice, double v2, double v1) {
        switch (choice) {
        case 1: return Double.NaN;
        case 2: return Double.POSITIVE_INFINITY;
        case 3: return Double.NEGATIVE_INFINITY;
        case 4: return 0.0d;
        case 5: return -0.0d;
        default: return v2 / v1;
        }
    }
}