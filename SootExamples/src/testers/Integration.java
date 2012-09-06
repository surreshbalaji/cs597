package testers;

public class Integration {
public static void main(String args[])
{
	int a,b,c,d,e,f;
	A a1 =new A();
	B b1=new B();
	a=a1.x;
	b=b1.y+5;
	d=a1.x+25;
	c=b+10;
	e=b1.y+20;
	f=a1.x+78;
	a1.setSumOp(b1.getFlag());
	a1.DoOp(3, 2);
	
	/*int x,y,z;
	x=10;
	y=5;
	
	z=2;*/
}
}
