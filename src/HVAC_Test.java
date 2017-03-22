/**
 * 
 */

/**
 * @author arpan
 *
 */

import psycof.RoomHVAC;	

public class HVAC_Test {

	/**
	 * 
	 */
	public HVAC_Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RoomHVAC myROOM = new RoomHVAC(200/*Room Volume in m3*/,22/*Dry Bulb Temp*/,19/*wet bulb*/,101.325/*Atm Pressure*/);
		
		myROOM.Print();
		
		double Enthalpy_Needed = myROOM.DryHeatRoomTill_Tw(25);
		
		System.out.printf("\n\nEnthalpy Needed to reach 25 C Wet Bulb: %f \n", Enthalpy_Needed);
		
		System.out.printf("\nAfter the operation\n");
		myROOM.Print();

	}

}
