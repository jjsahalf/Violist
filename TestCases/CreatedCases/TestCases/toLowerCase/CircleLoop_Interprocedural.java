package TestCases.toLowerCase;
import LoggerLib.Logger;
class CircleLoop_Interprocedural {
    public static String addA(String v)
	{
		return v.toLowerCase();
	}
    public static void main(String[] args) {
	String a="Aa";
	String b="Ab";
	String c="Ac";
	String e="Ae";
	for(int i=0;i<3;i++)
	{
		a=addA(b);
		b=addA(c);
		c=addA(a);
		Logger.reportString(c,"toLowerCase.CircleLoop_Interprocedural");
	}

    }
}