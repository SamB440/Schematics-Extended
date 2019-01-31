package net.islandearth.schematics.extended;

/**
 * Called when an NBT block has the wrong id
 * @author SamB440
 */
public class WrongIdException extends Exception {
	
	private static final long serialVersionUID = 4472556003462521095L;

	public WrongIdException(String message) {
		super(message);
	}
}
