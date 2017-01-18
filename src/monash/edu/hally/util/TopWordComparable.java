package monash.edu.hally.util;

import java.util.Comparator;

/**
 * ���ã��������µĴʻ㣬�Ը��ʵķ�ʽ��������
 */
public class TopWordComparable implements Comparator<Integer> {
	
	private double phi[];

	public TopWordComparable(double phi[])
	{
		this.phi=phi;
	}

	@Override
	public int compare(Integer o1, Integer o2) {

		if(phi[o1]>phi[o2]) return -1;
		if(phi[o1]<phi[o2]) return 1;
		return 0;
	}
}
