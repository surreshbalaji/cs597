package testers;

public class B {

	boolean flag= false;
	int x=2;
	public boolean getFlag()
	{
		int y=1;
		if(flag)
		{
			flag=false;
		}
		else
		{
			flag=true;
		}
		y++;
		return flag;
	}
}
