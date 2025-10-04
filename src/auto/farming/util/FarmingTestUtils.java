package auto.farming.util;

import auto.farming.FarmingManager;
import auto.farming.model.*;
import haven.Coord2d;

/**
 * Development utilities for testing the farming system.
 * TEMPORARY - for testing only, remove in production.
 */
public class FarmingTestUtils {
    /**
     * Creates a test field for development/testing.
     * Creates a 10x10 tile rectangular field at the specified location.
     * @param centerX X coordinate of field center (in grid tiles)
     * @param centerY Y coordinate of field center (in grid tiles)
     * @param name Field name
     */
    public static void createTestField(double centerX, double centerY, String name) {
        FarmingManager manager = FarmingManager.getInstance();
        
        // Create a 10x10 rectangle field
        FarmField field = new FarmField(name, FieldType.QUANTITY, FieldShape.RECTANGLE);
        
        // Set grid boundaries (10x10 tiles)
        Coord2d min = new Coord2d(centerX - 5, centerY - 5);
        Coord2d max = new Coord2d(centerX + 5, centerY + 5);
        FieldGrid grid = FieldGrid.rectangle(min.x, min.y, max.x, max.y);
        field.setGrid(grid);
        field.setGridOrigin(min);
        
        // Set crop type
        field.setCropType("gfx/terobjs/plants/wheat");
        
        // Add to manager
        manager.addField(field);
        
        System.out.println("Created test field: " + name + " at (" + centerX + ", " + centerY + ")");
        System.out.println("Field ID: " + field.getId());
    }
    
    /**
     * Creates a test circle field for development/testing.
     * @param centerX X coordinate of field center (in grid tiles)
     * @param centerY Y coordinate of field center (in grid tiles)
     * @param radius Radius in tiles
     * @param name Field name
     */
    public static void createTestCircleField(double centerX, double centerY, double radius, String name) {
        FarmingManager manager = FarmingManager.getInstance();
        
        FarmField field = new FarmField(name, FieldType.QUALITY, FieldShape.CIRCLE);
        
        Coord2d center = new Coord2d(centerX, centerY);
        FieldGrid grid = FieldGrid.circle(centerX, centerY, radius);
        field.setGrid(grid);
        field.setGridOrigin(center);
        
        field.setCropType("gfx/terobjs/plants/flax");
        
        manager.addField(field);
        
        System.out.println("Created test circle field: " + name + " at (" + centerX + ", " + centerY + ") radius " + radius);
        System.out.println("Field ID: " + field.getId());
    }
    
    /**
     * Clears all fields from the manager.
     */
    public static void clearAllFields() {
        FarmingManager.getInstance().clearAll();
        System.out.println("Cleared all fields");
    }
    
    /**
     * Prints info about all fields.
     */
    public static void printFields() {
        FarmingManager manager = FarmingManager.getInstance();
        System.out.println("=== Farming Fields (" + manager.getFieldCount() + ") ===");
        for (FarmField field : manager.getAllFields()) {
            System.out.println(field.getName() + " - " + field.getShape() + " - " + field.getType() + 
                             " - Enabled: " + field.isEnabled());
        }
    }
}
