package creature.geeksquad.library;
/**
 * Player controls and key bindings module library for Java.
 * 
 * Author: Ramon A. Lovato, ramonalovato.com
 */

import java.awt.event.*;

import javax.swing.*;

/**
 * Key bindings module library.
 * 
 * @author Ramon A. Lovato
 */
public class KeyBinds {
	private PlayerControls playerControls;
	private InputMap iMap;
	private ActionMap aMap;
	
	/**
	 * Instantiate KeyBinds with the passed JComponent.
	 * 
	 * @param pane JComponent to which to attach the key bindings.
	 */
	public KeyBinds(JComponent pane, PlayerControls playerControls) {
		this.playerControls = playerControls;
		iMap = pane.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        aMap = pane.getActionMap();
        KeyStroke key;
        
        // Up/W.
        key = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
        iMap.put(key, "Up");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true);
        iMap.put(key,  "released Up");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false);
        iMap.put(key, "Up");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true);
        iMap.put(key, "released Up");
        aMap.put("Up", new KeyAction("Up"));
        aMap.put("released Up", new KeyAction("released Up"));
        
        // Down/S.
        key = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
        iMap.put(key, "Down");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true);
        iMap.put(key,  "released Down");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false);
        iMap.put(key, "Down");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true);
        iMap.put(key,  "released Down");
        aMap.put("Down", new KeyAction("Down"));
        aMap.put("released Down", new KeyAction("released Down"));
        
        // Left/A.
        key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false);
        iMap.put(key, "Left");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true);
        iMap.put(key, "released Left");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false);
        iMap.put(key, "Left");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true);
        iMap.put(key, "released Left");
        aMap.put("Left", new KeyAction("Left"));
        aMap.put("released Left", new KeyAction("released Left"));
        
        // Right/D.
        key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false);
        iMap.put(key, "Right");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true);
        iMap.put(key, "released Right");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false);
        iMap.put(key, "Right");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true);
        iMap.put(key,  "released Right");
        aMap.put("Right", new KeyAction("Right"));
        aMap.put("released Right", new KeyAction("released Right"));
        
        // Space.
        key = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false);
        iMap.put(key, "Space");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true);
        iMap.put(key, "released Space");
        aMap.put("Space", new KeyAction("Space"));
        aMap.put("released Space", new KeyAction("released Space"));
        
        // Escape.
        key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        iMap.put(key, "Escape");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
        iMap.put(key, "released Escape");
        aMap.put("Escape", new KeyAction("Escape"));
        aMap.put("released Escape", new KeyAction("released Escape"));
        
        // Enter.
        key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        iMap.put(key, "Enter");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true);
        iMap.put(key, "released Enter");
        aMap.put("Enter", new KeyAction("Enter"));
        aMap.put("released Enter", new KeyAction("released Enter"));
        
        // P.
        key = KeyStroke.getKeyStroke(KeyEvent.VK_P, 0, false);
        iMap.put(key, "Pause");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_P, 0, true);
        iMap.put(key, "released Pause");
        aMap.put("Pause", new KeyAction("Pause"));
        aMap.put("released Pause", new KeyAction("released Pause"));
	}
	
	/**
	 * Add a new key binding. For keyEvent integer code, use the constants in
	 * KeyEvent (e.g. KeyEvent.VK_A, KeyEvent.VK_B, etc.).
	 * 
	 * @param keyEvent Integer code of the keyEvent to bind.
	 * @param keyName String to use as key in the maps.
	 */
	public void addBinding(int keyEvent, String keyName) {
		keyName = keyName.toLowerCase();
		KeyStroke key = KeyStroke.getKeyStroke(keyEvent, 0, false);
		iMap.put(key, keyName);
		key = KeyStroke.getKeyStroke(keyEvent, 0, true);
		iMap.put(key,  "released " + keyName);
		aMap.put(keyName, new KeyAction(keyName));
		aMap.put("released " + keyName, new KeyAction("released " + keyName));
	}
	
	/**
	 * A nested KeyAction class that gets added to the ActionMap.
	 * 
	 * @author Ramon A. Lovato
	 */
    @SuppressWarnings("serial")
	public class KeyAction extends AbstractAction {
        private String action;

        public KeyAction(String action) {
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Test to make sure the main game has been linked.
            if (playerControls != null) {
                playerControls.updateInput(action.toLowerCase());
            }
        }
    }
    
}
