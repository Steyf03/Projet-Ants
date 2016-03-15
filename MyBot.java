




import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
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
    
     int nbDeTour = 0;
    
    MyBot()
    {
    	ViderLog();
    }
    
    private Map<Tile, Tile> orders = new HashMap<Tile, Tile>();

    
    private Set<Tile> unseenTiles;
    private Set<Tile> enemyHills = new HashSet<Tile>();

    

    
    
    

    
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
    
    Tile caseDirection( Tile t, Aim a)
    {
    	int col = t.getCol();
    	int row = t.getRow();
    	
    	if(a == Aim.EAST)
    		col++;
    	if(a == Aim.NORTH)
    		row--;
    	if(a == Aim.SOUTH)
    		row++;
    	if(a == Aim.WEST)
    		col--;
    	
    	return new Tile(row, col);
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


class Fourmie{
    	
	Tile positionTourPrécédant;
	
    	Tile position;
    	boolean morte = false;

    	
    	//void déplacer
    	
    	Fourmie( Tile positionDeNaissance)
    	{
    		position = positionDeNaissance;
    		positionTourPrécédant = positionDeNaissance;
    	}
    	
    	public Tile getPosition()
    	{
    		return position;
    	}
    	
    	public void deplacerUnseen( List<Route> unseenRoutes)
    	{
    		int i = 0;
    		for (Route route : unseenRoutes) {
    			
                if (doMoveLocation(/*route.getStart(),*/ route.getEnd())  || i > 10) {
                	//Log("f1 doit aller en " + route.getEnd());
                	
                    break;
                }
            }

    	}
    	
    	
    	private boolean doMoveLocation(/*Tile antLoc,*/ Tile destLoc) {
    		Tile positionAvantDeplacement = position;
    		Ants ants = getAnts();
            // Track targets to prevent 2 ants to the same location
            List<Aim> directions = ants.getDirections(position, destLoc);
            for (Aim direction : directions) {
            	
            	Log(" j'essaye d'aller vers : " + caseDirection(position, direction).toString());
            	Log(" j'étais au tour d'avant en : " + positionTourPrécédant);
            	
                if (doMoveDirection(/*antLoc,*/ direction)) {
                	positionTourPrécédant = positionAvantDeplacement;
                    return true;
                }
            }
            return false;
        }
    	
        private boolean doMoveDirection(/*Tile antLoc,*/ Aim direction) {
            Ants ants = getAnts();
            // Track all moves, prevent collisions
            /*Tile newLoc = ants.getTile(antLoc, direction);
            if (ants.getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc)) {
                ants.issueOrder(antLoc, direction);
                orders.put(newLoc, antLoc);*/
            Tile newLoc = ants.getTile(position, direction);
            if (ants.getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc) && !caseDirection(position, direction).equals(positionTourPrécédant)) {
                ants.issueOrder(position, direction);
                orders.put(newLoc, position);
                position = newLoc;
            	
            	Log("f1 est en " + position);
                
                return true;
            } else {
                return false;
            }
        }
    	
        private void mourrir()
        {
        	morte = true;
        }
        
        
        
        
    }
    
    
    ArrayList<Fourmie> listeNosFourmies = new ArrayList<Fourmie>();
    
    
    
    
    
    
    
    
    @Override
    public void doTurn() {
    	
    	nbDeTour++;
    	
    	try{
    	Log("doTurn début");
    	
    	
    	
        Ants ants = getAnts();
        
        //test de spawn des ants
        
        ArrayList<Tile> positionNosFourmies = new ArrayList<Tile>() ;
			for(Fourmie f:listeNosFourmies)
			{
				if(!f.morte)
				{
				positionNosFourmies.add(f.getPosition());
				
				Log( " - " + f.getPosition().toString() );
				}
			}
			
        
        for(Tile ant:ants.getMyAnts())
        {
        	if(!positionNosFourmies.contains(ant))
        	{
        		listeNosFourmies.add(new Fourmie (ant));
        		Log("une fourmie est née");
        	}
        }
        
        //test de mort des ants
        for(Tile ant:positionNosFourmies)
        {
        	if(!ants.getMyAnts().contains(ant))
        	{
        		for(Fourmie f:listeNosFourmies)
        		{
        			if (f.position.equals(ant))
        			{
        				//listeNosFourmies.remove(f);
        				f.mourrir();
        				Log( "  X " + f.position + "morte");
        				
        			}
        		}
        		Log("une fourmie est morte : " + ant);
        	}
        }
        
        
        
        orders.clear();
        
        //--------------------
        
       
        
       
        
        
        
        

        
       
        /*if(listeNosFourmies.size() > 0)
        {

        	
        	
        	if (unseenTiles == null) {
                unseenTiles = new HashSet<Tile>();
                for (int row = 0; row < ants.getRows(); row++) {
                    for (int col = 0; col < ants.getCols(); col++) {
                        unseenTiles.add(new Tile(row, col));
                    }
                }
            }
        	
        	//Log( "unseenTiles créé" );
        	
        	if(!listeNosFourmies.get(0).morte)
            if (!orders.containsValue(listeNosFourmies.get(0).position)) {
            	
            	
            	
                List<Route> unseenRoutes = new ArrayList<Route>();
                for (Tile unseenLoc : unseenTiles) {
                    int distance = ants.getDistance(listeNosFourmies.get(0).position, unseenLoc);
                    Route route = new Route(listeNosFourmies.get(0).position, unseenLoc, distance);
                	/*int distance = ants.getDistance(listeNosFourmies.get(0).position, new Tile(14,19));
                    Route route = new Route(listeNosFourmies.get(0).position, new Tile(14,19), distance);*/
                	
   /*    unseenRoutes.add(route);
                }
                Collections.sort(unseenRoutes);
                
                
                listeNosFourmies.get(0).deplacerUnseen(unseenRoutes);
    
            }
        
        	
        	
        }
        
        
        
        
        
        
        
        
        Log("doTurn fin");
        Log("");
        
        if(true)
        	return;*/
        
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
        Log("ne pas marcher sur le nid");
        for (Tile myHill : ants.getMyHills()) {
            orders.put(myHill, null);
        }

        
        
        // find close food
        Log("detection nourriture");
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
        int y = 0 ;
        for (Route route : foodRoutes) {
        	
        	
        	
        	
        	Fourmie laFourmie = null;
            		for(Fourmie f:listeNosFourmies)
            		{
            			if (f.position.equals(route.getStart()))
            			{
            				laFourmie = f;
            			}
            		}
            	
            Log("route nourriture " + y);
        	
        	
            if ( (!foodTargets.containsKey(route.getEnd())
                    && !foodTargets.containsValue(route.getStart())
                    && laFourmie.doMoveLocation(/*route.getStart(),*/ route.getEnd())) ||  y>2) {
                foodTargets.put(route.getEnd(), route.getStart());
            }
        }
        
        
     // add new hills to set
        Log("detection nid");
        for (Tile enemyHill : ants.getEnemyHills()) {
            if (!enemyHills.contains(enemyHill)) {
            	
                enemyHills.add(enemyHill);
            }
        }
        // attack hills
        Log("attaque nid");
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
        	
        	Fourmie laFourmie = null;
    		for(Fourmie f:listeNosFourmies)
    		{
    			if (f.position.equals(route.getStart()))
    			{
    				laFourmie = f;
    			}
    		}
        	
    		laFourmie.doMoveLocation(/*route.getStart(),*/ route.getEnd());
        }

        
        
        
        
     // explore unseen areas
        Log("exploration");
        for (Tile antLoc : sortedAnts) {
            if (!orders.containsValue(antLoc)) {
                List<Route> unseenRoutes = new ArrayList<Route>();
                for (Tile unseenLoc : unseenTiles) {
                    int distance = ants.getDistance(antLoc, unseenLoc);
                    Route route = new Route(antLoc, unseenLoc, distance);
                    unseenRoutes.add(route);
                }
                Collections.sort(unseenRoutes);
                int i =0;
                for (Route route : unseenRoutes) {
                	Fourmie laFourmie = null;
                	
                	
                	
                	
                	for(Fourmie f:listeNosFourmies)
            		{
            			if (f.position.equals(route.getStart()))
            			{
            				laFourmie = f;
            			}
            		}
                	i++;
        			Log("route" + i);
                    if (laFourmie.doMoveLocation(/*route.getStart(),*/ route.getEnd()) || i>1) {
                        break;
                    }
                }
            }
        }


        
        
        
     // unblock hills ( envoie les fourmis qui bloque le nid ailleur)
        Log("debloquer nid");
        for (Tile myHill : ants.getMyHills()) {
        	
        	
        	
            if (ants.getMyAnts().contains(myHill) && !orders.containsValue(myHill)) {
            	
            	Fourmie laFourmie = null;
            	for(Fourmie f:listeNosFourmies)
        		{
        			if (f.position.equals(myHill))
        			{
        				laFourmie = f;
        			}
        		}
            	
                for (Aim direction : Aim.values()) {

                    if (laFourmie.doMoveDirection(/*myHill,*/ direction)) {
                        break;
                    }
                }
            }
        }

        
    	}
    	catch(Exception e)
    	
    	{
    		Log("Erreur tour " + nbDeTour + " : " + e.getMessage()); 
    		Log(" -> " + e.getStackTrace().toString());
    		

    	}
        
        
        
    }
    
    
    
}
