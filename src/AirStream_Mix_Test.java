
import psycof.AirStream;
public class AirStream_Mix_Test {

	public static void main(String[] args) {
		// TODO: Auto-generated method stub
		
		//PsycState Psmtry = new PsycState(25,21.5);
		//Psmtry.DryHeat(-50);
		//Psmtry.SatPsycEnth(30);
		//Psmtry.Print();
		
		AirStream Stream_1 = new AirStream(20,5,0,101.325); 
		// AirStream( Air flow-rate in m^3/s, Dry bulb temp, double Tw, double B)
		AirStream Stream_2 = new AirStream(10,30,30,101.325);
		AirStream Mixture_of_Streams = AirStream.Mix(Stream_1, Stream_2);
		
		Mixture_of_Streams.Print();
		
	}

}
