package balckangel.Points;

public class Joueur implements Comparable<Joueur>
{
	String nom;
	int points;
	
	public Joueur(String nom, int points)
	{
		super();
		this.nom = nom;
		this.points = points;
	}

	public String getNom()
	{
		return nom;
	}

	public int getPoints()
	{
		return points;
	}
	
	public int compareTo(Joueur other) { 
		if (other.getPoints() > points)
		{
			return -1; 
		}
		else if(other.getPoints() == points)
		{
			return 0;
		}
		else
		{
			return 1;
		}
	} 
	
	public String toString()
	{
		return (getNom() + " : " + getPoints() + " point(s)");
	}
	
}
