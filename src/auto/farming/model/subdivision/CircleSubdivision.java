package auto.farming.model.subdivision;

import auto.farming.model.FieldGrid;
import auto.farming.model.FieldShape;
import haven.Coord2d;
import java.util.ArrayList;
import java.util.List;

/**
 * Circular field subdivision using radial "pie slice" segments.
 * 
 * <p>Divides a circular field into equal-angle segments radiating from the center.
 * Each segment is a wedge-shaped region defined by start and end angles.</p>
 */
public class CircleSubdivision implements FieldSubdivision {
    private final FieldGrid originalField;
    private final int segments;
    private final List<Segment> subdivisions;
    
    /**
     * Represents a radial segment (pie slice) of a circular field.
     */
    private static class Segment {
        final Coord2d center;
        final double radius;
        final double startAngle;  // In radians
        final double endAngle;    // In radians
        
        Segment(Coord2d center, double radius, double startAngle, double endAngle) {
            this.center = center;
            this.radius = radius;
            this.startAngle = startAngle;
            this.endAngle = endAngle;
        }
        
        /**
         * Checks if a point is within this segment.
         */
        boolean contains(double x, double y) {
            // Check if point is within circle radius
            double dx = x - center.x;
            double dy = y - center.y;
            double distSquared = dx * dx + dy * dy;
            
            if (distSquared > radius * radius) {
                return false;
            }
            
            // Check if point is within angle range
            double angle = Math.atan2(dy, dx);
            
            // Normalize angle to [0, 2π)
            if (angle < 0) {
                angle += 2 * Math.PI;
            }
            
            // Handle wrap-around case (segment crosses 0°)
            if (startAngle > endAngle) {
                return angle >= startAngle || angle <= endAngle;
            } else {
                return angle >= startAngle && angle <= endAngle;
            }
        }
        
        /**
         * Creates a FieldGrid approximation of this segment.
         * Note: Returns the full circle grid for now - tiles will be filtered by contains()
         */
        FieldGrid toFieldGrid() {
            return FieldGrid.circle(center.x, center.y, radius);
        }
    }
    
    /**
     * Creates a radial subdivision.
     * 
     * @param field Original circular field
     * @param segments Number of radial segments
     * @throws IllegalArgumentException if field is not circular or segments < 1
     */
    public CircleSubdivision(FieldGrid field, int segments) {
        if (field.getShape() != FieldShape.CIRCLE) {
            throw new IllegalArgumentException("Field must be circular");
        }
        if (segments < 1) {
            throw new IllegalArgumentException("Segments must be at least 1");
        }
        
        this.originalField = field;
        this.segments = segments;
        this.subdivisions = generateSubdivisions();
    }
    
    /**
     * Creates a quadrant subdivision (4 segments).
     * 
     * @param field Original circular field
     */
    public CircleSubdivision(FieldGrid field) {
        this(field, 4);
    }
    
    /**
     * Generates all radial subdivision segments.
     */
    private List<Segment> generateSubdivisions() {
        List<Segment> segs = new ArrayList<>();
        
        Coord2d center = originalField.getOrigin();
        double radius = originalField.getDimensions().x;
        
        double anglePerSegment = (2 * Math.PI) / segments;
        
        for (int i = 0; i < segments; i++) {
            double startAngle = i * anglePerSegment;
            double endAngle = (i + 1) * anglePerSegment;
            
            segs.add(new Segment(center, radius, startAngle, endAngle));
        }
        
        return segs;
    }
    
    @Override
    public int getCount() {
        return segments;
    }
    
    @Override
    public FieldGrid getSubdivision(int index) {
        if (index < 0 || index >= subdivisions.size()) {
            throw new IndexOutOfBoundsException("Subdivision index " + index + " out of bounds");
        }
        return subdivisions.get(index).toFieldGrid();
    }
    
    @Override
    public List<FieldGrid> getAllSubdivisions() {
        List<FieldGrid> grids = new ArrayList<>();
        for (Segment seg : subdivisions) {
            grids.add(seg.toFieldGrid());
        }
        return grids;
    }
    
    @Override
    public boolean isInSubdivision(double x, double y, int segmentIndex) {
        if (segmentIndex < 0 || segmentIndex >= subdivisions.size()) {
            return false;
        }
        return subdivisions.get(segmentIndex).contains(x, y);
    }
    
    /**
     * Gets the angle range for a segment.
     * 
     * @param segmentIndex Segment index
     * @return Array of [startAngle, endAngle] in radians
     */
    public double[] getSegmentAngleRange(int segmentIndex) {
        if (segmentIndex < 0 || segmentIndex >= subdivisions.size()) {
            throw new IndexOutOfBoundsException("Subdivision index out of bounds");
        }
        
        Segment seg = subdivisions.get(segmentIndex);
        return new double[] {seg.startAngle, seg.endAngle};
    }
    
    /**
     * Gets the angle range for a segment in degrees.
     * 
     * @param segmentIndex Segment index
     * @return Array of [startAngle, endAngle] in degrees
     */
    public double[] getSegmentAngleRangeDegrees(int segmentIndex) {
        double[] radians = getSegmentAngleRange(segmentIndex);
        return new double[] {
            Math.toDegrees(radians[0]),
            Math.toDegrees(radians[1])
        };
    }
    
    /**
     * Determines which segment a point belongs to.
     * 
     * @param x Grid X coordinate
     * @param y Grid Y coordinate
     * @return Segment index, or -1 if point is outside the circle
     */
    public int getSegmentAt(double x, double y) {
        for (int i = 0; i < subdivisions.size(); i++) {
            if (subdivisions.get(i).contains(x, y)) {
                return i;
            }
        }
        return -1;
    }
}
