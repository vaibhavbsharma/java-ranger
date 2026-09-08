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
//Copyright (C) 2005 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.
//
//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.
//
//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.symbc.numeric;

public enum Comparator {

   EQ(" == ") { public Comparator not() { return NE; }},
   NE(" != ") { public Comparator not() { return EQ; }},
   LT(" < ")  { public Comparator not() { return GE; }},
   LE(" <= ") { public Comparator not() { return GT; }},
   GT(" > ")  { public Comparator not() { return LE; }},
   GE(" >= ") { public Comparator not() { return LT; }},
   // Unary comparators — these operate on a single expression
   // (right operand is null in Constraint).  Used by the NaN/Inf/zero/sign
   // class predicates of the FDIV/DDIV outcome encoding.  IS_POSITIVE /
   // IS_NEGATIVE follow the IEEE 754 sign bit: +0.0 is positive, -0.0 is
   // negative.
   IS_NAN(" IS_NAN ") { public Comparator not() { return NOT_IS_NAN; }},
   NOT_IS_NAN(" NOT_IS_NAN ") { public Comparator not() { return IS_NAN; }},
   IS_INF(" IS_INF ") { public Comparator not() { return NOT_IS_INF; }},
   NOT_IS_INF(" NOT_IS_INF ") { public Comparator not() { return IS_INF; }},
   IS_ZERO(" IS_ZERO ") { public Comparator not() { return NOT_IS_ZERO; }},
   NOT_IS_ZERO(" NOT_IS_ZERO ") { public Comparator not() { return IS_ZERO; }},
   IS_POSITIVE(" IS_POSITIVE ") { public Comparator not() { return IS_NEGATIVE; }},
   IS_NEGATIVE(" IS_NEGATIVE ") { public Comparator not() { return IS_POSITIVE; }};

   private final String str;

   Comparator(String str){
	   this.str= str;
   }
   
   public abstract Comparator not();
   
   @Override
   public String toString() {
	 return str;
   }

	/**
	 * Apply this comparator to the given operands.
	 * 
	 * @param left
	 *            the left operand
	 * @param right
	 *            the right operand
	 * @return <code>true</code> if and only if the operands satisfy this
	 *         comparator
	 */
	public boolean evaluate(double left, double right) {
		switch (this) {
		case EQ:
			return left == right;
		case NE:
			return left != right;
		case LT:
			return left < right;
		case LE:
			return left <= right;
		case GT:
			return left > right;
		case GE:
			return left >= right;
		default:
			assert false : "Not expecting to evaluate a unary comparator with two arguments";
			return false;
		}
	}
	public boolean evaluate(double x){
		switch (this){
			case IS_NAN:
				return Double.isNaN(x);
			case NOT_IS_NAN:
				return !Double.isNaN(x);
			case IS_INF:
				return Double.isInfinite(x);
			case NOT_IS_INF:
				return !Double.isInfinite(x);
			case IS_ZERO:
				return x == 0;
			case NOT_IS_ZERO:
				return x != 0;
			case IS_POSITIVE:
				// IEEE 754 sign: +0.0 is positive, -0.0 is not
				return (Double.doubleToRawLongBits(x) & 0x8000000000000000L) == 0L && !Double.isNaN(x);
			case IS_NEGATIVE:
				return (Double.doubleToRawLongBits(x) & 0x8000000000000000L) != 0L;
			default:
				assert false : "Not expecting to evaluate a binary comparator with one argument";
			return false;
		}
	}
}