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
//Copyright (C) 2006 United States Government as represented by the
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

import gov.nasa.jpf.symbc.numeric.Comparator;
import java.util.Map;

public abstract class Constraint implements Comparable<Constraint> {
  private final Expression left;

  private Comparator comp;

  private final Expression right;

  public Constraint and;


  public Constraint(Expression l, Comparator c, Expression r) {
    left = l;
    comp = c;
    right = r;
  }
  /**
   * Creates a unary constraint (no right operand).
   * NaN/Inf checks (IS_NAN, NOT_IS_NAN, IS_INF, NOT_IS_INF)
   * are predicates on a single expression — they do not compare
   * two values.  Setting {@code right = null} distinguishes them
   * from binary constraints (EQ, NE, LT, etc.) in {@link #compareTo}
   * and {@link #toString}.
   *
   * @param l  the expression to test
   * @param c  must be one of IS_NAN, NOT_IS_NAN, IS_INF, NOT_IS_INF
   */
  public Constraint(Expression l, Comparator c){
	left = l;
	comp = c;
	right = null;
	assert (c == Comparator.IS_NAN) || (c == Comparator.NOT_IS_NAN)
		|| (c == Comparator.IS_INF) || (c == Comparator.NOT_IS_INF);
  }

  /** Returns the left expression. Subclasses may override to give tighter type bounds.*/
  public Expression getLeft() {
      return left;
  }

  /** Returns the right expression. Subclasses may override to give tighter type bounds.*/
  public Expression getRight() {
      return right;
  }

  /**
   * Returns the comparator used in this constraint.
   */
  public Comparator getComparator() {
    return comp;
  }

  public void setComparator(Comparator c) {
	    comp = c;
	  }
  /**
   * Returns the negation of this constraint, but without the tail.
   */
  public abstract Constraint not();

  /**
   * Returns the next conjunct.
   */
  public Constraint getTail() {
    return and;
  }

  public String stringPC() {
    return left.stringPC() + comp.toString() + right.stringPC()
        + ((and == null) ? "" : " && " + and.stringPC());
  }

  public void getVarVals(Map<String,Object> varsVals) {
	  if (left != null) {
		  left.getVarsVals(varsVals);
	  }
	  if (right != null) {
		  right.getVarsVals(varsVals);
	  }
	  if (and != null) {
		  and.getVarVals(varsVals);
	  }
  }

  public boolean equals(Object o) {

	  if (o == null){
		  return false;
	  }

	  if (!(o instanceof Constraint)) {
      return false;
    }

    return left.equals(((Constraint) o).left)
        && comp.equals(((Constraint) o).comp)
        && right.equals(((Constraint) o).right);
  }

  public int hashCode() {
	  int result = Integer.MAX_VALUE;
	  if (left != null) {
		  result = result ^ left.hashCode();
	  }
	  if (comp != null) {
		  result = result ^ comp.hashCode();
	  }
	  if (right != null) {
		  result = result ^ right.hashCode();
	  }
	  return result;
	  //return left.hashCode() ^ comp.hashCode() ^ right.hashCode();
  }

  /**
	 * Lexicographic comparison of the 3-tuple ({@code comp}, {@code left},
	 * {@code right}).  The {@code right} entry may be null for unary
	 * constraints (IS_NAN, IS_INF, etc.); null sorts before non-null so
	 * unary constraints precede binary ones when the first two elements
	 * tie.  Used by {@link PathCondition#compareTo}.
	 * @param c  the constraint to compare to (must not be null)
	 * @return -1 / 0 / +1 per {@link Comparable} contract
	 */
	@Override
	public final int compareTo(Constraint c) {
		int r = comp.compareTo(c.getComparator());
		if (r == 0) {
			r = left.compareTo(c.getLeft());
			if (r == 0) {
				if (right == null && c.getRight() == null)
					r = 0;
				else if (right == null)
					r = -1;
				else if (c.getRight() == null)
					r = 1;
				else
					r = right.compareTo(c.getRight());
			}
		}
		return r;
	}
  
  public String toString() {
    if (right == null)
      return comp.toString() + "(" + left.toString() + ")"
          + ((and == null) ? "" : " &&\n" + and.toString());
    return left.toString() + comp.toString() + right.toString()
        + ((and == null) ? "" : " &&\n" + and.toString());
  }

  public Constraint last() {
      Constraint c= this;
      while(c.and != null) {
          c = c.and;
      }
      return c;
  }
  
//JacoGeldenhuys
	public void accept(ConstraintExpressionVisitor visitor) {
		visitor.preVisit(this);
		left.accept(visitor);
		right.accept(visitor);
		visitor.postVisit(this);
	}

	public String prefix_notation() {
		//return left.toString() + comp.toString() + right.toString()
		        //+ ((and == null) ? "" : " && " + and.toString()); -- for specialization
		  //      + ((and == null) ? "" : " &&\n" + and.toString());
		// Sang: rewrite NE in z3's notation: (a != b) becomes (not (= a b))
		String result = null;
		if (comp == Comparator.NE){
			result = "(not ( = " + left.prefix_notation() + " " + right.prefix_notation() +"))";
		}
		else{
			result = " ("+ comp.toString() +" "+ left.prefix_notation() +" " + right.prefix_notation() +")";
		}
		if(and!=null) result = "(and "+and.prefix_notation()+" "+result+")";
		return result;
	}

	public String prefix_notationPC4Z3() {
		//return left.toString() + comp.toString() + right.toString()
		        //+ ((and == null) ? "" : " && " + and.toString()); -- for specialization
		  //      + ((and == null) ? "" : " &&\n" + and.toString());
		// Sang: rewrite NE in z3's notation: (a != b) becomes (not (= a b))
		String result = null;
		if (comp == Comparator.NE){
			result = "(assert (not ( = " + left.prefix_notation() + " " + right.prefix_notation() +")))";
		}
		else{
			result = "(assert ("+ comp.toString() +" "+ left.prefix_notation() +" " + right.prefix_notation() +"))";
		}
		if(and!=null) result = result +"\n" + and.prefix_notationPC4Z3();
		return result;
	}

}