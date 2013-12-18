package balckangel.PvP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Equipe implements Serializable
{
	public List<String> joueurs;
	int points;
	
	public Equipe()
	{
		joueurs = new ArrayList<String>();
		points = 0;
	}
	
	public void ajouter(String nom)
	{
		joueurs.add(nom);
	}
	
	public void supprimer(String nom)
	{
		joueurs.remove(nom);
	}
	
	public List<String> getEquipe()
	{
		return joueurs;
	}
	
	public int getPoints()
	{
		return points;
	}
	
	public void setEquipe(List<String> liste)
	{
		joueurs = liste;
	}
	
	public void setPoints(int pts)
	{
		points = pts;
	}
	
	public boolean Contient(String nom)
	{
		return joueurs.contains(nom);
	}
	
	public int getSize()
	{
		return joueurs.size();
	}
	
	public void plus(int pts)
	{
		points += pts;
	}
	
	public void Vider()
	{
		joueurs.clear();
		points = 0;
	}
	
	public void moins(int pts)
	{
		points -= pts;
		if (points < 0)
		{
			points = 0;
		}
	}
	
	public String toString()
	{
		if(joueurs.isEmpty())
		{
			return "Equipe vide";
		}
		
		String texte = "Equipe composee de ";
		
		for(int i=0; i<joueurs.size(); i++)
		{
			texte += joueurs.get(i);
			if(i == joueurs.size()-1)
			{
				texte += ". Son nombre de point est " + points +".";
			}
			else if(i == joueurs.size()-2)
			{
				texte += " et ";
			}
			else
			{
				texte += ", ";
			}
		}
		
		return texte;
	}
}
