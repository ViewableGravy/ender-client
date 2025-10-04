package auto.farming.ui.editor;

import auto.farming.FarmingManager;
import auto.farming.model.FarmField;
import auto.farming.model.FieldGrid;
import auto.farming.model.FieldShape;
import auto.farming.model.FieldType;
import auto.farming.ui.overlay.FarmingOverlay;
import haven.*;

import java.util.function.Consumer;

/**
 * Interactive field editor for creating and editing fields on the map.
 * Handles mouse interaction, grid snapping, and field validation.
 */
public class FieldEditor implements MapView.Grabber {
    private static FieldEditor activeEditor = null;
    
    private final MapView mapView;
    private final FarmingManager manager;
    private final EditorOverlay overlay;
    private final MapView.GrabXL grabber;
    
    private EditorMode mode = EditorMode.OFF;
    private int step = 0; // Current step in creation process (0 or 1)
    
    // Callbacks
    private Consumer<FarmField> onFieldCreated = null;
    private Consumer<FarmField> onFieldEdited = null;
    private Runnable onCancelled = null;
    
    // Field being edited (null for creation)
    private FarmField editingField = null;
    
    /**
     * Creates a new field editor.
     * @param mapView The map view to attach to
     */
    public FieldEditor(MapView mapView) {
        this.mapView = mapView;
        this.manager = FarmingManager.getInstance();
        this.overlay = new EditorOverlay(FarmingOverlay.getInstance().getSettings());
        this.grabber = mapView.new GrabXL(this);
    }
    
    /**
     * Renders the active editor (static method for MapView integration).
     * @param g Graphics output
     */
    public static void renderActive(GOut g) {
        if (activeEditor != null) {
            activeEditor.render(g);
        }
    }
    
    /**
     * Cancels the active editor (static method for keyboard handling).
     * @return true if an editor was cancelled
     */
    public static boolean cancelActive() {
        if (activeEditor != null) {
            activeEditor.cancel();
            return true;
        }
        return false;
    }
    
    /**
     * Activates the editor in rectangle creation mode.
     * @param onCreate Callback when field is created
     * @param onCancel Callback when cancelled
     */
    public void startRectangleMode(Consumer<FarmField> onCreate, Runnable onCancel) {
        activate(EditorMode.RECTANGLE, onCreate, onCancel);
    }
    
    /**
     * Activates the editor in circle creation mode.
     * @param onCreate Callback when field is created
     * @param onCancel Callback when cancelled
     */
    public void startCircleMode(Consumer<FarmField> onCreate, Runnable onCancel) {
        activate(EditorMode.CIRCLE, onCreate, onCancel);
    }
    
    /**
     * Activates the editor in edit mode for an existing field.
     * @param field The field to edit
     * @param onEdit Callback when field is edited
     * @param onCancel Callback when cancelled
     */
    public void startEditMode(FarmField field, Consumer<FarmField> onEdit, Runnable onCancel) {
        this.editingField = field;
        this.onFieldEdited = onEdit;
        activate(EditorMode.EDIT, null, onCancel);
    }
    
    /**
     * Internal activation method.
     */
    private void activate(EditorMode newMode, Consumer<FarmField> onCreate, Runnable onCancel) {
        if (mode != EditorMode.OFF) {
            deactivate();
        }
        
        this.mode = newMode;
        this.step = 0;
        this.onFieldCreated = onCreate;
        this.onCancelled = onCancel;
        
        overlay.clear();
        grabber.mv = true; // Enable mouse movement tracking
        mapView.grab(grabber);
        
        // Set as active editor
        activeEditor = this;
        
        System.out.println("Field editor activated: " + mode);
    }
    
