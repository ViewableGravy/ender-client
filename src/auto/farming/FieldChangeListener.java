package auto.farming;

import auto.farming.model.FarmField;

/**
 * Listener interface for farm field changes.
 * Implement this to receive notifications when fields are added, updated, or removed.
 */
public interface FieldChangeListener {
    /**
     * Called when a field is added to the manager.
     * @param field The field that was added
     */
    void onFieldAdded(FarmField field);
    
    /**
     * Called when a field is updated.
     * @param field The field that was updated
     */
    void onFieldUpdated(FarmField field);
    
    /**
     * Called when a field is removed from the manager.
     * @param field The field that was removed
     */
    void onFieldRemoved(FarmField field);
}
