package monash.edu.hally.knowledge;

import java.util.ArrayList;

public class RelatedSets {
	
	private ArrayList<RelatedSet> relatedList=new ArrayList<RelatedSet>();
	
	public boolean isContain(String entity)
	{
		for (RelatedSet relatedSet : relatedList) {
			if(relatedSet.getItemFromIndex(0).equals(entity)){
				return true;
			}
		}
		return false;
	}
	
	public void add(RelatedSet relatedSet)
	{
		relatedList.add(relatedSet);
	}
	
	public int getSize()
	{
		return relatedList.size();
	}
	
	public RelatedSet getRelatedSet(int index)
	{
		return relatedList.get(index);
	}
	
	public int getRulesSize()
	{
		int rules=0;
		for (RelatedSet relatedSet : relatedList) {
			rules += relatedSet.getSize()-1;
		}
		
		return rules;
		
	}
	
	public String toString()
	{	
		String string="";
		for (RelatedSet relatedSet : relatedList) {
			string += relatedSet.toString();
		}	
		return string;
		
	}
	
	public static void main(String[] args) {
		

	}

}
