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
package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * NaN-aware 4-branch CG: NaN / LT / EQ / GT (IEEE 754).
 * DCMPG: NaN → +1
 */
public class DCMPG extends gov.nasa.jpf.jvm.bytecode.DCMPG {

    @Override
    public Instruction execute(ThreadInfo th) {
        StackFrame sf = th.getModifiableTopFrame();

        RealExpression sym_v1 = (RealExpression) sf.getOperandAttr(1);
        RealExpression sym_v2 = (RealExpression) sf.getOperandAttr(3);

        if (sym_v1 == null && sym_v2 == null) {
            return super.execute(th);
        }

        ChoiceGenerator<?> cg;
        int choice;

        if (!th.isFirstStepInsn()) {
            cg = new PCChoiceGenerator(SymbolicInstructionFactory.collect_constraints ? 1 : 4);
            ((PCChoiceGenerator) cg).setOffset(this.position);
            ((PCChoiceGenerator) cg).setMethodName(this.getMethodInfo().getFullName());
            th.getVM().getSystemState().setNextChoiceGenerator(cg);
            return this;
        }

        double v1 = sf.popDouble();
        double v2 = sf.popDouble();

        cg = th.getVM().getSystemState().getChoiceGenerator();
        assert (cg instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: " + cg;

        if (SymbolicInstructionFactory.collect_constraints) {
            if (Double.isNaN(v1) || Double.isNaN(v2))
                choice = 0;
            else if (v2 < v1)
                choice = 1;
            else if (v2 == v1)
                choice = 2;
            else
                choice = 3;
            ((PCChoiceGenerator) cg).select(choice);
        } else {
            choice = (Integer) cg.getNextChoice();
        }

        PathCondition pc;
        ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

        if (prev_cg == null)
            pc = new PathCondition();
        else
            pc = ((PCChoiceGenerator) prev_cg).getCurrentPC();

        assert pc != null;

        if (choice == 0) { // at least one operand is NaN
            if (sym_v1 != null)
                pc._addDet(sym_v1, Comparator.IS_NAN);
            if (sym_v2 != null)
                pc._addDet(sym_v2, Comparator.IS_NAN);
            if (!pc.simplify()) {
                th.getVM().getSystemState().setIgnored(true);
            } else {
                ((PCChoiceGenerator) cg).setCurrentPC(pc);
            }
            sf.push(1, false);
        } else {
            if (sym_v1 != null)
                pc._addDet(sym_v1, Comparator.NOT_IS_NAN);
            if (sym_v2 != null)
                pc._addDet(sym_v2, Comparator.NOT_IS_NAN);

            if (choice == 1) { // v2 < v1
                if (sym_v1 != null) {
                    if (sym_v2 != null)
                        pc._addDet(Comparator.LT, sym_v2, sym_v1);
                    else
                        pc._addDet(Comparator.LT, v2, sym_v1);
                } else
                    pc._addDet(Comparator.LT, sym_v2, v1);
                if (!pc.simplify()) {
                    th.getVM().getSystemState().setIgnored(true);
                } else {
                    ((PCChoiceGenerator) cg).setCurrentPC(pc);
                }
                sf.push(-1, false);
            } else if (choice == 2) { // v2 == v1
                if (sym_v1 != null) {
                    if (sym_v2 != null)
                        pc._addDet(Comparator.EQ, sym_v1, sym_v2);
                    else
                        pc._addDet(Comparator.EQ, sym_v1, v2);
                } else
                    pc._addDet(Comparator.EQ, v1, sym_v2);
                if (!pc.simplify()) {
                    th.getVM().getSystemState().setIgnored(true);
                } else {
                    ((PCChoiceGenerator) cg).setCurrentPC(pc);
                }
                sf.push(0, false);
            } else { // choice == 3, v2 > v1
                if (sym_v1 != null) {
                    if (sym_v2 != null)
                        pc._addDet(Comparator.GT, sym_v2, sym_v1);
                    else
                        pc._addDet(Comparator.GT, v2, sym_v1);
                } else
                    pc._addDet(Comparator.GT, sym_v2, v1);
                if (!pc.simplify()) {
                    th.getVM().getSystemState().setIgnored(true);
                } else {
                    ((PCChoiceGenerator) cg).setCurrentPC(pc);
                }
                sf.push(1, false);
            }
        }

        return getNext(th);
    }
}
