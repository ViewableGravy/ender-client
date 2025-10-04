package auto.farming.model;

import haven.Coord;
import haven.Coord2d;
import auto.farming.util.GridUtils;
import java.util.List;

/**
 * Manual test class for FieldGrid and GridUtils functionality.
 * Run this class to verify grid coordinate system implementation.
 */
public class FieldGridTest {
    
    public static void main(String[] args) {
        System.out.println("=== FieldGrid and GridUtils Manual Tests ===\n");
        
        testGridUtils();
        testRectangleGrid();
        testCircleGrid();
        testBoundaryChecking();
        testTileEnumeration();
        testUtilityMethods();
        
        System.out.println("\n=== All Tests Passed! ===");
    }
    
    private static void testGridUtils() {
        System.out.println("Test 1: GridUtils coordinate conversion");
        
        // Test world to grid conversion
        Coord worldCoord = new Coord(110, 220);
        Coord2d gridCoord = GridUtils.worldToGrid(worldCoord);
        
        System.out.println("  World (110, 220) -> Grid (" + gridCoord.x + ", " + gridCoord.y + ")");
        assert Math.abs(gridCoord.x - 10.0) < 0.01 : "Expected grid X = 10";
        assert Math.abs(gridCoord.y - 20.0) < 0.01 : "Expected grid Y = 20";
        System.out.println("  ✓ World to grid conversion works");
        
        // Test grid to world conversion
        Coord2d grid = new Coord2d(5, 10);
        Coord world = GridUtils.gridToWorld(grid);
        
        System.out.println("  Grid (5, 10) -> World (" + world.x + ", " + world.y + ")");
        assert world.x == 55 : "Expected world X = 55";
        assert world.y == 110 : "Expected world Y = 110";
        System.out.println("  ✓ Grid to world conversion works");
        
        // Test distance calculation
        Coord2d a = new Coord2d(0, 0);
        Coord2d b = new Coord2d(3, 4);
        double distance = GridUtils.distance(a, b);
        
        System.out.println("  Distance (0,0) to (3,4) = " + distance);
        assert Math.abs(distance - 5.0) < 0.01 : "Expected distance = 5.0";
        System.out.println("  ✓ Distance calculation works");
        
        // Test Manhattan distance
        double manhattanDist = GridUtils.manhattanDistance(a, b);
        System.out.println("  Manhattan distance (0,0) to (3,4) = " + manhattanDist);
        assert manhattanDist == 7.0 : "Expected Manhattan distance = 7.0";
        System.out.println("  ✓ Manhattan distance works");
    }
    
    private static void testRectangleGrid() {
        System.out.println("\nTest 2: Rectangle Grid Creation");
        
        FieldGrid rect = FieldGrid.rectangle(0, 0, 10, 10);
        
        assert rect.getShape() == FieldShape.RECTANGLE : "Shape should be RECTANGLE";
        System.out.println("  ✓ Rectangle created with correct shape");
        
        Coord2d center = rect.getCenter();
        System.out.println("  Rectangle (0,0,10,10) center: (" + center.x + ", " + center.y + ")");
        assert Math.abs(center.x - 5.0) < 0.01 : "Center X should be 5";
        assert Math.abs(center.y - 5.0) < 0.01 : "Center Y should be 5";
        System.out.println("  ✓ Center calculation correct");
        
        double area = rect.getArea();
        System.out.println("  Rectangle area: " + area);
        assert Math.abs(area - 100.0) < 0.01 : "Area should be 100";
        System.out.println("  ✓ Area calculation correct");
        
        double[] bounds = rect.getBounds();
        System.out.println("  Bounds: [" + bounds[0] + ", " + bounds[1] + ", " + bounds[2] + ", " + bounds[3] + "]");
        assert bounds[0] == 0 && bounds[1] == 0 && bounds[2] == 10 && bounds[3] == 10;
        System.out.println("  ✓ Bounds correct");
    }
    
    private static void testCircleGrid() {
        System.out.println("\nTest 3: Circle Grid Creation");
        
        FieldGrid circle = FieldGrid.circle(5, 5, 3);
        
        assert circle.getShape() == FieldShape.CIRCLE : "Shape should be CIRCLE";
        System.out.println("  ✓ Circle created with correct shape");
        
        Coord2d center = circle.getCenter();
        System.out.println("  Circle (5,5,r=3) center: (" + center.x + ", " + center.y + ")");
        assert Math.abs(center.x - 5.0) < 0.01 : "Center X should be 5";
        assert Math.abs(center.y - 5.0) < 0.01 : "Center Y should be 5";
        System.out.println("  ✓ Center is correct");
        
        double area = circle.getArea();
        double expectedArea = Math.PI * 3 * 3;
        System.out.println("  Circle area: " + area + " (expected: " + expectedArea + ")");
        assert Math.abs(area - expectedArea) < 0.01 : "Area should be π * r²";
        System.out.println("  ✓ Area calculation correct");
        
        double[] bounds = circle.getBounds();
        System.out.println("  Bounds: [" + bounds[0] + ", " + bounds[1] + ", " + bounds[2] + ", " + bounds[3] + "]");
        assert bounds[0] == 2 && bounds[1] == 2 && bounds[2] == 8 && bounds[3] == 8;
        System.out.println("  ✓ Bounding box correct");
    }
    
