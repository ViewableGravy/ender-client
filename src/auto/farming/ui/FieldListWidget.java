package auto.farming.ui;

import haven.*;
import auto.farming.model.FarmField;
import auto.farming.model.FieldType;
import auto.farming.FarmingManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scrollable list widget displaying all farming fields.
 * Provides sorting, filtering, and field selection capabilities.
 */
public class FieldListWidget extends Listbox<FarmField> {
    private static final int ITEM_HEIGHT = UI.scale(25);
    private static final int LIST_WIDTH = UI.scale(540);
    private static final int VISIBLE_ITEMS = 20;
    
    private final FarmingManager manager;
    private List<FarmField> fields;
    private String filterText = "";
    private SortMode sortMode = SortMode.NAME;
    private boolean sortAscending = true;
    
    public enum SortMode {
        NAME("Name"),
        TYPE("Type"),
        AREA("Area"),
        ENABLED("Enabled");
        
        public final String displayName;
        
        SortMode(String displayName) {
            this.displayName = displayName;
        }
    }
    
    /**
     * Creates a new field list widget.
     */
    public FieldListWidget() {
        super(LIST_WIDTH, VISIBLE_ITEMS, ITEM_HEIGHT);
        this.manager = FarmingManager.getInstance();
        this.bgcolor = new Color(0, 0, 0, 180);
        refreshFields();
    }
    
    /**
     * Refreshes the field list from the manager.
     */
    public void refreshFields() {
        fields = new ArrayList<>(manager.getAllFields());
        applyFilterAndSort();
    }
    
    /**
     * Sets the filter text for field names and crop types.
     */
    public void setFilter(String text) {
        this.filterText = text.toLowerCase();
        applyFilterAndSort();
    }
    
    /**
     * Sets the sort mode and direction.
     */
    public void setSort(SortMode mode, boolean ascending) {
        this.sortMode = mode;
        this.sortAscending = ascending;
        applyFilterAndSort();
    }
    
    /**
     * Applies current filter and sort settings.
     */
    private void applyFilterAndSort() {
        // Filter
        List<FarmField> filtered = fields;
        if (!filterText.isEmpty()) {
            filtered = fields.stream()
                .filter(f -> f.getName().toLowerCase().contains(filterText) ||
                            (f.getCropConfig() != null && 
                             f.getCropConfig().getDisplayName().toLowerCase().contains(filterText)))
                .collect(Collectors.toList());
        }
        
        // Sort
        Comparator<FarmField> comparator;
        switch (sortMode) {
            case NAME:
                comparator = Comparator.comparing(FarmField::getName);
                break;
            case TYPE:
                comparator = Comparator.comparing(FarmField::getType);
                break;
            case AREA:
                comparator = Comparator.comparingDouble(f -> f.getGrid().getArea());
                break;
            case ENABLED:
                comparator = Comparator.comparing(FarmField::isEnabled).reversed();
                break;
            default:
                comparator = Comparator.comparing(FarmField::getName);
        }
        
        if (!sortAscending) {
            comparator = comparator.reversed();
        }
        
        filtered.sort(comparator);
        this.fields = filtered;
    }
    
    @Override
    protected FarmField listitem(int idx) {
        return (idx >= 0 && idx < fields.size()) ? fields.get(idx) : null;
    }
    
    @Override
    protected int listitems() {
        return fields.size();
    }
    
    @Override
    protected void drawitem(GOut g, FarmField field, int idx) {
        if (field == null) return;
        
        int x = UI.scale(5);
        int y = UI.scale(3);
        
        // Draw enabled checkbox indicator
        if (field.isEnabled()) {
            g.chcolor(new Color(0, 255, 0, 255));
            g.frect(new Coord(x, y + UI.scale(3)), UI.scale(new Coord(3, 12)));
            g.chcolor();
            x += UI.scale(8);
        } else {
            g.chcolor(new Color(128, 128, 128, 255));
            g.frect(new Coord(x, y + UI.scale(3)), UI.scale(new Coord(3, 12)));
            g.chcolor();
            x += UI.scale(8);
        }
        
        // Draw field type icon (color-coded square)
        Color typeColor = field.getType() == FieldType.QUALITY ? 
            new Color(255, 215, 0) : new Color(50, 205, 50);
        g.chcolor(typeColor);
        g.frect(new Coord(x, y + UI.scale(2)), UI.scale(new Coord(15, 15)));
        g.chcolor();
        x += UI.scale(20);
        
        // Draw field name
        g.atext(field.getName(), new Coord(x, y + UI.scale(10)), 0, 0.5);
        x += UI.scale(200);
        
        // Draw crop name if configured
        if (field.getCropConfig() != null) {
            g.chcolor(new Color(200, 200, 200));
            g.atext(field.getCropConfig().getDisplayName(), new Coord(x, y + UI.scale(10)), 0, 0.5);
            g.chcolor();
            x += UI.scale(120);
        } else {
            x += UI.scale(120);
        }
        
        // Draw area
        g.chcolor(new Color(180, 180, 180));
        int area = (int) field.getGrid().getArea();
        g.atext(area + " tiles", new Coord(x, y + UI.scale(10)), 0, 0.5);
        g.chcolor();
    }
    
    @Override
    protected void itemclick(FarmField item, int button) {
        super.itemclick(item, button);
        if (button == 1 && item != null) {
            // Single click - select field
            change(item);
        } else if (button == 3 && item != null) {
            // Right click - toggle enabled state
            item.setEnabled(!item.isEnabled());
            try {
                manager.saveConfig();
            } catch (Exception e) {
                System.err.println("Failed to save fields: " + e.getMessage());
            }
        }
    }
    
    @Override
    protected void itemactivate(FarmField item) {
        if (item != null) {
            // Double-click - zoom to field (will be implemented when MapView integration is added)
            System.out.println("Double-clicked field: " + item.getName());
            // TODO: Zoom map to field location
        }
    }
    
    /**
     * Gets the currently selected field.
     */
    public FarmField getSelectedField() {
        return sel;
    }
}
