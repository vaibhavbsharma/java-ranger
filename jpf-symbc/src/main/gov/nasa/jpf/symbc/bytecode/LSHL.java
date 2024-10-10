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

import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;


/**
 * Shift left
 * ..., value1, value2  =>..., result
 */
public class LSHL extends gov.nasa.jpf.jvm.bytecode.LSHL {
	@Override
  public Instruction execute (ThreadInfo th) {
	  StackFrame sf = th.getModifiableTopFrame();

		IntegerExpression sym_v1 = (IntegerExpression) sf.getOperandAttr(0);
		IntegerExpression sym_v2 = (IntegerExpression) sf.getOperandAttr(2);

	    if(sym_v1==null && sym_v2==null)
	        return super.execute(th);// we'll still do the concrete execution
	    else {
	    	int v1 = sf.pop();
	    	long v2 = sf.popLong();
	    	sf.pushLong(v2 << v1);

	    	Expression result = null;
	    	if (sym_v1 != null) {
	    		if (sym_v2 != null) {
					//result = sym_v1._shiftL(sym_v2);
					BinaryLinearIntegerExpression sym_shift_val = new BinaryLinearIntegerExpression((IntegerExpression) sym_v1, Operator.AND, new IntegerConstant(63));
					result = sym_v2._shiftL(sym_shift_val);
				}
	    		else { // v2 is concrete
					//result = sym_v1._shiftL(v2);
					BinaryLinearIntegerExpression sym_shift_val = new BinaryLinearIntegerExpression((IntegerExpression) sym_v1, Operator.AND, new IntegerConstant(63));

					result = new IntegerConstant(v2)._shiftL(sym_shift_val);

//					BinaryRealExpression sym_shift_val = new BinaryRealExpression(new )
//					result = new RealConstant(v2)._shiftL(sym_shift_val);


				}
	    	}
	    	else if (sym_v2 != null) {
				result = new IntegerConstant(v2)._shiftL(v1 & 63);
	    	}

	    	sf.setLongOperandAttr(result);
	    	return getNext(th);
	    }
  }
}
