import java.io.IOException;
import java.util.*;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot
{
	/**
	 * Main method executed by the game engine for starting the bot.
	 * 
	 * @param args
	 *            command line arguments
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException
	{
		new MyBot().readSystemInput();
	}

	// Stocke les différents mouvements des Ants
	// Key = La nouvelle position où va bouger l'Ant
	// Value = La position de l'Ant
	private Map<Tile, Tile> orders = new HashMap<Tile, Tile>();

	// Toutes les cases que l'on a pas vu durant la partie
	private Set<Tile> unseenTiles;

	// Stocke en mémoire les hills ennemis
	private Set<Tile> enemyHills = new HashSet<Tile>();
	
	private boolean doMoveDirection(Tile antLoc, Aim direction)
	{
		Ants ants = getAnts();
		// Track all moves, prevent collisions
		Tile newLoc = ants.getTile(antLoc, direction);
		if (ants.getIlk(newLoc).isUnoccupied() && !orders.containsKey(newLoc))
		{
			ants.issueOrder(antLoc, direction);
			orders.put(newLoc, antLoc);
			return true;
		} else
		{
			return false;
		}
	}

	private boolean doMoveLocation(Tile antLoc, Tile destLoc)
	{
		Ants ants = getAnts();
		// Track targets to prevent 2 ants to the same location
		List<Aim> directions = ants.getDirections(antLoc, destLoc);
		for (Aim direction : directions)
		{
			if (doMoveDirection(antLoc, direction))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void doTurn()
	{
		Init();
		
		Ants ants = getAnts();
		orders.clear();
		Map<Tile, Tile> foodTargets = new HashMap<Tile, Tile>();

		// add all locations to unseen tiles set, run once
		if (unseenTiles == null)
		{
			unseenTiles = new HashSet<Tile>();
			for (int row = 0; row < ants.getRows(); row++)
			{
				for (int col = 0; col < ants.getCols(); col++)
				{
					unseenTiles.add(new Tile(row, col));
				}
			}
		}
		// remove any tiles that can be seen, run each turn
		for (Iterator<Tile> locIter = unseenTiles.iterator(); locIter.hasNext();)
		{
			Tile next = locIter.next();
			if (ants.isVisible(next))
			{
				locIter.remove();
			}
		}

		// prevent stepping on own hill
		for (Tile myHill : ants.getMyHills())
		{
			orders.put(myHill, null);
		}

		// unblock hills
		for (Tile myHill : ants.getMyHills())
		{
			if (ants.getMyAnts().contains(myHill) && !orders.containsValue(myHill))
			{
				for (Aim direction : Aim.values())
				{
					if (doMoveDirection(myHill, direction))
					{
						break;
					}
				}
			}
		}

		TreeSet<Tile> sortedAnts = new TreeSet<Tile>(ants.getMyAnts());
		
		// explore unseen areas
		for (Tile antLoc : sortedAnts)
		{
			if (!orders.containsValue(antLoc))
			{
				List<Route> unseenRoutes = new ArrayList<Route>();
				for (Tile unseenLoc : unseenTiles)
				{
					int distance = ants.getDistance(antLoc, unseenLoc);
					Route route = new Route(antLoc, unseenLoc, distance);
					unseenRoutes.add(route);
				}
				Collections.sort(unseenRoutes);
				for (Route route : unseenRoutes)
				{
					if (doMoveLocation(route.getStart(), route.getEnd()))
					{
						break;
					}
				}
			}
		}

		// find close food
		List<Route> foodRoutes = new ArrayList<Route>();
		TreeSet<Tile> sortedFood = new TreeSet<Tile>(ants.getFoodTiles());
		for (Tile foodLoc : sortedFood)
		{
			for (Tile antLoc : sortedAnts)
			{
				int distance = ants.getDistance(antLoc, foodLoc);
				Route route = new Route(antLoc, foodLoc, distance);
				foodRoutes.add(route);
			}
		}
		Collections.sort(foodRoutes);
		for (Route route : foodRoutes)
		{
			if (!foodTargets.containsKey(route.getEnd()) && !foodTargets.containsValue(route.getStart())
					&& doMoveLocation(route.getStart(), route.getEnd()))
			{
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
	}
	
	void Init()
	{
		
	}

}
