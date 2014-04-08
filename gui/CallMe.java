
package creature.geeksquad.gui;

import creature.geeksquad.genetics.Population;
import java.util.concurrent.Callable;

/**
 *
 * @author Marcos
 */
public class CallMe implements Callable<Population>{

    @Override
    public Population call() throws Exception {
        return new Population(Tribe.POPULATION_SIZE);
    }
}
