package psycof;

import psyc.PsycState;

public class AirStream extends PsycState{
	
	double AirFlow; // Flow in Kg of Dry air in second

	public AirStream() {
		// TODO Auto-generated constructor stub
		this.AirFlow = 0;
	}
	
	public AirStream(double flow, double Td, double Tw, double B) {
		// TODO Auto-generated constructor stub
		super(Td,Tw,B);
		this.AirFlow = flow / super.SpecificVol();
	}
	
	public enum ConsType{
		Flow_RH_Td_B,
		Flow_Pw_Td_B,
		Flow_Td_Tw_B,
		FlowKgDa_RH_Td_B,
		FlowKgDa_Pw_Td_B,
		FlowKgDa_Td_Tw_B;
	}
	
	public AirStream(ConsType type,  double flow, double RH, double Td, double B) {
		// TODO Auto-generated constructor stub
		if(type == ConsType.Flow_RH_Td_B){
			super.Psyc_RH_Td_B(RH, Td, B);
			this.AirFlow = flow / super.SpecificVol();
		}if(type == ConsType.Flow_Td_Tw_B){
			super.Psyc_Td_Tw_B(RH, Td, B);
			this.AirFlow = flow / super.SpecificVol();
		}if(type == ConsType.Flow_Pw_Td_B){
			super.Psyc_Pw_Td_B(RH, Td, B);
			this.AirFlow = flow / super.SpecificVol();
		}if(type == ConsType.FlowKgDa_RH_Td_B){
			super.Psyc_RH_Td_B(RH, Td, B);
			this.AirFlow = flow;
		}if(type == ConsType.FlowKgDa_Td_Tw_B){
			super.Psyc_Td_Tw_B(RH, Td, B);
			this.AirFlow = flow;
		}if(type == ConsType.FlowKgDa_Pw_Td_B){
			super.Psyc_Pw_Td_B(RH, Td, B);
			this.AirFlow = flow;
		}
		
		
		
	}
	
	/*Methods for Unit Conversion*/
	
	//TODO: flow rate unit conversions
	
	/*Get Data about the Air Stream methods*/
	
	public double EnthalpyPerSec(){
		return super.EnthalpyPerKgDa()*this.AirFlow;
	}
	public double MoistPerSec(){
		return super.MoistContPerKgDa()*this.AirFlow;
	}
	public double FlowRateKgDa(){
		return this.AirFlow;
	}
	public double FlowRate(){
		return this.AirFlow*super.SpecificVol();
	}
	
	public static AirStream Mix(AirStream S1, AirStream S2){
		
		//TODO: if the pressure of two streams are too different then raise an error.
		double B = S1.BaroPressure();
		double flow = S1.AirFlow + S2.AirFlow;
		double Enth = (S1.EnthalpyPerKgDa()*S1.AirFlow + S2.EnthalpyPerKgDa()*S2.AirFlow);
		double Pw = (S1.VapPressure()*S1.AirFlow+S2.VapPressure()*S2.AirFlow)/flow;
		double Td = (S1.DryBulbTemp()*S1.AirFlow+S2.DryBulbTemp()*S2.AirFlow)/flow;
		
		
		//System.out.printf("Pw = %f, Td = %f, SatPw = %f\n\n", Pw, Td,S1.SatVapPressure(Td));
		if (S1.SatVapPressure(Td) >= Pw){ // Check for condensation.
			//System.out.println("I'm IN \n");
			return( new AirStream(ConsType.Flow_Pw_Td_B,flow,Pw,Td,B));
		}else{
			AirStream Mixture = new AirStream(flow,Td,Td,B);
			Mixture.SatPsycEnth(Enth/flow);
			return Mixture;
		}
				
	}
	
	public void AddDryHeatWatt(double watt){
		super.DryHeatPerKgDa(watt/this.AirFlow);
	}
	
	public double AddDryHeatWattTill_Td(double Td){
		return super.DryHeatPerKgDaTill_Td(Td);
	}
	
	public double AddDryHeatWattTill_Tw(double Tw){
		return super.DryHeatPerKgDaTill_Tw(Tw);
	}
	
	/*public void Print(){
		
		System.out.printf("\nFlow Rate (Kgda / s) = %f\n", this.AirFlow);
		super.Print();
	}*/
	/**
	 * 
	 * @param EffectiveCrossSection in m^2 of the particular area
	 * @return Dry Kata cooling power in Watt / m^2
	 */
	public double DryKataCoolingPower(double EffectiveCrossSection){ 
		double V = this.FlowRate()/EffectiveCrossSection; // Calculating velocity in m/s
		if(V>1){
			return (5.44+19.62*Math.sqrt(V))*(36.5-this.DryBulbTemp());
		}else{
			return (8.37+16.74*Math.sqrt(V))*(36.5-this.DryBulbTemp());
		}
		// Source: Misra, G.B., ‘Mine Environment and ventilation’,  OUP, 1986, 12th Impression (2006),	(p: 212-214)
	}
	/**
	 * 
	 * @param EffectiveCrossSection in m^2 of the particular area
	 * @return Wet Kata cooling power in Watt / m^2
	 */
	public double WetKataCoolingPower(double EffectiveCrossSection){ 
		double V = this.FlowRate()/EffectiveCrossSection; // Calculating velocity in m/s
		if(V>1){
			return (4.19+46.05* Math.cbrt(V))*(36.5-this.WetBulbTemp());
		}else{
			return (14.65+35.59 * Math.cbrt(V))*(36.5-this.WetBulbTemp());
		}
		// Source: Misra, G.B., ‘Mine Environment and ventilation’,  OUP, 1986, 12th Impression (2006),	(p: 212-214)
	}

}
