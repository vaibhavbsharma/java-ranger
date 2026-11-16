package gov.nasa.jpf.symbc.numeric;

import java.util.List;

import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Visitor;
import za.ac.sun.cs.green.expr.VisitorException;

/**
 * A Green expression node that represents a unary IEEE 754 floating-point class
 * predicate applied to an operand.
 *
 * <p>The base Green library ({@code green.jar}) has no operator for the FP class
 * tests ({@code isNaN}, {@code isInf}, {@code isZero}, {@code isPositive},
 * {@code isNegative}) that symbolic FP division (FDIV/DDIV) needs in order to
 * express its outcome disjunction as a single logical formula. This node pairs a
 * Green operand expression with one of the existing SPF {@link Comparator} class
 * predicates, and carries that predicate as a first-class expression leaf inside a
 * Green formula (e.g. the disjunction produced by {@code GammaVarExpr} /
 * {@code AstToGreenVisitor}).
 *
 * <p>The {@code Comparator} is reused verbatim from the SPF numeric layer, so the
 * mapping to a concrete decision procedure stays in a single place
 * ({@link GreenPbTranslator}). Because the Green {@link Visitor} interface is a
 * compiled, closed hierarchy, this node dispatches to the SPF translator itself at
 * {@link #accept(Visitor)} time via an {@code instanceof} check; for any other
 * (generic) {@code Visitor} it falls through to the base no-op chain, which keeps it
 * safe with the rest of the codebase that walks Green trees.
 */
public final class FPClassExpr extends Expression {

    /** The operand to test (a Green expression, typically a {@code RealVariable}). */
    public final Expression operand;

    /** The FP class predicate to apply (an SPF {@code Comparator}). */
    public final Comparator cmp;

    public FPClassExpr(Expression operand, Comparator cmp) {
        this.operand = operand;
        this.cmp = cmp;
    }

    // Visits the operand first (so the translator pushes its Z3 expression), then,
    // only when the visiting translator is our own GreenPbTranslator, emits the
    // Z3 FP predicate for the class comparator.
    @Override
    public void accept(Visitor visitor) throws VisitorException {
        operand.accept(visitor);
        if (visitor instanceof GreenPbTranslator) {
            ((GreenPbTranslator) visitor).postVisitFPClass(this);
        }
    }

    @Override
    public int compareTo(Expression o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof FPClassExpr)
                && ((FPClassExpr) o).cmp == cmp
                && ((FPClassExpr) o).operand.equals(operand);
    }

    @Override
    public int hashCode() {
        return 31 * cmp.hashCode() + operand.hashCode();
    }

    @Override
    public String toString() {
        return cmp + "(" + operand + ")";
    }

    @Override
    public int getLength() {
        return operand.getLength();
    }

    @Override
    public int getLeftLength() {
        return operand.getLeftLength();
    }

    @Override
    public int numVar() {
        return operand.numVar();
    }

    @Override
    public int numVarLeft() {
        return operand.numVarLeft();
    }

    @Override
    public List<String> getOperationVector() {
        return operand.getOperationVector();
    }
}
