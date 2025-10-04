package auto.farming.ui.overlay;

import java.awt.Color;

/**
 * Configuration settings for the farming overlay renderer.
 * Controls visibility, colors, and rendering options.
 */
public class OverlaySettings {
    // Visibility toggles
    private boolean showFieldBoundaries = true;
    private boolean showSubdivisions = true;
    private boolean showTileStates = false;
    private boolean showBotActivity = false;
    private boolean showPerformanceMetrics = false;
    
    // Color settings
    private Color qualityFieldColor = new Color(255, 215, 0, 100); // Gold
    private Color quantityFieldColor = new Color(50, 205, 50, 100); // Lime green
    private Color subdivisionColor = new Color(255, 255, 255, 80); // White semi-transparent
    private Color activeFieldColor = new Color(255, 165, 0, 200); // Orange
    
    // Rendering settings
    private int fieldBorderWidth = 2;
    private int subdivisionBorderWidth = 1;
    private int activeFieldBorderWidth = 3;
    private int fieldOpacity = 100; // 0-255
    
    /**
     * Creates default overlay settings.
     */
    public OverlaySettings() {
    }
    
    // Visibility getters/setters
    public boolean isShowFieldBoundaries() {
        return showFieldBoundaries;
    }
    
    public void setShowFieldBoundaries(boolean show) {
        this.showFieldBoundaries = show;
    }
    
    public boolean isShowSubdivisions() {
        return showSubdivisions;
    }
    
    public void setShowSubdivisions(boolean show) {
        this.showSubdivisions = show;
    }
    
    public boolean isShowTileStates() {
        return showTileStates;
    }
    
    public void setShowTileStates(boolean show) {
        this.showTileStates = show;
    }
    
    public boolean isShowBotActivity() {
        return showBotActivity;
    }
    
    public void setShowBotActivity(boolean show) {
        this.showBotActivity = show;
    }
    
    public boolean isShowPerformanceMetrics() {
        return showPerformanceMetrics;
    }
    
    public void setShowPerformanceMetrics(boolean show) {
        this.showPerformanceMetrics = show;
    }
    
    // Color getters/setters
    public Color getQualityFieldColor() {
        return qualityFieldColor;
    }
    
    public void setQualityFieldColor(Color color) {
        this.qualityFieldColor = color;
    }
    
    public Color getQuantityFieldColor() {
        return quantityFieldColor;
    }
    
    public void setQuantityFieldColor(Color color) {
        this.quantityFieldColor = color;
    }
    
    public Color getSubdivisionColor() {
        return subdivisionColor;
    }
    
    public void setSubdivisionColor(Color color) {
        this.subdivisionColor = color;
    }
    
    public Color getActiveFieldColor() {
        return activeFieldColor;
    }
    
    public void setActiveFieldColor(Color color) {
        this.activeFieldColor = color;
    }
    
    // Rendering settings getters/setters
    public int getFieldBorderWidth() {
        return fieldBorderWidth;
    }
    
    public void setFieldBorderWidth(int width) {
        this.fieldBorderWidth = Math.max(1, Math.min(10, width));
    }
    
    public int getSubdivisionBorderWidth() {
        return subdivisionBorderWidth;
    }
    
    public void setSubdivisionBorderWidth(int width) {
        this.subdivisionBorderWidth = Math.max(1, Math.min(10, width));
    }
    
    public int getActiveFieldBorderWidth() {
        return activeFieldBorderWidth;
    }
    
    public void setActiveFieldBorderWidth(int width) {
        this.activeFieldBorderWidth = Math.max(1, Math.min(10, width));
    }
    
    public int getFieldOpacity() {
        return fieldOpacity;
    }
    
    public void setFieldOpacity(int opacity) {
        this.fieldOpacity = Math.max(0, Math.min(255, opacity));
    }
    
    /**
     * Creates a copy of these settings.
     */
    public OverlaySettings copy() {
        OverlaySettings copy = new OverlaySettings();
        copy.showFieldBoundaries = this.showFieldBoundaries;
        copy.showSubdivisions = this.showSubdivisions;
        copy.showTileStates = this.showTileStates;
        copy.showBotActivity = this.showBotActivity;
        copy.showPerformanceMetrics = this.showPerformanceMetrics;
        copy.qualityFieldColor = this.qualityFieldColor;
        copy.quantityFieldColor = this.quantityFieldColor;
        copy.subdivisionColor = this.subdivisionColor;
        copy.activeFieldColor = this.activeFieldColor;
        copy.fieldBorderWidth = this.fieldBorderWidth;
        copy.subdivisionBorderWidth = this.subdivisionBorderWidth;
        copy.activeFieldBorderWidth = this.activeFieldBorderWidth;
        copy.fieldOpacity = this.fieldOpacity;
        return copy;
    }
}
