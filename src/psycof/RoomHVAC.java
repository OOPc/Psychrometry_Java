package psycof;

import psyc.PsycState;

public class RoomHVAC extends PsycState{
	
	double RoomVolume; 
	
	
	public RoomHVAC() {
		// TODO Auto-generated constructor stub
		this.RoomVolume = 0;
	}
	
	public RoomHVAC(double vol, double Td, double Tw, double B) {
		// TODO Auto-generated constructor stub]
		super(Td,Tw,B);
		this.RoomVolume = vol;
	}
	
	public enum ConsType{
		Vol_RH_Td_B,
		Vol_Td_Tw_B;
	}
	
	public RoomHVAC(ConsType type,  double vol, double RH, double Td, double B) {
		// TODO Auto-generated constructor stub]
		if(type == ConsType.Vol_RH_Td_B){
			super.Psyc_RH_Td_B(RH, Td, B);
			this.RoomVolume = vol;
		}if(type == ConsType.Vol_Td_Tw_B){
			super.Psyc_Td_Tw_B(RH, Td, B);
			this.RoomVolume = vol;
		}
		
	}
	
	public double RoomVolume(){
		return this.RoomVolume;
	}
	public double TotalAirMass(){
		return(super.Density()*this.RoomVolume);
	}
	
	public double TotalDryAirMass(){
		return(this.RoomVolume/super.SpecificVol());
	}
	
	public double TotalEnthalpy(){
		return(this.TotalDryAirMass()*super.EnthalpyPerKgDa());
	}
	
	public void DryHeatRoom(double heat){
		super.DryHeatPerKgDa(heat/this.TotalDryAirMass());
	}
	
	public double DryHeatRoomTill_Td(double Td){
		return(super.DryHeatPerKgDaTill_Td(Td)*this.TotalDryAirMass());
	}
	
	public double DryHeatRoomTill_Tw(double Tw){
		return(super.DryHeatPerKgDaTill_Tw(Tw)*this.TotalDryAirMass());
	}
	
	/*public double Humidity(){
		return(super.Humidity());
	}
	
	public double DuePoint(){
		return(super.DuePoint());
	}
	
	public double Density(){
		return(super.Density());
	}*/
		
}


