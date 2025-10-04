package auto.farming.model;

/**
 * Manual test class for FarmField functionality.
 * Run this class to verify FarmField implementation.
 */
public class FarmFieldTest {
    
    public static void main(String[] args) {
        System.out.println("=== FarmField Manual Tests ===\n");
        
        testBasicCreation();
        testValidation();
        testEqualsAndHashCode();
        testToString();
        testSetters();
        
        System.out.println("\n=== All Tests Passed! ===");
    }
    
    private static void testBasicCreation() {
        System.out.println("Test 1: Basic field creation");
        
        FarmField field = new FarmField("Test Farm", FieldType.QUALITY, FieldShape.RECTANGLE);
        
        assert field.getId() != null : "ID should not be null";
        assert field.getName().equals("Test Farm") : "Name should be 'Test Farm'";
        assert field.getType() == FieldType.QUALITY : "Type should be QUALITY";
        assert field.getShape() == FieldShape.RECTANGLE : "Shape should be RECTANGLE";
        assert field.isEnabled() : "Field should be enabled by default";
        assert field.getCreatedAt() > 0 : "CreatedAt should be set";
        
        System.out.println("  ✓ Basic creation works correctly");
    }
    
    private static void testValidation() {
        System.out.println("Test 2: Validation");
        
        // Test constructor validation - null name
        try {
            new FarmField(null, FieldType.QUALITY, FieldShape.RECTANGLE);
            assert false : "Should throw exception for null name";
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejects null name in constructor");
        }
        
        // Test constructor validation - empty name
        try {
            new FarmField("  ", FieldType.QUALITY, FieldShape.RECTANGLE);
            assert false : "Should throw exception for empty name";
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejects empty name in constructor");
        }
        
        // Test constructor validation - null type
        try {
            new FarmField("Test", null, FieldShape.RECTANGLE);
            assert false : "Should throw exception for null type";
        } catch (NullPointerException e) {
            System.out.println("  ✓ Correctly rejects null type in constructor");
        }
        
        // Test constructor validation - null shape
        try {
            new FarmField("Test", FieldType.QUALITY, null);
            assert false : "Should throw exception for null shape";
        } catch (NullPointerException e) {
            System.out.println("  ✓ Correctly rejects null shape in constructor");
        }
        
        // Test validate() method - missing crop type
        FarmField field = new FarmField("Test", FieldType.QUALITY, FieldShape.RECTANGLE);
        try {
            field.validate();
            assert false : "Should throw exception when crop type is missing";
        } catch (IllegalStateException e) {
            System.out.println("  ✓ Correctly rejects missing crop type in validate()");
        }
        
        // Valid field with crop type
        field.setCropType("gfx/terobjs/plants/wheat");
        field.validate(); // Should not throw
        System.out.println("  ✓ Valid field passes validation");
    }
    
    private static void testEqualsAndHashCode() {
        System.out.println("Test 3: Equals and HashCode");
        
        FarmField field1 = new FarmField("Farm 1", FieldType.QUALITY, FieldShape.RECTANGLE);
        FarmField field2 = new FarmField("Farm 2", FieldType.QUANTITY, FieldShape.CIRCLE);
        FarmField field1Copy = field1;
        
        // Test equals with same instance
        assert field1.equals(field1Copy) : "Field should equal itself";
        System.out.println("  ✓ Field equals itself");
        
        // Test equals with different instances
        assert !field1.equals(field2) : "Different fields should not be equal";
        System.out.println("  ✓ Different fields are not equal");
        
        // Test equals with null
        assert !field1.equals(null) : "Field should not equal null";
        System.out.println("  ✓ Field does not equal null");
        
        // Test equals with different type
        assert !field1.equals("Not a field") : "Field should not equal different type";
        System.out.println("  ✓ Field does not equal different type");
        
        // Test hashCode consistency
        assert field1.hashCode() == field1Copy.hashCode() : "Same field should have same hashCode";
        System.out.println("  ✓ HashCode is consistent");
    }
    
    private static void testToString() {
        System.out.println("Test 4: ToString");
        
        FarmField field = new FarmField("My Farm", FieldType.QUALITY, FieldShape.RECTANGLE);
        field.setCropType("gfx/terobjs/plants/wheat");
        
        String str = field.toString();
        
        assert str.contains(field.getId().toString()) : "toString should contain ID";
        assert str.contains("My Farm") : "toString should contain name";
        assert str.contains("QUALITY") : "toString should contain type";
        assert str.contains("wheat") : "toString should contain crop type";
        
        System.out.println("  ✓ toString format: " + str);
    }
    
    private static void testSetters() {
        System.out.println("Test 5: Setters");
        
        FarmField field = new FarmField("Test", FieldType.QUALITY, FieldShape.RECTANGLE);
        
        // Test setName
        field.setName("New Name");
        assert field.getName().equals("New Name") : "Name should be updated";
        System.out.println("  ✓ setName works");
        
        try {
            field.setName(null);
            assert false : "Should reject null name";
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ setName rejects null");
        }
        
        // Test setType
        field.setType(FieldType.QUANTITY);
        assert field.getType() == FieldType.QUANTITY : "Type should be updated";
        System.out.println("  ✓ setType works");
        
        try {
            field.setType(null);
            assert false : "Should reject null type";
        } catch (NullPointerException e) {
            System.out.println("  ✓ setType rejects null");
        }
        
        // Test setShape
        field.setShape(FieldShape.CIRCLE);
        assert field.getShape() == FieldShape.CIRCLE : "Shape should be updated";
        System.out.println("  ✓ setShape works");
        
        // Test setCropType
        field.setCropType("gfx/terobjs/plants/barley");
        assert field.getCropType().equals("gfx/terobjs/plants/barley") : "Crop type should be updated";
        System.out.println("  ✓ setCropType works");
        
        // Test setEnabled
        field.setEnabled(false);
        assert !field.isEnabled() : "Enabled should be false";
        field.setEnabled(true);
        assert field.isEnabled() : "Enabled should be true";
        System.out.println("  ✓ setEnabled works");
        
        // Test setCreatedAt
        long timestamp = 1234567890L;
        field.setCreatedAt(timestamp);
        assert field.getCreatedAt() == timestamp : "CreatedAt should be updated";
        System.out.println("  ✓ setCreatedAt works");
        
        // Test setGridOrigin (can be null)
        field.setGridOrigin(null);
        assert field.getGridOrigin() == null : "GridOrigin can be null";
        System.out.println("  ✓ setGridOrigin allows null");
    }
}
