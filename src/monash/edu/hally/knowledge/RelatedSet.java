package monash.edu.hally.knowledge;

import java.util.ArrayList;

public class RelatedSet {
	
	private ArrayList<String> knowledgeList=new ArrayList<String>();
	
	public RelatedSet(){}
	
	public RelatedSet(String entity,String categories[])
	{
		knowledgeList.add(entity);
		for (String category : categories) {
			knowledgeList.add(category);
		}
	}
	
	public String getItemFromIndex(int index)
	{
		return knowledgeList.get(index);
	}

	public int getSize()
	{
		return knowledgeList.size();
	}
	
	public String toString()
	{
		return knowledgeList.toString();
		
	}
	
	
	public static void main(String[] args) {
	

	}

}
