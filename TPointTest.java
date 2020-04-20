import junit.framework.TestCase;

public class TPointTest extends TestCase {
    TPoint pt1, pt2, pt3, pt4;

    protected void setUp() throws Exception {
        super.setUp();
        pt1 = new TPoint(1, 0);
        pt2 = new TPoint(0, 2);
        pt3 = new TPoint (pt1);
        pt4 = new TPoint(0, 2);

    }
    public void testTPointBasic() {
        assertEquals(1, pt1.x);
        assertEquals(0, pt1.y);

        assertEquals(1, pt3.x);
        assertEquals(0, pt3.y);

        assertEquals("(1,0)", pt3.toString());
    }

    public void testTPointEquals() {
        Piece piece = new Piece("0 0 0 1 0 3 0 9");
        assertTrue(pt1.equals(pt3));
        assertTrue(pt1.equals(pt1));
        assertFalse(pt1.equals(new TPoint(1, 1)));
        assertTrue(pt2.equals(pt4));
        assertFalse(pt1.equals(pt2));
        assertFalse(pt2.equals(pt1));
        assertFalse(pt1.equals(piece));
    }

    public void testHashCode() {
        assertEquals(Integer.hashCode(0) + Integer.hashCode(1), pt1.hashCode());
    }

}