package net.islandearth.schematics.extended;

/**
 * Called when a schematic's data has not been loaded.
 * @author SamB440
 */
public class SchematicNotLoadedException extends Exception {


	private static final long serialVersionUID = -8805283716076251297L;
	
	public SchematicNotLoadedException(String message) {
		super(message);
	}
}
