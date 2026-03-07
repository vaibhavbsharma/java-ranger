package gov.nasa.jpf.symbc.numeric;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TestRealIsNaN {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // This mimics the effect of reading config properties like symbolic.min_double etc.

        // Set minDouble and maxDouble
        Field minDoubleField = MinMax.class.getDeclaredField("minDouble");
        minDoubleField.setAccessible(true);
        minDoubleField.set(null, -10000.0);

        Field maxDoubleField = MinMax.class.getDeclaredField("maxDouble");
        maxDoubleField.setAccessible(true);
        maxDoubleField.set(null, 10000.0);

        // Initialize the per‑variable maps to empty maps (they are null by default)
        Field varMinDoubleMapField = MinMax.class.getDeclaredField("varMinDoubleMap");
        varMinDoubleMapField.setAccessible(true);
        varMinDoubleMapField.set(null, new HashMap<String, Double>());

        Field varMaxDoubleMapField = MinMax.class.getDeclaredField("varMaxDoubleMap");
        varMaxDoubleMapField.setAccessible(true);
        varMaxDoubleMapField.set(null, new HashMap<String, Double>());

    }
    @Test
    public void testEquality() {
        SymbolicReal x = new SymbolicReal("x");
        RealIsNaN p1 = RealIsNaN.create(x);
        RealIsNaN p2 = RealIsNaN.create(x);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testToString() {
        SymbolicReal x = new SymbolicReal("x");
        assertEquals("isNaN(" + x + ")", RealIsNaN.create(x).toString());
    }

    @Test
    public void testStringPC() {
        SymbolicReal x = new SymbolicReal("x");
        assertEquals("isNaN(" + x + ")", RealIsNaN.create(x).stringPC());
    }

    @Test
    public void testGetSortOnConstant() {
        RealNaN nan = RealNaN.FLOAT_NAN;
        assertEquals(FpSort.FLOAT, RealIsNaN.create(nan).getSort());
    }

    @Test
    public void testGetVarsVals() {
        SymbolicReal x = new SymbolicReal("x");
        RealIsNaN p = RealIsNaN.create(x);
        Map<String, Object> map = new HashMap<>();
        p.getVarsVals(map);
        // No direct assertion, but ensures no exception.
    }

    @Test
    public void testCompareTo() {
        SymbolicReal x = new SymbolicReal("x");
        SymbolicReal y = new SymbolicReal("y");
        RealIsNaN p1 = RealIsNaN.create(x);
        RealIsNaN p2 = RealIsNaN.create(x);
        RealIsNaN p3 = RealIsNaN.create(y);
        assertEquals(0, p1.compareTo(p2));
        assertNotEquals(0, p1.compareTo(p3));
    }

    @Test
    public void testVisitorDispatch() {
        SymbolicReal x = new SymbolicReal("x");
        RealIsNaN p = RealIsNaN.create(x);
        CountingVisitor v = new CountingVisitor();
        p.accept(v);
        assertEquals(1, v.preCount);
        assertEquals(1, v.visitCount);
        assertEquals(1, v.postCount);
    }

    private static class CountingVisitor extends ConstraintExpressionVisitor {
        int preCount = 0;
        int visitCount = 0;
        int postCount = 0;

        @Override
        public void preVisit(RealIsNaN expr) {
            preCount++;
        }

        @Override
        public void visitRealIsNaN(RealIsNaN expr) {
            visitCount++;
        }

        @Override
        public void postVisit(RealIsNaN expr) {
            postCount++;
        }
    }
}