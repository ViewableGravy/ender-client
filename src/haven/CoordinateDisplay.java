package haven;

import java.awt.Color;

/**
 * Widget that displays player coordinates (world coords and grid coords)
 * For testing farming bot field creation
 */
public class CoordinateDisplay extends Widget {
    private final MapView mapView;
    private static final Text.Foundry fnd = new Text.Foundry(Text.sans, 12).aa(true);
    private static final Color bgColor = new Color(0, 0, 0, 128);
    private static final Color textColor = Color.WHITE;
    
    private String coordText = "";
    private Tex coordTex = null;
    
    public CoordinateDisplay(MapView mapView) {
        super(new Coord(450, 50));  // Widened from 250 to 450 pixels
        this.mapView = mapView;
    }
    
    @Override
    public void tick(double dt) {
        super.tick(dt);
        updateCoordinates();
    }
    
    private void updateCoordinates() {
        if (mapView == null) return;
        
        Gob player = mapView.player();
        if (player == null) {
            coordText = "Waiting for player...";
            return;
        }
        
        try {
            Coord3f playerPos = player.getc();
            if (playerPos == null) {
                coordText = "Loading position...";
                return;
            }
            
            // World coordinates
            double worldX = playerPos.x;
            double worldY = -playerPos.y; // Invert Y for display
            
            // Grid coordinates (divide by tile size = 11)
            double gridX = worldX / 11.0;
            double gridY = playerPos.y / 11.0; // Use non-inverted Y for grid calculation
            
            coordText = String.format("World: (%.1f, %.1f) | Grid: (%.1f, %.1f)", 
                worldX, worldY, gridX, gridY);
        } catch (Loading e) {
            // Map data not loaded yet, ignore
            coordText = "Loading map data...";
        } catch (Exception e) {
            // Any other error, show message but don't crash
            coordText = "Error: " + e.getMessage();
        }
    }
    
    @Override
    public void draw(GOut g) {
        // Draw background
        g.chcolor(bgColor);
        g.frect(Coord.z, sz);
        g.chcolor();
        
        // Draw coordinate text
        if (coordTex == null || !coordText.isEmpty()) {
            coordTex = fnd.render(coordText, textColor).tex();
        }
        
        if (coordTex != null) {
            g.image(coordTex, new Coord(5, (sz.y - coordTex.sz().y) / 2));
        }
    }
}
