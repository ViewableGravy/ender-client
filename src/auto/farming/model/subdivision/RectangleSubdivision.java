package auto.farming.model.subdivision;

import auto.farming.model.FieldGrid;
import haven.Coord2d;
import java.util.ArrayList;
import java.util.List;

/**
 * Rectangular field subdivision using grid-based rows and columns.
 * 
 * <p>Divides a rectangular field into equal-sized grid segments.
 * Supports various configurations like 2x2 (4 quadrants), 2x3 (6 segments), etc.</p>
 */
public class RectangleSubdivision implements FieldSubdivision {
    private final FieldGrid originalField;
    private final int rows;
    private final int columns;
    private final List<FieldGrid> subdivisions;
    
    /**
     * Creates a rectangular subdivision.
     * 
     * @param field Original rectangular field
     * @param rows Number of rows to divide into
     * @param columns Number of columns to divide into
     * @throws IllegalArgumentException if field is not rectangular or if rows/cols < 1
     */
    public RectangleSubdivision(FieldGrid field, int rows, int columns) {
        if (field.getShape() != auto.farming.model.FieldShape.RECTANGLE) {
            throw new IllegalArgumentException("Field must be rectangular");
        }
        if (rows < 1 || columns < 1) {
            throw new IllegalArgumentException("Rows and columns must be at least 1");
        }
        
        this.originalField = field;
        this.rows = rows;
        this.columns = columns;
        this.subdivisions = generateSubdivisions();
    }
    
    /**
     * Creates a quadrant subdivision (2x2).
     * 
     * @param field Original rectangular field
     */
    public RectangleSubdivision(FieldGrid field) {
        this(field, 2, 2);
    }
    
    /**
     * Generates all subdivision segments.
     */
    private List<FieldGrid> generateSubdivisions() {
        List<FieldGrid> segments = new ArrayList<>();
        
        Coord2d origin = originalField.getOrigin();
        Coord2d dims = originalField.getDimensions();
        
        double segmentWidth = dims.x / columns;
        double segmentHeight = dims.y / rows;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                double minX = origin.x + (col * segmentWidth);
                double minY = origin.y + (row * segmentHeight);
                double maxX = minX + segmentWidth;
                double maxY = minY + segmentHeight;
                
                FieldGrid segment = FieldGrid.rectangle(minX, minY, maxX, maxY);
                segments.add(segment);
            }
        }
        
        return segments;
    }
    
    @Override
    public int getCount() {
        return rows * columns;
    }
    
    @Override
    public FieldGrid getSubdivision(int index) {
        if (index < 0 || index >= subdivisions.size()) {
            throw new IndexOutOfBoundsException("Subdivision index " + index + " out of bounds");
        }
        return subdivisions.get(index);
    }
    
    @Override
    public List<FieldGrid> getAllSubdivisions() {
        return new ArrayList<>(subdivisions);
    }
    
    @Override
    public boolean isInSubdivision(double x, double y, int segmentIndex) {
        if (segmentIndex < 0 || segmentIndex >= subdivisions.size()) {
            return false;
        }
        return subdivisions.get(segmentIndex).contains(new Coord2d(x, y));
    }
    
    /**
     * Gets the row and column indices for a segment.
     * 
     * @param segmentIndex Segment index
     * @return Array of [row, column]
     */
    public int[] getSegmentPosition(int segmentIndex) {
        if (segmentIndex < 0 || segmentIndex >= subdivisions.size()) {
            throw new IndexOutOfBoundsException("Subdivision index out of bounds");
        }
        
        int row = segmentIndex / columns;
        int col = segmentIndex % columns;
        return new int[] {row, col};
    }
    
    /**
     * Gets the segment index at a specific row and column.
     * 
     * @param row Row index
     * @param col Column index
     * @return Segment index
     */
    public int getSegmentIndex(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= columns) {
            throw new IndexOutOfBoundsException("Row or column out of bounds");
        }
        return row * columns + col;
    }
    
    /**
     * @return Number of rows
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * @return Number of columns
     */
    public int getColumns() {
        return columns;
    }
}
