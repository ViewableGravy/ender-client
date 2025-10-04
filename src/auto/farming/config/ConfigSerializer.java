package auto.farming.config;

import auto.farming.model.*;
import com.google.gson.*;
import haven.Coord2d;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Custom serializers and deserializers for farming data models.
 */
public class ConfigSerializer {
    
    /**
     * UUID serializer for compact JSON representation.
     */
    public static class UUIDAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {
        @Override
        public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
        
        @Override
        public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return UUID.fromString(json.getAsString());
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Invalid UUID format: " + json.getAsString(), e);
            }
        }
    }
    
    /**
     * Coord2d serializer for compact JSON representation.
     */
    public static class Coord2dAdapter implements JsonSerializer<Coord2d>, JsonDeserializer<Coord2d> {
        @Override
        public JsonElement serialize(Coord2d src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("x", src.x);
            obj.addProperty("y", src.y);
            return obj;
        }
        
        @Override
        public Coord2d deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            return new Coord2d(obj.get("x").getAsDouble(), obj.get("y").getAsDouble());
        }
    }
    
    /**
     * FieldGrid serializer that handles both rectangle and circle types.
     */
    public static class FieldGridAdapter implements JsonSerializer<FieldGrid>, JsonDeserializer<FieldGrid> {
        @Override
        public JsonElement serialize(FieldGrid src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("shape", src.getShape().name());
            
            if (src.getShape() == FieldShape.RECTANGLE) {
                obj.add("bounds", context.serialize(src.getBounds()));
            } else if (src.getShape() == FieldShape.CIRCLE) {
                obj.add("center", context.serialize(src.getCenter()));
                obj.addProperty("radius", src.getRadius());
            }
            
            return obj;
        }
        
        @Override
        public FieldGrid deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            FieldShape shape = FieldShape.valueOf(obj.get("shape").getAsString());
            
            if (shape == FieldShape.RECTANGLE) {
                // Deserialize bounds as [minX, minY, maxX, maxY]
                JsonArray boundsArray = obj.getAsJsonArray("bounds");
                Coord2d min = new Coord2d(boundsArray.get(0).getAsDouble(), boundsArray.get(1).getAsDouble());
                Coord2d max = new Coord2d(boundsArray.get(2).getAsDouble(), boundsArray.get(3).getAsDouble());
                return FieldGrid.rectangle(min.x, min.y, max.x, max.y);
            } else if (shape == FieldShape.CIRCLE) {
                Coord2d center = context.deserialize(obj.get("center"), Coord2d.class);
                double radius = obj.get("radius").getAsDouble();
                return FieldGrid.circle(center.x, center.y, radius);
            }
            
            throw new JsonParseException("Unknown field shape: " + shape);
        }
    }
    
    /**
     * Creates a Gson instance configured with all custom serializers.
     * @return Configured Gson instance
     */
    public static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(UUID.class, new UUIDAdapter())
                .registerTypeAdapter(Coord2d.class, new Coord2dAdapter())
                .registerTypeAdapter(FieldGrid.class, new FieldGridAdapter())
                .create();
    }
}
