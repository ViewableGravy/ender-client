package auto.farming.ui;

import haven.*;
import auto.farming.model.FarmField;
import auto.farming.FarmingManager;

import java.awt.Color;

/**
 * Fields tab content for the farming configuration window.
 * Displays a list of all fields with filtering, sorting, and action buttons.
 */
public class FieldsTab extends Widget {
    private final FarmingManager manager;
    private final FieldListWidget fieldList;
    private final SearchTextEntry searchBox;
    private final Label statsLabel;
    
    // Action buttons
    private Button addFieldBtn;
    private Button editFieldBtn;
    private Button deleteFieldBtn;
    private Button refreshBtn;
    
    // Sort buttons
    private Button sortNameBtn;
    private Button sortTypeBtn;
    private Button sortAreaBtn;
    
    private FieldListWidget.SortMode currentSort = FieldListWidget.SortMode.NAME;
    private boolean sortAscending = true;
    
    /**
     * Creates a new fields tab.
     */
    public FieldsTab(Coord sz) {
        super(sz);
        this.manager = FarmingManager.getInstance();
        
        int y = 0;
        int margin = UI.scale(5);
        
        // Search box
        add(new Label("Search:"), new Coord(margin, y + UI.scale(5)));
        searchBox = add(new SearchTextEntry(UI.scale(200), this), new Coord(UI.scale(60), y));
        y += UI.scale(30);
        
        // Sort buttons
        add(new Label("Sort by:"), new Coord(margin, y + UI.scale(5)));
        int sortX = UI.scale(60);
        sortNameBtn = add(new Button(UI.scale(80), "Name"), new Coord(sortX, y));
        sortX += UI.scale(85);
        sortTypeBtn = add(new Button(UI.scale(80), "Type"), new Coord(sortX, y));
        sortX += UI.scale(85);
        sortAreaBtn = add(new Button(UI.scale(80), "Area"), new Coord(sortX, y));
        y += UI.scale(30);
        
        // Statistics label
        statsLabel = add(new Label(""), new Coord(margin, y));
        y += UI.scale(25);
        
        // Field list
        fieldList = add(new FieldListWidget(), new Coord(margin, y));
        y += fieldList.sz.y + UI.scale(10);
        
        // Action buttons
        int btnY = y;
        int btnX = margin;
        int btnWidth = UI.scale(120);
        int btnSpacing = UI.scale(10);
        
        addFieldBtn = add(new Button(btnWidth, "Add Field"), new Coord(btnX, btnY));
        btnX += btnWidth + btnSpacing;
        
        editFieldBtn = add(new Button(btnWidth, "Edit Field"), new Coord(btnX, btnY));
        editFieldBtn.hide(); // Hidden until field selected
        btnX += btnWidth + btnSpacing;
        
        deleteFieldBtn = add(new Button(btnWidth, "Delete Field"), new Coord(btnX, btnY));
        deleteFieldBtn.hide(); // Hidden until field selected
        btnX += btnWidth + btnSpacing;
        
        refreshBtn = add(new Button(btnWidth, "Refresh"), new Coord(btnX, btnY));
        
        // Update initial stats
        updateStats();
    }
    
    /**
     * Called when search text changes.
     */
    private void onSearchChanged() {
        fieldList.setFilter(searchBox.text());
        updateStats();
    }
    
    /**
     * Updates the statistics label.
     */
    private void updateStats() {
        int total = manager.getAllFields().size();
        int enabled = (int) manager.getEnabledFields().stream().count();
        double totalArea = manager.getAllFields().stream()
            .mapToDouble(f -> f.getGrid().getArea())
            .sum();
        
        String stats = String.format("Total: %d fields | Enabled: %d | Total Area: %.0f tiles", 
            total, enabled, totalArea);
        statsLabel.settext(stats);
    }
    
    /**
     * Refreshes the field list.
     */
    public void refresh() {
        fieldList.refreshFields();
        updateStats();
        updateButtonStates();
    }
    
    /**
     * Updates button visibility based on selection.
     */
    private void updateButtonStates() {
        FarmField selected = fieldList.getSelectedField();
        if (selected != null) {
            editFieldBtn.show();
            deleteFieldBtn.show();
        } else {
            editFieldBtn.hide();
            deleteFieldBtn.hide();
        }
    }
    
    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == sortNameBtn) {
            setSortMode(FieldListWidget.SortMode.NAME);
        } else if (sender == sortTypeBtn) {
            setSortMode(FieldListWidget.SortMode.TYPE);
        } else if (sender == sortAreaBtn) {
            setSortMode(FieldListWidget.SortMode.AREA);
        } else if (sender == addFieldBtn) {
            onAddField();
        } else if (sender == editFieldBtn) {
            onEditField();
        } else if (sender == deleteFieldBtn) {
            onDeleteField();
        } else if (sender == refreshBtn) {
            refresh();
        } else if (sender == fieldList && msg.equals("changed")) {
            updateButtonStates();
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }
    
    /**
     * Sets the sort mode and toggles direction if same mode.
     */
    private void setSortMode(FieldListWidget.SortMode mode) {
        if (currentSort == mode) {
            sortAscending = !sortAscending;
        } else {
            currentSort = mode;
            sortAscending = true;
        }
        fieldList.setSort(currentSort, sortAscending);
        updateStats();
    }
    
    /**
     * Handles add field button click.
     */
    private void onAddField() {
        // TODO: Open field editor in create mode (FB-3.3)
        System.out.println("Add field - to be implemented in FB-3.3");
    }
    
    /**
     * Handles edit field button click.
     */
    private void onEditField() {
        FarmField selected = fieldList.getSelectedField();
        if (selected != null) {
            // TODO: Open field editor in edit mode (FB-3.3)
            System.out.println("Edit field: " + selected.getName());
        }
    }
    
    /**
     * Handles delete field button click.
     */
    private void onDeleteField() {
        FarmField selected = fieldList.getSelectedField();
        if (selected != null) {
            // Confirm deletion
            String msg = "Delete field '" + selected.getName() + "'?";
            if (ui.gui != null) {
                ui.gui.add(new Window(UI.scale(new Coord(300, 100)), "Confirm Delete") {
                    {
                        add(new Label(msg), UI.scale(new Coord(10, 10)));
                        add(new Button(UI.scale(60), "Yes") {
                            @Override
                            public void click() {
                                manager.getAllFields().remove(selected);
                                try {
                                    manager.saveConfig();
                                } catch (Exception e) {
                                    System.err.println("Failed to save: " + e.getMessage());
                                }
                                refresh();
                                parent.reqdestroy();
                            }
                        }, UI.scale(new Coord(50, 50)));
                        add(new Button(UI.scale(60), "No") {
                            @Override
                            public void click() {
                                parent.reqdestroy();
                            }
                        }, UI.scale(new Coord(140, 50)));
                        pack();
                    }
                }, UI.scale(new Coord(300, 200)));
            }
        }
    }
    
    /**
     * Custom TextEntry that notifies parent on text changes.
     */
    private static class SearchTextEntry extends TextEntry {
        private final FieldsTab parent;
        
        public SearchTextEntry(int width, FieldsTab parent) {
            super(width, "");
            this.parent = parent;
        }
        
        @Override
        public void changed(ReadLine buf) {
            super.changed(buf);
            parent.onSearchChanged();
        }
    }
}
