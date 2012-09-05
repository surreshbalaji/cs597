package testers;

public class Integration {
public static void main(String args[])
{
	boolean x,y,z;
	A a =new A();
	B b=new B();
	a.flag=true;
	x=a.flag;
	y=x;
	b.flag=x;
/*	a.setSumOp(b.getFlag());
	a.DoOp(3, 2);*/
	
	/*int x,y,z;
	x=10;
	y=5;
	
	z=2;*/
}
}
