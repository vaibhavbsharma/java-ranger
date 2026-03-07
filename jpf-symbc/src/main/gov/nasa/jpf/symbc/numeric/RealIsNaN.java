package gov.nasa.jpf.symbc.numeric;

import java.util.Map;

/**
 * Symbolic predicate representing isNaN(expr).
 * Returns an integer expression: 1 if expr is NaN, 0 otherwise.
 */
public final class RealIsNaN extends IntegerExpression {
    private final RealExpression expr;

    // Private constructor; use factory method create().
    private RealIsNaN(RealExpression expr) {
        this.expr = expr;
    }

    /**
     * Factory method to create an isNaN predicate.
     * @param expr the floating‑point expression to test.
     * @return a new RealIsNaN instance.
     */
    public static RealIsNaN create(RealExpression expr) {
        // TODO: Consider caching identical expressions to reduce AST size.
        return new RealIsNaN(expr);
    }

    /**
     * Returns the inner expression.
     */
    public RealExpression getExpr() {
        return expr;
    }

    /**
     * Attempts to retrieve the floating‑point sort (FLOAT/DOUBLE) of the inner expression.
     * For constants (RealSpecialConstant) the sort is known; for symbolic variables,
     * this will return null until type information is added.
     * @return the FpSort if determinable, otherwise null.
     */
    public FpSort getSort() {
        if (expr instanceof RealSpecialConstant) {
            return ((RealSpecialConstant) expr).getSort();
        }
        // TODO: Later, when SymbolicReal stores its type, handle it here.
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RealIsNaN)) return false;
        RealIsNaN other = (RealIsNaN) obj;
        return expr.equals(other.expr);
    }

    @Override
    public int hashCode() {
        return expr.hashCode() * 31 + 17;
    }

    @Override
    public String toString() {
        return "isNaN(" + expr + ")";
    }

    /**
     * Returns a string representation suitable for path condition output.
     */
    @Override
    public String stringPC() {
        return toString();
    }

    /**
     * Populates the map with variable assignments from the inner expression.
     */
    @Override
    public void getVarsVals(Map<String, Object> solvedVars) {
        expr.getVarsVals(solvedVars);
    }

    /**
     * Compares this expression with another for ordering.
     * Ordering is based first on class name, then on the inner expression if both are RealIsNaN.
     */
    @Override
    public int compareTo(Expression o) {
        if (o instanceof RealIsNaN) {
            RealIsNaN other = (RealIsNaN) o;
            return expr.compareTo(other.expr);
        }
        return getClass().getName().compareTo(o.getClass().getName());
    }

    @Override
    public void accept(ConstraintExpressionVisitor visitor) {
        visitor.preVisit(this);
        visitor.visitRealIsNaN(this);
        expr.accept(visitor);
        visitor.postVisit(this);
    }
}