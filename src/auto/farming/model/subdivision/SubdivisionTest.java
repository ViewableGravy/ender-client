package auto.farming.model.subdivision;

import auto.farming.model.FieldGrid;
import auto.farming.model.FieldShape;
import haven.Coord2d;
import java.util.List;

/**
 * Manual test class for field subdivision functionality.
 * Run this class to verify subdivision system implementation.
 */
public class SubdivisionTest {
    
    public static void main(String[] args) {
        System.out.println("=== Field Subdivision System Manual Tests ===\n");
        
        testSubdivisionConfig();
        testRectangleQuadrants();
        testRectangleCustomGrid();
        testCircleQuadrants();
        testCircleCustomSegments();
        testBoundaryChecking();
        testNoOverlap();
        
        System.out.println("\n=== All Tests Passed! ===");
    }
    
    private static void testSubdivisionConfig() {
        System.out.println("Test 1: SubdivisionConfig");
        
        SubdivisionConfig none = SubdivisionConfig.none();
        assert none.getType() == SubdivisionConfig.SubdivisionType.NONE;
        assert none.getCount() == 1;
        System.out.println("  ✓ NONE configuration works");
        
        SubdivisionConfig quads = SubdivisionConfig.quadrants();
        assert quads.getType() == SubdivisionConfig.SubdivisionType.QUADRANTS;
        assert quads.getCount() == 4;
        System.out.println("  ✓ QUADRANTS configuration works");
        
        SubdivisionConfig custom = new SubdivisionConfig(
            SubdivisionConfig.SubdivisionType.GRID, 8
        );
        assert custom.getCount() == 8;
        System.out.println("  ✓ Custom configuration works");
        
        String str = custom.toString();
        assert str.contains("GRID") && str.contains("8");
        System.out.println("  ✓ toString works: " + str);
    }
    
    private static void testRectangleQuadrants() {
        System.out.println("\nTest 2: Rectangle Quadrant Subdivision (2x2)");
        
        FieldGrid field = FieldGrid.rectangle(0, 0, 10, 10);
        RectangleSubdivision subdivision = new RectangleSubdivision(field);
        
        assert subdivision.getCount() == 4 : "Should have 4 quadrants";
        System.out.println("  ✓ Quadrant count correct (4)");
        
        assert subdivision.getRows() == 2 && subdivision.getColumns() == 2;
        System.out.println("  ✓ 2x2 grid configuration");
        
        // Test each quadrant
        FieldGrid q0 = subdivision.getSubdivision(0); // Top-left
        FieldGrid q1 = subdivision.getSubdivision(1); // Top-right
        FieldGrid q2 = subdivision.getSubdivision(2); // Bottom-left
        FieldGrid q3 = subdivision.getSubdivision(3); // Bottom-right
        
        assert q0.contains(new Coord2d(2, 2)) : "Q0 should contain (2,2)";
        assert q1.contains(new Coord2d(7, 2)) : "Q1 should contain (7,2)";
        assert q2.contains(new Coord2d(2, 7)) : "Q2 should contain (2,7)";
        assert q3.contains(new Coord2d(7, 7)) : "Q3 should contain (7,7)";
        System.out.println("  ✓ All quadrants contain correct points");
        
        // Test segment position
        int[] pos0 = subdivision.getSegmentPosition(0);
        assert pos0[0] == 0 && pos0[1] == 0 : "Segment 0 is row 0, col 0";
        
        int[] pos3 = subdivision.getSegmentPosition(3);
        assert pos3[0] == 1 && pos3[1] == 1 : "Segment 3 is row 1, col 1";
        System.out.println("  ✓ Segment position calculations correct");
        
        // Test segment index lookup
        int idx = subdivision.getSegmentIndex(1, 0);
        assert idx == 2 : "Row 1, Col 0 should be segment 2";
        System.out.println("  ✓ Segment index lookup works");
    }
    
    private static void testRectangleCustomGrid() {
        System.out.println("\nTest 3: Rectangle Custom Grid (3x4 = 12 segments)");
        
        FieldGrid field = FieldGrid.rectangle(0, 0, 30, 40);
        RectangleSubdivision subdivision = new RectangleSubdivision(field, 3, 4);
        
        assert subdivision.getCount() == 12 : "Should have 12 segments";
        System.out.println("  ✓ Segment count correct (12)");
        
        List<FieldGrid> all = subdivision.getAllSubdivisions();
        assert all.size() == 12 : "getAllSubdivisions should return 12";
        System.out.println("  ✓ getAllSubdivisions returns all segments");
        
        // Test first segment dimensions
        FieldGrid seg0 = subdivision.getSubdivision(0);
        double[] bounds = seg0.getBounds();
        double width = bounds[2] - bounds[0];
        double height = bounds[3] - bounds[1];
        
        System.out.println("  Segment 0 dimensions: " + width + " x " + height);
        assert Math.abs(width - 10.0) < 0.01 : "Width should be 30/3 = 10";
        assert Math.abs(height - 10.0) < 0.01 : "Height should be 40/4 = 10";
        System.out.println("  ✓ Segment dimensions correct");
        
        // Test boundary membership
        assert subdivision.isInSubdivision(5, 5, 0) : "Point should be in segment 0";
        assert !subdivision.isInSubdivision(5, 5, 11) : "Point should not be in segment 11";
        System.out.println("  ✓ isInSubdivision works correctly");
    }
    
