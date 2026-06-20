package edu.ucsb.cs.vlab.translate.smtlib.generic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.ucsb.cs.vlab.translate.NormalFormTranslator;
import edu.ucsb.cs.vlab.translate.smtlib.Results;
import edu.ucsb.cs.vlab.translate.smtlib.TranslationManager;
import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.SymbolicCharAtInteger;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.RealVariable;

public abstract class NumericConstraintTranslator extends NormalFormTranslator<Constraint, Comparator, String> {
	public NumericConstraintTranslator(TranslationManager manager) {
		super((Constraint c) -> {
			return c.getComparator().toString() + "-not-implemented";
		}, manager);
	}

	public String collect(Constraint npc) {
		return translate(npc).stream().map((s) -> {
			return "(assert " + s + ")";
		}).collect(Collectors.joining("\n"));
	}

	@Override
	public Comparator getKeyFrom(Constraint instance) {
		return instance.getComparator();
	}

	@Override
	public List<String> transformChain(Constraint instance, List<String> collection) {
		if (instance == null)
			return collection;
		if (instance instanceof RealConstraint){
			// throw new UnsupportedOperationException(" Constraints are being dropped");
			return transformChain(instance.getTail(), collection);
		} else
			if (!GreenConstraint.class.isInstance(instance))
			collection.add(transform(instance));
		else {
			// TODO translate GreenConstraint to a solver string
			GreenConstraint gC = (GreenConstraint) instance;
			System.out.println(gC);
			if (gC.and instanceof LinearIntegerConstraint)
				collection.add(transform(gC.and));
			collection.add(transformGreenExp(gC.getExp()));
		}
		return transformChain(instance.getTail(), collection);
	}

	private String transformGreenExp(za.ac.sun.cs.green.expr.Expression exp) {
		if (exp instanceof Operation) {
			Operation op = (Operation) exp;
			String opString = getOpString(op.getOperator());
			String ret =  "(" + opString;
			for (int i = 0; i < op.getArity(); i++){
				if(op.getArity() == 2 && i == 0
						&& op.getOperand(0) instanceof IntVariable
						&& ((IntVariable) op.getOperand(0)).getOriginal() instanceof SymbolicCharAtInteger
						&& op.getOperand(1) instanceof IntConstant){
					ret += " (str.to_code " + transformGreenExp(op.getOperand(i)) + ")";
				}
				else{
					String transformedExp = transformGreenExp(op.getOperand(i));
					Pattern pattern = Pattern.compile("CharAt\\((\\w+)\\)_\\w+_==(\\d+)");
					Matcher matcher = pattern.matcher(exp.toString());
					if( matcher.matches()){
						System.out.println("Group 2: " + matcher.group(2));
						String strValue = matcher.group(2);
						try{
							int intValue = Integer.parseInt(strValue);
							char charValue = (char) intValue;
							transformedExp="\""+transformedExp.replaceAll(strValue, String.valueOf(charValue)) +"\"";
							ret=ret.replaceAll("\\(str.to_code","");
							ret=ret.substring(0,ret.length()-1);
						}catch (NumberFormatException e){
							assert false: "this should not happen, as it is based on the RE definition this group should be a number";
						}
				}
					ret += " " + transformedExp;
				}
			}
			ret += ")" + (opString.contains("(=") ? ")" : "");
			return ret;
		} else {
			if (exp instanceof IntVariable){
				Object orig = ((IntVariable) exp).getOriginal();
				if(orig instanceof SymbolicCharAtInteger)
					return manager.numExpr.collect((IntegerExpression) orig);
				else{
					Results.numericVariables.add(((IntVariable) exp).getName());
					return exp.toString();
				}
			}

			if (exp instanceof RealVariable) Results.numericVariables.add(((RealVariable) exp).getName());
			return exp.toString();
		}
	}

	private String getOpString(Operation.Operator operator) {
		switch(operator) {
			case EQ: return "=";
			case NE: return "not (=";
			case LT:
			case LE:
			case GT:
			case GE:
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case MOD:
			case NEG:
			case BIT_AND:
			case BIT_OR:
			case BIT_XOR:
			case BIT_NOT:
			case SHIFTL:
			case SHIFTR:
			case SHIFTUR:
				return operator.toString();
			case AND: return "and";
			case OR: return "or";
			case NOT:
				return "not ";
			default:
				throw new IllegalArgumentException("unsupported translation to ABC for operator: " + operator);
		}
	}

	static class IntermediateConstraint {
		public final Constraint c;
		public final Expression l, r;
		public final String arg1, arg2;

		public static Set<Class<?>> CHARS = new HashSet<Class<?>>();

		static {
			CHARS.addAll(Arrays.asList(StringExpression.class, SymbolicCharAtInteger.class));
		}

		public IntermediateConstraint(TranslationManager manager, Constraint c) {
			this.c = c;

			l = c.getLeft();
			r = c.getRight();

			String a = manager.numExpr.collect((IntegerExpression) l); // throws classcastexception for realexpressions
			String b = manager.numExpr.collect((IntegerExpression) r);
			Comparator cmp = c.getComparator();

			if(CHARS.contains(l.getClass()) || CHARS.contains(r.getClass())) {
				if (l instanceof SymbolicCharAtInteger
						&& r instanceof IntegerConstant
						&& (cmp.toString().equals(" >= ") || cmp.toString().equals(" <= ") || cmp.toString().equals(" != ")) ){
					try {
						a = "(str.to_code " + a + ")";
					} catch(NumberFormatException e) {}
				} else if(CHARS.contains(l.getClass()) && l instanceof SymbolicCharAtInteger){
					a = "(str.to_code " + a + ")";
					Pattern regex = Pattern.compile("(\\(str.at \\w+ \\d+\\))");
					Matcher matcher = regex.matcher(b);
					if(matcher.find()){
						String matchedStr = matcher.group(0);
						b = b.replaceAll(matchedStr, "str.to_code "+ matchedStr);
					}
				} else if(CHARS.contains(r.getClass()) && r instanceof SymbolicCharAtInteger){
					b = "(str.to_code " + b + ")";
					Pattern regex = Pattern.compile("(\\(str.at \\w+ \\d+\\))");
					Matcher matcher = regex.matcher(a);
					if(matcher.find()){
						String matchedStr = matcher.group(0);
						a = a.replaceAll(matchedStr, "str.to_code "+ matchedStr);
					}
				}
				else {
					try {
						a = "\"" + String.valueOf((char) Integer.parseInt(a)) + "\"";
					} catch(NumberFormatException e) {}

					try {
						b = "\"" + String.valueOf((char) Integer.parseInt(b)) + "\"";
					} catch(NumberFormatException e) {}
				}
			}

			arg1 = a;
			arg2 = b;
		}
	}

	public Function<Constraint, String> createConstraint(final String op) {
		return (Constraint c) -> {
			final IntermediateConstraint ic = new IntermediateConstraint(manager, c);
			final int openBrackets = op.length() - op.replace("(", "").length();

			return op + " " + ic.arg1 + " " + ic.arg2 + new String(new char[openBrackets]).replace("\0", ")");
		};
	}
}
