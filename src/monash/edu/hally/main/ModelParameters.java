package monash.edu.hally.main;

public class ModelParameters {
	
	
	private int iterations;	//��������
	private int burn_in;	//burn-in ʱ��
	private int saveStep;	//burn-in ����ÿsaveStep�ε�������һ�ν��

	private int K; //������Ŀ
	private int topNum;	//	��ʾ�����¸�����ߵ�ǰtopNum����
	
	private int samplingEquation=1;	//ȡ����ʽ���� 1,2
	
	
	public int getSamplingEquation() {
		return samplingEquation;
	}
	public void setSamplingEquation(int samplingEquation) {
		this.samplingEquation = samplingEquation;
	}
	public int getIterations() {
		return iterations;
	}
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	public int getBurn_in() {
		return burn_in;
	}
	public void setBurn_in(int burn_in) {
		this.burn_in = burn_in;
	}
	public int getSaveStep() {
		return saveStep;
	}
	public void setSaveStep(int saveStep) {
		this.saveStep = saveStep;
	}
	public int getK() {
		return K;
	}
	public void setK(int k) {
		K = k;
	}
	public int getTopNum() {
		return topNum;
	}
	public void setTopNum(int topNum) {
		this.topNum = topNum;
	}
	
	
}
