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
import gov.nasa.jpf.vm.*;


/**
 * Remainder float
 * ..., value1, value2 => ..., result
 */
public class FREM extends gov.nasa.jpf.jvm.bytecode.FREM  {

  @Override
  public Instruction execute (ThreadInfo th) {
   
    StackFrame sf = th.getModifiableTopFrame();

      RealExpression sym_v1 = (RealExpression) sf.getOperandAttr(0);
      float v1 = sf.peekFloat(0);
      RealExpression sym_v2 = (RealExpression) sf.getOperandAttr(1);
      float v2 = sf.peekFloat(1);

      if (sym_v1 == null) {
          Instruction next_insn = super.execute(th);
          if (sym_v2 != null) // result is symbolic expression
              sf.setOperandAttr(sym_v2._rem(v1));
          return next_insn;
      }

      ChoiceGenerator<?> cg;
      boolean condition;

      if (!th.isFirstStepInsn()) { // first time around
          cg = new PCChoiceGenerator(SymbolicInstructionFactory.collect_constraints ? 1 : 2);
          ((PCChoiceGenerator) cg).setOffset(this.position);
          ((PCChoiceGenerator) cg).setMethodName(this.getMethodInfo().getFullName());
          th.getVM().getSystemState().setNextChoiceGenerator(cg);
          return this;
      } else { // this is what really returns results
          cg = th.getVM().getSystemState().getChoiceGenerator();
          assert (cg instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: " + cg;
          if (SymbolicInstructionFactory.collect_constraints) {
              condition = v1 == 0; // i.e. false
              ((PCChoiceGenerator) cg).select(condition ? 1 : 0); // YN: set the choice correctly
          } else {
              condition = (Integer) cg.getNextChoice() != 0;
          }
      }

      super.execute(th); // pops v1, v2 and pushes r = v2 / v1;

      PathCondition pc;
      ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

      if (prev_cg == null)
          pc = new PathCondition();
      else
          pc = ((PCChoiceGenerator) prev_cg).getCurrentPC();

      assert pc != null;

      Comparator comparator = (condition) ? Comparator.EQ: Comparator.NE;

      pc._addDet(comparator, sym_v1, 0);
      if (pc.simplify()) { // satisfiable
          ((PCChoiceGenerator) cg).setCurrentPC(pc);

          // set the result
          RealExpression result;
          if (sym_v2 != null)
              result = sym_v2._rem(sym_v1);
          else
              result = sym_v1._rem_reverse(v2);

          sf = th.getModifiableTopFrame();
          sf.setOperandAttr(result);
          return getNext(th);

      } else {
          th.getVM().getSystemState().setIgnored(true);
          return getNext(th);
      }
//
//    if(sym_v1==null && sym_v2==null){
//        if (v1 == 0){
//            return th.createAndThrowException("java.lang.ArithmeticException","division by zero");
//        }
//        sf.push(Types.floatToInt(v2 % v1), false);
//    }else {
//    	sf.push(0, false);
//    	throw new RuntimeException("## Error: SYMBOLIC FREM not supported");
//    }
	
//    return getNext(th);
  }

}