    private static void testBoundaryChecking() {
        System.out.println("\nTest 4: Boundary Checking");
        
        // Rectangle boundary tests
        FieldGrid rect = FieldGrid.rectangle(0, 0, 10, 10);
        
        assert rect.contains(new Coord2d(5, 5)) : "Center should be inside";
        System.out.println("  ✓ Rectangle contains center point");
        
        assert rect.contains(new Coord2d(0, 0)) : "Min corner should be inside";
        System.out.println("  ✓ Rectangle contains min corner");
        
        assert rect.contains(new Coord2d(10, 10)) : "Max corner should be inside";
        System.out.println("  ✓ Rectangle contains max corner");
        
        assert !rect.contains(new Coord2d(11, 5)) : "Point outside should not be inside";
        System.out.println("  ✓ Rectangle excludes outside point");
        
        assert !rect.contains(null) : "Null should return false";
        System.out.println("  ✓ Rectangle handles null correctly");
        
        // Circle boundary tests
        FieldGrid circle = FieldGrid.circle(0, 0, 5);
        
        assert circle.contains(new Coord2d(0, 0)) : "Center should be inside";
        System.out.println("  ✓ Circle contains center");
        
        assert circle.contains(new Coord2d(3, 4)) : "Point at distance 5 should be inside";
        System.out.println("  ✓ Circle contains point at radius");
        
        assert !circle.contains(new Coord2d(6, 0)) : "Point outside radius should not be inside";
        System.out.println("  ✓ Circle excludes outside point");
        
        // Edge case: point exactly at radius
        assert circle.contains(new Coord2d(5, 0)) : "Point exactly at radius should be inside";
        System.out.println("  ✓ Circle includes point at exact radius");
    }
    
    private static void testTileEnumeration() {
        System.out.println("\nTest 5: Tile Enumeration");
        
        // Small rectangle
        FieldGrid smallRect = FieldGrid.rectangle(0, 0, 2, 2);
        List<Coord2d> rectTiles = smallRect.getTiles();
        
        System.out.println("  Rectangle (0,0,2,2) tiles: " + rectTiles.size());
        assert rectTiles.size() == 9 : "3x3 grid should have 9 tiles"; // 0,1,2 x 0,1,2 = 9
        System.out.println("  ✓ Rectangle tile count correct");
        
        // Small circle
        FieldGrid smallCircle = FieldGrid.circle(0, 0, 1.5);
        List<Coord2d> circleTiles = smallCircle.getTiles();
        
        System.out.println("  Circle (0,0,r=1.5) tiles: " + circleTiles.size());
        assert circleTiles.size() > 0 : "Circle should have some tiles";
        System.out.println("  ✓ Circle tile enumeration works");
        
        // Verify all enumerated tiles are actually inside the circle
        for (Coord2d tile : circleTiles) {
            assert smallCircle.contains(tile) : "All enumerated tiles should be inside bounds";
        }
        System.out.println("  ✓ All circle tiles are within bounds");
        
        // Larger rectangle for performance check
        FieldGrid largeRect = FieldGrid.rectangle(0, 0, 20, 20);
        List<Coord2d> largeTiles = largeRect.getTiles();
        
        System.out.println("  Large rectangle (0,0,20,20) tiles: " + largeTiles.size());
        assert largeTiles.size() == 441 : "21x21 grid should have 441 tiles"; // 0-20 inclusive = 21 values
        System.out.println("  ✓ Large rectangle enumeration correct");
    }
    
    private static void testUtilityMethods() {
        System.out.println("\nTest 6: Utility Methods");
        
        FieldGrid grid = FieldGrid.rectangle(10, 20, 30, 40);
        
        String str = grid.toString();
        System.out.println("  toString: " + str);
        assert str.contains("RECTANGLE") : "toString should contain shape";
        assert str.contains("10") : "toString should contain origin X";
        System.out.println("  ✓ toString works");
        
        // Test setters
        Coord2d newOrigin = new Coord2d(50, 60);
        grid.setOrigin(newOrigin);
        assert grid.getOrigin().x == 50 && grid.getOrigin().y == 60;
        System.out.println("  ✓ setOrigin works");
        
        Coord2d newDims = new Coord2d(100, 200);
        grid.setDimensions(newDims);
        assert grid.getDimensions().x == 100 && grid.getDimensions().y == 200;
        System.out.println("  ✓ setDimensions works");
    }
}
