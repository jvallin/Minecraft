package balckangel.Points;

import java.io.Serializable;
import java.util.UUID;

public class Joueur implements Comparable<Joueur>, Serializable
{
	private static final long serialVersionUID = 1L;
	UUID uuid;
	String nom;
	int points;
	int pointsMission;
	int victoires;
	
	public Joueur(UUID uuid, String nom, int points, int pointsMission, int victoires)
	{
		super();
		this.uuid = uuid;
		this.nom = nom;
		setPoints(points);
		setPoints(pointsMission);
		setVictoires(victoires);
	}

	public UUID getUUID()
	{
		return uuid;
	}
	
	public String getNom()
	{
		return nom;
	}

	public int getPoints()
	{
		return points;
	}
	
	public int getPointsMission()
	{
		return pointsMission;
	}

	public int getVictoires()
	{
		return victoires;
	}
	
	public void setPoints(int points)
	{
		this.points = points;
	}
	
	public void setPointsMission(int pointsMission)
	{
		this.pointsMission = pointsMission;
	}

	public void setVictoires(int victoires)
	{
		this.victoires = victoires;
	}
	
	public void addPoints(int nb)
	{
		points += nb;
	}
	
	public void addPointsMission(int nb)
	{
		pointsMission += nb;
	}
	
	public void addVictoires(int nb)
	{
		victoires += nb;
	}
	
	public void clearPoints()
	{
		points = 0;
	}
	
	public void clearPointsMission()
	{
		pointsMission = 0;
	}
	
	public void clearVictoires()
	{
		victoires = 0;
	}
	
	public void clearAll()
	{
		clearPoints();
		clearPointsMission();
		clearVictoires();
	}
	
	public boolean equals(Joueur other)
	{
		return other.getUUID().equals(uuid);	  
	}
	
	public int compareTo(Joueur other)
	{ 
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
		return (getNom() + " : " + getPoints() + " point(s), " + getPointsMission() + " point(s) mission et " + getVictoires() + " victoire(s).");
	} 
	
	public String toStringLog()
	{
		return ("Vous avez : " + getPoints() + " point(s), " + getPointsMission() + " point(s) mission et " + getVictoires() + " victoire(s).");
	}
	
}
