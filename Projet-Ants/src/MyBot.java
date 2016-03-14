




import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
    /**
     * Main method executed by the game engine for starting the bot.
     * 
     * @param args command line arguments
     * 
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
    	
        new MyBot().readSystemInput();
    }
    
    /**
     * For every ant check every direction in fixed order (N, E, S, W) and move it if the tile is
     * passable.
     */
    /*@Override
    public void doTurn() {
        Ants ants = getAnts();
        for (Tile myAnt : ants.getMyAnts()) {
            for (Aim direction : Aim.values()) {
                if (ants.getIlk(myAnt, direction).isPassable()) {
                    ants.issueOrder(myAnt, direction);
                    break;
                }
            }
        }
    }*/
    
    
    MyBot()
    {
    	ViderLog();
    }
    
    private Map<Tile, Tile> orders = new HashMap<Tile, Tile>();

    
    private Set<Tile> unseenTiles;
    private Set<Tile> enemyHills = new HashSet<Tile>();

    
    private boolean doMoveDirection(Tile antLoc, Aim direction) {
        Ants ants = getAnts();
        // Track all moves, prevent collisions
        Tile newLoc = ants.getTile(antLoc, direction);
        if (ants.getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
            ants.issueOrder(antLoc, direction);
            orders.put(newLoc, antLoc);
            return true;
        } else {
            return false;
        }
    }
    
    
    private boolean doMoveLocation(Tile antLoc, Tile destLoc) {
        Ants ants = getAnts();
        // Track targets to prevent 2 ants to the same location
        List<Aim> directions = ants.getDirections(antLoc, destLoc);
        for (Aim direction : directions) {
            if (doMoveDirection(antLoc, direction)) {
                return true;
            }
        }
        return false;
    }

    
    void ViderLog()
    {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(
				    new File("log.txt"), 
				    false));
			
			writer.println("");

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    void Log(String s)
    {
    	
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(
				    new File("log.txt"), 
				    true));			
			writer.println(s);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
    }


    
    @Override
    public void doTurn() {
    	
    	
    	Log("doTurn début");
    	
    	
    	
        Ants ants = getAnts();
        orders.clear();
        
        //--------------------
        
       
        
        /*Tile [] myAnts = (Tile[]) ants.getMyAnts().toArray();
        Tile a = myAnts[0];
        
        ants.issueOrder(a, Aim.NORTH);
        
        for(int i=0; i<ants.getMyAnts().size();i++)
        {
        	if( i == 0)
        	{
        		ants.issueOrder(myAnts[i], Aim.WEST);
        	}
        	else
        	{
        		ants.issueOrder(myAnts[i], Aim.NORTH);
        	}
        		
        }*/
        
        int i =0;
        for (Tile myAnt : ants.getMyAnts()) {
        	
        	if(i == 0)
        	{
        		i++;
        		if (ants.getIlk(myAnt, Aim.SOUTH).isPassable()) {
                    ants.issueOrder(myAnt, Aim.SOUTH);
                    //break;
                }
        		
        	}
        	
            for (Aim direction : Aim.values()) {
            	
            	
                if (ants.getIlk(myAnt, direction).isPassable()) {
                    ants.issueOrder(myAnt, direction);
                    break;
                }
            }
        }
        
        
        
        if(true)
        	return;
        
        //------------------------
        
        
        Map<Tile, Tile> foodTargets = new HashMap<Tile, Tile>();

        
        
        // add all locations to unseen tiles set, run once
        if (unseenTiles == null) {
            unseenTiles = new HashSet<Tile>();
            for (int row = 0; row < ants.getRows(); row++) {
                for (int col = 0; col < ants.getCols(); col++) {
                    unseenTiles.add(new Tile(row, col));
                }
            }
        }
        // remove any tiles that can be seen, run each turn
        for (Iterator<Tile> locIter = unseenTiles.iterator(); locIter.hasNext(); ) {
            Tile next = locIter.next();
            if (ants.isVisible(next)) {
                locIter.remove();
            }
        }
        
        
        
     // prevent stepping on own hill
        for (Tile myHill : ants.getMyHills()) {
            orders.put(myHill, null);
        }

        
        
        // find close food
        List<Route> foodRoutes = new ArrayList<Route>();
        TreeSet<Tile> sortedFood = new TreeSet<Tile>(ants.getFoodTiles());
        TreeSet<Tile> sortedAnts = new TreeSet<Tile>(ants.getMyAnts());
        for (Tile foodLoc : sortedFood) {
            for (Tile antLoc : sortedAnts) {
                int distance = ants.getDistance(antLoc, foodLoc);
                Route route = new Route(antLoc, foodLoc, distance);
                foodRoutes.add(route);
            }
        }
        Collections.sort(foodRoutes);
        for (Route route : foodRoutes) {
            if (!foodTargets.containsKey(route.getEnd())
                    && !foodTargets.containsValue(route.getStart())
                    && doMoveLocation(route.getStart(), route.getEnd())) {
                foodTargets.put(route.getEnd(), route.getStart());
            }
        }
        
        
     // add new hills to set
        for (Tile enemyHill : ants.getEnemyHills()) {
            if (!enemyHills.contains(enemyHill)) {
                enemyHills.add(enemyHill);
            }
        }
        // attack hills
        List<Route> hillRoutes = new ArrayList<Route>();
        for (Tile hillLoc : enemyHills) {
            for (Tile antLoc : sortedAnts) {
                if (!orders.containsValue(antLoc)) {
                    int distance = ants.getDistance(antLoc, hillLoc);
                    Route route = new Route(antLoc, hillLoc, distance);
                    hillRoutes.add(route);
                }
            }
        }
        Collections.sort(hillRoutes);
        for (Route route : hillRoutes) {
            doMoveLocation(route.getStart(), route.getEnd());
        }

        
        
        
        
     // explore unseen areas
        for (Tile antLoc : sortedAnts) {
            if (!orders.containsValue(antLoc)) {
                List<Route> unseenRoutes = new ArrayList<Route>();
                for (Tile unseenLoc : unseenTiles) {
                    int distance = ants.getDistance(antLoc, unseenLoc);
                    Route route = new Route(antLoc, unseenLoc, distance);
                    unseenRoutes.add(route);
                }
                Collections.sort(unseenRoutes);
                for (Route route : unseenRoutes) {
                    if (doMoveLocation(route.getStart(), route.getEnd())) {
                        break;
                    }
                }
            }
        }


        
        
        
     // unblock hills ( envoie les fourmis qui bloque le nid ailleur)
        for (Tile myHill : ants.getMyHills()) {
            if (ants.getMyAnts().contains(myHill) && !orders.containsValue(myHill)) {
                for (Aim direction : Aim.values()) {
                    if (doMoveDirection(myHill, direction)) {
                        break;
                    }
                }
            }
        }

        
        
        
        
        
    }
    
    
    
}
