import java.util.LinkedList;
import java.util.List;

public class Ant
{

	public Tile position; // Position actuelle de la fourmie
	public boolean inDanger; // boolean inDanger
	public LinkedList<Tile> neighbourTiles; // Liste de tiles qui entourent la fourmie
	
	public LinkedList<Tile> closeEnemies; // Ennemis proches
	public Tile closestEnemy; // Ennemis le plus proche
	public LinkedList<Tile> closeAllies; // Alliés proches
	public Tile closestAlly; // Alliés le plus proche
	
	public Ant(Tile t)
	{
		this.position = t;
	}
	
	public String toString()
	{
		return "Ant : " + this.position;
	}
	
}