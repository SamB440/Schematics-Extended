package net.islandearth.schematics.extended;

/**
 * Called when an NBT block has the wrong id
 * @author SamB440
 */
public class WrongIdException extends Exception {

	public WrongIdException(String message) {
		super(message);
	}
}
