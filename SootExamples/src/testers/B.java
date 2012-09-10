package testers;

public class B {

	boolean flag= false;
	int y=3;
	public int getFlag()
	{
		
		if(flag)
		{
			flag=false;
		}
		else
		{
			flag=true;
		}
		y++;
		int x=y+5;
		return x;
	}
}
