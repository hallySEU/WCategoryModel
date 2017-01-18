package monash.edu.hally.knowledge;

import java.util.ArrayList;

public class RelatedLinks {
	
	private ArrayList<String> linksList=new ArrayList<String>();
	
	public int size()
	{
		return linksList.size();
	}

	public String get(int index)
	{
		return linksList.get(index);
	}
	
	public void add(String relatedLink)
	{
		linksList.add(relatedLink);
	}
	
	public boolean isContain(String relatedLink)
	{
		if(linksList.contains(relatedLink))
			return true;
		return false;
	}
	
	public String toString()
	{	
		String string="";
		for (String link : linksList) {
			string += link+"\t";
		}	
		return string;
		
	}
	
	
	public static void main(String[] args) {
	

	}

}