    private static void testCircleQuadrants() {
        System.out.println("\nTest 4: Circle Quadrant Subdivision (4 radial segments)");
        
        FieldGrid field = FieldGrid.circle(0, 0, 10);
        CircleSubdivision subdivision = new CircleSubdivision(field);
        
        assert subdivision.getCount() == 4 : "Should have 4 segments";
        System.out.println("  ✓ Segment count correct (4)");
        
        // Test angle ranges
        double[] angles0 = subdivision.getSegmentAngleRangeDegrees(0);
        System.out.println("  Segment 0 angle range: " + angles0[0] + "° to " + angles0[1] + "°");
        assert Math.abs(angles0[0] - 0) < 0.01 : "Segment 0 starts at 0°";
        assert Math.abs(angles0[1] - 90) < 0.01 : "Segment 0 ends at 90°";
        System.out.println("  ✓ Segment 0 angle range correct (0-90°)");
        
        double[] angles1 = subdivision.getSegmentAngleRangeDegrees(1);
        assert Math.abs(angles1[0] - 90) < 0.01 && Math.abs(angles1[1] - 180) < 0.01;
        System.out.println("  ✓ Segment 1 angle range correct (90-180°)");
        
        // Test point membership in segments
        // Point at (5, 0) should be in segment 0 (East, 0°)
        assert subdivision.isInSubdivision(5, 0, 0) : "East point should be in segment 0";
        
        // Point at (0, 5) should be in segment 1 (North, 90°)
        assert subdivision.isInSubdivision(0, 5, 1) : "North point should be in segment 1";
        
        // Point at (-5, 0) should be in segment 2 (West, 180°)
        assert subdivision.isInSubdivision(-5, 0, 2) : "West point should be in segment 2";
        
        // Point at (0, -5) should be in segment 3 (South, 270°)
        assert subdivision.isInSubdivision(0, -5, 3) : "South point should be in segment 3";
        
        System.out.println("  ✓ Radial segment membership correct");
        
        // Test getSegmentAt
        int seg = subdivision.getSegmentAt(5, 5);
        System.out.println("  Point (5,5) is in segment: " + seg);
        assert seg >= 0 && seg < 4 : "Point should be in a valid segment";
        System.out.println("  ✓ getSegmentAt works");
    }
    
    private static void testCircleCustomSegments() {
        System.out.println("\nTest 5: Circle Custom Segments (5 radial segments)");
        
        FieldGrid field = FieldGrid.circle(10, 10, 8);
        CircleSubdivision subdivision = new CircleSubdivision(field, 5);
        
        assert subdivision.getCount() == 5 : "Should have 5 segments";
        System.out.println("  ✓ Segment count correct (5)");
        
        // Each segment should be 72 degrees (360/5)
        double[] angles = subdivision.getSegmentAngleRangeDegrees(0);
        double angleSpan = angles[1] - angles[0];
        System.out.println("  Each segment spans: " + angleSpan + "°");
        assert Math.abs(angleSpan - 72) < 0.01 : "Each segment should be 72°";
        System.out.println("  ✓ Segment angle span correct (72°)");
        
        // Test all segments retrieved
        List<FieldGrid> all = subdivision.getAllSubdivisions();
        assert all.size() == 5 : "getAllSubdivisions should return 5";
        System.out.println("  ✓ getAllSubdivisions returns all segments");
    }
    
    private static void testBoundaryChecking() {
        System.out.println("\nTest 6: Boundary Checking");
        
        // Test invalid index handling
        FieldGrid field = FieldGrid.rectangle(0, 0, 10, 10);
        RectangleSubdivision subdivision = new RectangleSubdivision(field, 2, 2);
        
        try {
            subdivision.getSubdivision(-1);
            assert false : "Should throw exception for negative index";
        } catch (IndexOutOfBoundsException e) {
            System.out.println("  ✓ Correctly rejects negative index");
        }
        
        try {
            subdivision.getSubdivision(4);
            assert false : "Should throw exception for out-of-bounds index";
        } catch (IndexOutOfBoundsException e) {
            System.out.println("  ✓ Correctly rejects out-of-bounds index");
        }
        
        // Test invalid field shape
        try {
            FieldGrid circleField = FieldGrid.circle(0, 0, 5);
            new RectangleSubdivision(circleField);
            assert false : "Should reject circular field";
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ RectangleSubdivision rejects circular field");
        }
        
        try {
            FieldGrid rectField = FieldGrid.rectangle(0, 0, 10, 10);
            new CircleSubdivision(rectField);
            assert false : "Should reject rectangular field";
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ CircleSubdivision rejects rectangular field");
        }
    }
    
    private static void testNoOverlap() {
        System.out.println("\nTest 7: No Overlap Between Segments");
        
        // Test rectangle segments don't overlap
        FieldGrid rectField = FieldGrid.rectangle(0, 0, 10, 10);
        RectangleSubdivision rectSub = new RectangleSubdivision(rectField, 2, 2);
        
        // Sample points at segment boundaries
        Coord2d point = new Coord2d(5, 3); // Should be in only one segment
        int inCount = 0;
        for (int i = 0; i < rectSub.getCount(); i++) {
            if (rectSub.isInSubdivision(point.x, point.y, i)) {
                inCount++;
            }
        }
        assert inCount == 1 : "Point should be in exactly one rectangle segment";
        System.out.println("  ✓ Rectangle segments don't overlap");
        
        // Test circle segments don't overlap
        FieldGrid circleField = FieldGrid.circle(0, 0, 10);
        CircleSubdivision circleSub = new CircleSubdivision(circleField, 4);
        
        // Sample point in circle
        Coord2d circlePoint = new Coord2d(5, 5);
        inCount = 0;
        for (int i = 0; i < circleSub.getCount(); i++) {
            if (circleSub.isInSubdivision(circlePoint.x, circlePoint.y, i)) {
                inCount++;
            }
        }
        assert inCount == 1 : "Point should be in exactly one circle segment";
        System.out.println("  ✓ Circle segments don't overlap");
    }
}