    /**
     * Deactivates the editor and cleans up.
     */
    public void deactivate() {
        if (mode == EditorMode.OFF) {
            return;
        }
        
        mapView.release(grabber);
        overlay.clear();
        
        // Clear active editor
        if (activeEditor == this) {
            activeEditor = null;
        }
        
        EditorMode oldMode = mode;
        mode = EditorMode.OFF;
        step = 0;
        editingField = null;
        onFieldCreated = null;
        onFieldEdited = null;
        onCancelled = null;
        
        System.out.println("Field editor deactivated: " + oldMode);
    }
    
    /**
     * Cancels the current operation and deactivates.
     */
    public void cancel() {
        Runnable callback = onCancelled;
        deactivate();
        
        if (callback != null) {
            callback.run();
        }
    }
    
    /**
     * Checks if the editor is active.
     * @return true if active
     */
    public boolean isActive() {
        return mode != EditorMode.OFF;
    }
    
    /**
     * Gets the current editor mode.
     * @return The current mode
     */
    public EditorMode getMode() {
        return mode;
    }
    
    /**
     * Renders the editor overlay.
     * Called from MapView rendering.
     * @param g Graphics output
     */
    public void render(GOut g) {
        if (mode == EditorMode.OFF) {
            return;
        }
        
        switch (mode) {
            case RECTANGLE:
                overlay.renderRectanglePreview(g, mapView);
                break;
            case CIRCLE:
                overlay.renderCirclePreview(g, mapView);
                break;
            case EDIT:
                // TODO: Render field being edited with special highlighting
                break;
            default:
                break;
        }
        
        overlay.renderInstructions(g, mode, step);
    }
    
    // Grabber interface implementation
    
