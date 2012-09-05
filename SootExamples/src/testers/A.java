package testers;




public class A {
	int result;
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
	}

	public int doubleit(int a) {
		return a * 2;
	}
}
