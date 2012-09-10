package testers;

public class Integration {
public static void main(String args[])
{
	int a,b,c,d,e,f;
	A a1 =new A();
	B b1=new B();
	b1.flag=true;
	//test 1
	/*a=a1.x;
	b=b1.y+5;
	d=a1.x+25;
	c=b+10;
	d=b1.y+95;
	e=11+20;
	c=d+87;
	f=a1.x+78;*/
	
	//test 2
	a=a1.x;
	b=b1.y+5;
	d=a1.x+25;
	c=d+10;
	b1.y=c+29;
		
	a1.setSumOp(b1.flag,b1.getFlag(),1);
	a1.DoOp(b1.y, 2);
	
	/*int x,y,z;
	x=10;
	y=5;
	
	z=2;*/
}
}