    @Override
    public boolean mmousedown(Coord mc, int button) {
        if (button != 1) { // Only handle left click
            return false;
        }
        
        // mc is already in world coordinates from GrabXL (pixels, not tiles)
        Coord2d worldCoord = new Coord2d(mc);
        
        switch (mode) {
            case RECTANGLE:
                handleRectangleClick(worldCoord);
                return true;
            case CIRCLE:
                handleCircleClick(worldCoord);
                return true;
            case EDIT:
                // TODO: Handle edit mode click
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public boolean mmouseup(Coord mc, int button) {
        return isActive();
    }
    
    @Override
    public boolean mmousewheel(Coord mc, int amount) {
        return false; // Don't block mousewheel
    }
    
    @Override
    public void mmousemove(Coord mc) {
        if (mode == EditorMode.OFF) {
            return;
        }
        
        // mc is already in world coordinates from GrabXL
        Coord2d worldCoord = new Coord2d(mc);
        
        switch (mode) {
            case RECTANGLE:
                if (step == 1) {
                    overlay.setSecondCorner(worldCoord);
                }
                break;
            case CIRCLE:
                if (step == 1) {
                    overlay.setRadiusFromMouse(worldCoord);
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * Handles mouse click in rectangle mode.
     * @param worldCoord The click position in world coordinates
     */
    private void handleRectangleClick(Coord2d worldCoord) {
        if (step == 0) {
            // First click: set first corner
            overlay.setFirstCorner(worldCoord);
            step = 1;
        } else if (step == 1) {
            // Second click: create field
            overlay.setSecondCorner(worldCoord);
            createFieldFromPreview(FieldShape.RECTANGLE);
        }
    }
    
    /**
     * Handles mouse click in circle mode.
     * @param worldCoord The click position in world coordinates
     */
    private void handleCircleClick(Coord2d worldCoord) {
        if (step == 0) {
            // First click: set center
            overlay.setCenterPoint(worldCoord);
            step = 1;
        } else if (step == 1) {
            // Second click: create field with current radius
            createFieldFromPreview(FieldShape.CIRCLE);
        }
    }
    
    /**
     * Creates a field from the current preview.
     * @param shape The shape of the field
     */
    private void createFieldFromPreview(FieldShape shape) {
        FieldGrid grid = null;
        
        if (shape == FieldShape.RECTANGLE) {
            grid = overlay.getRectangleFieldGrid();
        } else if (shape == FieldShape.CIRCLE) {
            grid = overlay.getCircleFieldGrid();
        }
        
        if (grid == null) {
            System.err.println("Invalid field preview - cannot create field");
            return;
        }
        
        // Validate field
        if (!validateFieldPlacement(grid)) {
            return;
        }
        
        // Create field
        String fieldName = generateFieldName(shape);
        FieldType fieldType = FieldType.QUALITY; // Default to quality field
        FarmField field = new FarmField(fieldName, fieldType, shape);
        field.setGrid(grid);
        field.setEnabled(true);
        
        // Add to manager
        manager.addField(field);
        
        // Save configuration
        try {
            manager.saveConfig();
        } catch (Exception e) {
            System.err.println("Failed to save field configuration: " + e.getMessage());
        }
        
        System.out.println("Created field: " + fieldName + " with area " + grid.getArea() + " tiles");
        
        // Callback and deactivate
        Consumer<FarmField> callback = onFieldCreated;
        deactivate();
        
        if (callback != null) {
            callback.accept(field);
        }
    }
    
    /**
     * Validates field placement (no overlaps, minimum size, etc.).
     * @param grid The field grid to validate
     * @return true if valid
     */
    private boolean validateFieldPlacement(FieldGrid grid) {
        // Check minimum size
        if (grid.getArea() < 1) {
            showError("Field must be at least 1 tile");
            return false;
        }
        
        // Check for overlaps with existing fields
        for (FarmField existingField : manager.getAllFields()) {
            if (fieldsOverlap(grid, existingField.getGrid())) {
                showError("Field overlaps with existing field: " + existingField.getName());
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if two field grids overlap.
     * @param grid1 First field grid
     * @param grid2 Second field grid
     * @return true if they overlap
     */
    private boolean fieldsOverlap(FieldGrid grid1, FieldGrid grid2) {
        // Simple bounding box overlap check
        // For rectangle fields, get bounds
        if (grid1.getShape() == FieldShape.RECTANGLE && grid2.getShape() == FieldShape.RECTANGLE) {
            double x1 = grid1.getOrigin().x;
            double y1 = grid1.getOrigin().y;
            double x2 = x1 + grid1.getDimensions().x;
            double y2 = y1 + grid1.getDimensions().y;
            
            double x3 = grid2.getOrigin().x;
            double y3 = grid2.getOrigin().y;
            double x4 = x3 + grid2.getDimensions().x;
            double y4 = y3 + grid2.getDimensions().y;
            
            // Check if rectangles overlap
            return !(x2 <= x3 || x4 <= x1 || y2 <= y3 || y4 <= y1);
        }
        
        // For circle fields or mixed shapes, check if centers are too close
        Coord2d center1 = grid1.getCenter();
        Coord2d center2 = grid2.getCenter();
        double distance = center1.dist(center2);
        
        // Estimate radius (conservative)
        double radius1 = Math.max(grid1.getDimensions().x, grid1.getDimensions().y) / 2.0;
        double radius2 = Math.max(grid2.getDimensions().x, grid2.getDimensions().y) / 2.0;
        
        return distance < (radius1 + radius2);
    }
    
    /**
     * Generates a unique field name.
     * @param shape The field shape
     * @return A unique field name
     */
    private String generateFieldName(FieldShape shape) {
        String prefix = shape == FieldShape.CIRCLE ? "Circle" : "Rectangle";
        int number = 1;
        
        while (true) {
            String name = prefix + " Field " + number;
            boolean exists = false;
            
            for (FarmField field : manager.getAllFields()) {
                if (field.getName().equals(name)) {
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                return name;
            }
            
            number++;
        }
    }
    
    /**
     * Shows an error message to the user.
     * @param message The error message
     */
    private void showError(String message) {
        System.err.println("Field editor error: " + message);
        // TODO: Show in-game error notification
    }
}
