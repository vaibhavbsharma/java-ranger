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

//Copyright (C) 2007 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.

//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.

//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.symbc.bytecode;


import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class IADD extends gov.nasa.jpf.jvm.bytecode.IADD {

    @Override
    public Instruction execute(ThreadInfo th) {
        StackFrame sf = th.getModifiableTopFrame();
        IntegerExpression sym_v1 = (IntegerExpression) sf.getOperandAttr(0);
        IntegerExpression sym_v2 = (IntegerExpression) sf.getOperandAttr(1);

        if (sym_v1 == null && sym_v2 == null)
            return super.execute(th); // we'll still do the concrete execution
        else {
            int v1 = sf.pop();
            int v2 = sf.pop();

            sf.push(v1 + v2, false);
            IntegerExpression result = null;
            String[] dp = SymbolicInstructionFactory.dp;

            if (dp[0].equalsIgnoreCase("z3bitvector") || dp[0].equalsIgnoreCase("z3bitvectorinc")) {
                if (sym_v1 != null) {
                    if (sym_v2 != null)
                        result = ((sym_v1._plus(sym_v2))._shiftL(32))._shiftR(32);
                    else // v2 is concrete
                        result = ((sym_v1._plus(v2))._shiftL(32))._shiftR(32);
                } else if (sym_v2 != null) {
                    result = ((sym_v2._plus(v1))._shiftL(32))._shiftR(32);
                }
            } else {
                if (sym_v1 != null) {
                    if (sym_v2 != null)
                        result = (sym_v1._plus(sym_v2));
                    else // v2 is concrete
                        result = (sym_v1._plus(v2));
                } else if (sym_v2 != null) {
                    result = (sym_v2._plus(v1));
                }
            }
            sf.setOperandAttr(result);

            //System.out.println("Execute IADD: "+result);

            return getNext(th);
        }
    }
}
