package creature.geeksquad.library;
/**
 * Player controls and key bindings module library for Java.
 * 
 * Author: Ramon A. Lovato, ramonalovato.com
 */


import java.util.HashMap;
import java.util.Map;

/**
 * Player controls module library.
 * 
 * @author Ramon A. Lovato
 */
public class PlayerControls {
    // A hash map of key states booleans for the inputs.
    private Map<String, Boolean> input;
    
    /**
     * Initialize PlayerControls.
     */
    public PlayerControls() {
	    input = new HashMap<String, Boolean>();
	    input.put("up", false);
	    input.put("down", false);
	    input.put("left", false);
	    input.put("right", false);
	    input.put("space", false);
	    input.put("escape", false);
	    input.put("enter", false);
	    input.put("pause", false);
    }
    
    /**
     * Updates the input map based on the provided input. Called by KeyBinds.
     * 
     * @param str String representation of the key state change.
     */
    public void updateInput(String str) {
        Boolean state = (str.startsWith("released") ? false : true);
        String key = (state ? str : str.substring(9));
        input.put(key, state);
        // If one of the directions was pressed, automatically release the
        // opposite direction.
        if (state) {
            switch (str) {
                case "up":
                    input.put("down", false);
                    break;
                case "down":
                    input.put("up", false);
                    break;
                case "left":
                    input.put("right", false);
                    break;
                case "right":
                    input.put("left", false);
                    break;
                default:
                    break;
            }
        }
        assert(input.get(key) == state);
    }
    
    /**
     * Accessor for the map of input states.
     * 
     * @return HashMap<String, Boolean> of the input states.
     */
    public Map<String, Boolean> getInputs() {
    	return input;
    }
    
    /**
     * Disable the input for the passed key String.
     * 
     * @param key Key String for the input to disable.
     */
    public void disable(String key) {
    	input.put(key, false);
    }
    
    /**
     * Disable all input states.
     */
    public void disableAll() {
    	for(Map.Entry<String, Boolean> entry : input.entrySet()) {
    		input.put(entry.getKey(), false);
    	}
    }
    
    /**
     * Add a key binding option to the map.
     * 
     * @param key String name for the binding to be added.
     */
    public void addBinding(String key) {
    	input.put(key.toLowerCase(), false);
    	
    }
}
