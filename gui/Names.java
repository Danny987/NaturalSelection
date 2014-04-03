
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
    
    public static void loadFiles(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("tribenames.txt"));
            String line;
            line = br.readLine();
            while(line != null){
                tribeNames.add(line);
                line = br.readLine();
            }
            
            br = new BufferedReader(new FileReader("hoppernames.txt"));
            line = br.readLine();
            
            while(line != null){
                hopperNames.add(line);
                line = br.readLine();
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("file not found");
        } catch (IOException ex) {
            System.out.println("io exception");
        }
    }
    
    public static String getHopperName(){
        return hopperNames.get(Helper.RANDOM.nextInt(hopperNames.size())); 
    }
    
    public static String getTribeName(){
        return tribeNames.get(Helper.RANDOM.nextInt(tribeNames.size())); 
    }
}
