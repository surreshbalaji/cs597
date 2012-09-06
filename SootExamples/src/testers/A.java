package testers;




public class A {
	int result;
	int x=1;
	boolean flag = false;

	public void DoOp(int a, int b) {
		if (flag) {
			result = a + b;
		} else {
			result = a - b;

		}
	}

	public int getResult() {
		return result;
	}

	public void setSumOp(boolean f) {
		flag = f;
		int a,b,c;
		a=x+5;
		b=10;
		c=x+15;
	}

	public int doubleit(int a) {
		return a * 2;
	}
}
