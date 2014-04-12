
package creature.geeksquad.gui;

import creature.geeksquad.library.Helper;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marcos
 */
public class Names {

    private static final List<String> tribeNames = new ArrayList<>();
    private static final List<String> hopperNames = new ArrayList<>();

    public static void loadFiles() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("../data/tribenames.txt"));
            String line;
            line = br.readLine();
            while (line != null) {
                tribeNames.add(line);
                line = br.readLine();
            }

            br = new BufferedReader(new FileReader("../data/hoppernames.txt"));
            line = br.readLine();

            while (line != null) {
                hopperNames.add(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            Log.popup(null, "File not found.");
            Log.error(ex.toString());
        } catch (IOException ex) {
            Log.popup(null, "File not found.");
            Log.error(ex.toString());
        }
    }

    public static String getHopperName() {
        String s;
        if(hopperNames.isEmpty())s = "Zem";
        else s = hopperNames.get(Helper.RANDOM.nextInt(hopperNames.size()));
        return s;
    }

    public static String getTribeName() {
        String s;
        if(tribeNames.isEmpty()) s = "Every One Is Named Zem";
        else s = tribeNames.get(Helper.RANDOM.nextInt(tribeNames.size()));
        return s;
    }
}
