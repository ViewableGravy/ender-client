package auto.farming.ui;

import haven.*;
import auto.farming.FarmingManager;

/**
 * Main configuration window for the farming bot system.
 * Provides a tabbed interface for managing fields, crops, and bot settings.
 */
public class FarmingConfigWindow extends WindowX {
    private static final Coord DEFAULT_SIZE = UI.scale(new Coord(600, 800));
    private static final String WINDOW_CAP = "Farming Configuration";
    
    private final FarmingManager manager;
    private final MapView mapView;
    private final Tabs tabs;
    
    // Tab references
    public final Tabs.Tab fieldsTab;
    public final Tabs.Tab cropSettingsTab;
    public final Tabs.Tab storageTab;
    
    /**
     * Creates a new farming configuration window.
     * @param mapView The map view for field editor
     */
    public FarmingConfigWindow(MapView mapView) {
        super(DEFAULT_SIZE, WINDOW_CAP);
        this.manager = FarmingManager.getInstance();
        this.mapView = mapView;
        
        // Create tabbed interface
        tabs = new Tabs(UI.scale(new Coord(15, 10)), UI.scale(new Coord(570, 700)), this);
        
        // Create tabs
        fieldsTab = tabs.add();
        cropSettingsTab = tabs.add();
        storageTab = tabs.add();
        
        // Create tab buttons
        int tabY = tabs.c.y + tabs.sz.y + UI.scale(10);
        int tabButtonWidth = UI.scale(180);
        int tabSpacing = UI.scale(5);
        
        TabButton fieldsBtn = new TabButton(tabButtonWidth, "Fields", fieldsTab);
        TabButton cropBtn = new TabButton(tabButtonWidth, "Crop Settings", cropSettingsTab);
        TabButton storageBtn = new TabButton(tabButtonWidth, "Storage", storageTab);
        
        // Position tab buttons horizontally
        add(fieldsBtn, new Coord(tabs.c.x, tabY));
        add(cropBtn, new Coord(tabs.c.x + tabButtonWidth + tabSpacing, tabY));
        add(storageBtn, new Coord(tabs.c.x + (tabButtonWidth + tabSpacing) * 2, tabY));
        
        // Initialize tab contents
        initFieldsTab();
        initCropSettingsTab();
        initStorageTab();
        
        // Show first tab
        tabs.showtab(fieldsTab);
        
        pack();
    }
    
    /**
     * Initializes the Fields tab content.
     */
    private void initFieldsTab() {
        fieldsTab.add(new FieldsTab(tabs.sz, mapView), Coord.z);
    }
    
    /**
     * Initializes the Crop Settings tab content.
     */
    private void initCropSettingsTab() {
        // Placeholder - will be implemented in FB-3.5
        cropSettingsTab.add(new Label("Crop Settings tab - Coming soon"), UI.scale(new Coord(10, 10)));
    }
    
    /**
     * Initializes the Storage tab content.
     */
    private void initStorageTab() {
        // Placeholder - will be implemented in FB-3.6
        storageTab.add(new Label("Storage tab - Coming soon"), UI.scale(new Coord(10, 10)));
    }
    
    /**
     * Tab button widget for switching between tabs.
     */
    private class TabButton extends Button {
        private final Tabs.Tab tab;
        
        public TabButton(int width, String text, Tabs.Tab tab) {
            super(width, text, false);
            this.tab = tab;
        }
        
        @Override
        public void click() {
            tabs.showtab(tab);
        }
    }
    
    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (msg.equals("close")) {
            hide();
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }
    
    @Override
    public void destroy() {
        super.destroy();
    }
}
