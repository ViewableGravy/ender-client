package auto.farming.ui.editor;

/**
 * Editor modes for field creation and editing.
 * Controls the type of interaction and drawing behavior.
 */
public enum EditorMode {
    /**
     * Editor is inactive.
     */
    OFF,
    
    /**
     * Creating a rectangular field by clicking opposite corners.
     */
    RECTANGLE,
    
    /**
     * Creating a circular field by clicking center and dragging for radius.
     */
    CIRCLE,
    
    /**
     * Editing an existing field's properties or boundaries.
     */
    EDIT
}
