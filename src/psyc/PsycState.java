package psyc;

import java.lang.Math;

/**
 * @author arpan
 *
 */
public class PsycState {
	
	

	/**
	 * Limits for the descriptors
	 */
	private static final double T_Max = 80, T_Min = -15, B_Max = 1000, B_Min = 10, En_Max = 120, En_Min = 10;
	// TODO: Error / Constancy check to be implemented. Discuss with Sastry sir for low temperature limits.
	double Td;
	double Tw;
	double B;
	// TODO:: Add Vapor pressure as state descriptor. 
	
	public PsycState(){
		// Empty constructor
		this.Td = 0;
		this.Tw = 0;
		this.B = 0;
	}
	
	/**
	 * @param Td Dry Bulb Temperature in C
	 * @param Tw Wet Bulb Temp in C
	 */
	public PsycState(double Td, double Tw){//inputs in degree C
		this.Td = Td;
		this.Tw = Tw;
		this.B = 101.325;
	}
	
	/**
	 * @param Td Dry Bulb Temperature in C
	 * @param Tw Wet Bulb Temparature in C
	 * @param B Barometric Pressure in kPa
	 */
	public PsycState(double Td, double Tw, double B){ // inputs in degree C and kPa
		this.Td = Td;
		this.Tw = Tw;
		this.B = B;
	}
	
	/**
	 * @author arpan
	 *
	 */

	/*----------Constructors for empty class----------*/
	
	
	public void Psyc_Td_Tw_B(double Td, double Tw, double B){ // inputs in degree C and kPa
		this.Td = Td;
		this.Tw = Tw;
		this.B = B;
	}
	
	
	/**
	 * @param RH humidity in percentage scale
	 * @param Td Dry Bulb Temperature
	 * @param B Barometric Pressure
	 */
	public void Psyc_RH_Td_B(double RH, double Td,double B){ // Unit kPa , deg C, in % 
		//TODO: discuss with sir about Humidity
		double Pw = this.SatVapPressure(Td) * RH/100;
		this.Psyc_Pw_Td_B(Pw, Td, B);
	}
	
	public void Psyc_Pw_Tw_B(double Pw, double Tw, double B){
		this.B = B;
		double Pws = this.SatVapPressure(Tw);
		this.Td = (Pws - Pw)/(0.000644*this.B) + Tw;
		this.Tw = Tw;
	}
	
	/**
	 * @param Pw
	 * @param Td
	 * @param B
	 */
	public void Psyc_Pw_Td_B(double Pw, double Td, double B){ // Unit kPa , deg C, in % 
		this.Td = Td;
		this.B = B;
		int iter=0;
		double Tw = Td, move=0, k, lmd = 10;
		while(true){
		    iter = iter+1; // for later advance uses
		    
		    k = (0.6105*Math.exp(17.27 * Tw / (237.3+ Tw)) - 0.000644*B*(Td - Tw) - Pw);
		    if (Math.abs(k)<0.001) break;
		    
		    if(Tw>T_Max || Tw < T_Min){
		    	// TODO: Raise an Error / Exception
		        Tw = Double.NaN;
		        break;
		    }
		    
		    if(k<0){
		        if(move<0) lmd = lmd/2;
		        move = lmd;
		    }else{
		        if(move>0) lmd = lmd/2;
		        move = -lmd;
		    }		    
		    Tw = Tw + move;
		}
		this.Tw = Tw;
	}
	
	public void SatPsycEnth(double targetE){
		SatPsycEnth(targetE,25); // Default initial seed of 25 degree C
	}
	
	public void SatPsycEnth(double targetE, double T){
		
		this.Td = this.Tw = T;
		double lmd = 5, move =0;
		int iter = 0;	
			while(true){
				iter = iter+1;
				double k = targetE-this.EnthalpyPerKgDa();
				if(Math.abs(k)<0.001) break;
				if(k>0){
			        if(move<0) lmd = lmd/2;
			        move = lmd;
			    }else{
			        if(move>0) lmd = lmd/2;
			        move = -lmd;
			    }
				this.Tw = this.Td = this.Td + move;
			}
		
		
	}
	
	/*Methods of Units Conversions*/
	
	public double kPa_to_mmHg(double P_in_kPa){
		return P_in_kPa * 7.50062;
	}
	
	public double mmHg_to_kPa (double P_in_mmHg){
		return P_in_mmHg/7.50062;
	}
	
	// TODO: Conversion for Temperature
	
	
	
	
	
	// Get data Methods
	
	/**
	 * @return The Vapor Pressure of Psychrometric state
	 */
	
	public double DryBulbTemp(){
		return this.Td;
	}
	
	public double WetBulbTemp(){
		return this.Tw;
	}
	
	public double BaroPressure(){
		return this.B;
	}
	
	public double BaroPressure_mmHg(){
		return this.B*7.50062;
	}
	
	
	public double VapPressure(){
		// Gives Partial Vapor Pressure (kPa) Tdb, Twb (*C)Barpmetric_Pressure (kPa)
		return( this.SatVapPressure() - 0.000644*this.B*(Td - Tw));

	}
	
	public double VapPressure_mmHg(){
		return 7.50062*this.VapPressure();
	}
	/**
	 * @return Saturated Vapor Pressure 
	 */
	public double SatVapPressure(){ // Saturated Vapor pressure
		return(0.6105*Math.exp(17.27 * Tw / (237.3+ Tw)));
	}
	//Overloading
	/**
	 * @param T Temperature in C 
	 * @return Saturated Vapor Pressure for Temperature T
	 */
	public double SatVapPressure(double T){ // Saturated Vapor pressure for temperature in call by value
		return(0.6105*Math.exp(17.27 * T / (237.3+ T)));
	}
	
	public double MoistContPerKgDa(){ // kg of vapor per kg of da
		return (0.622*this.VapPressure())/(B - this.VapPressure());
	}
	
	public double SpecificVol(){
		return (0.287 * (273.15 + Td))/ (B - this.VapPressure());
	}
	
	public double EnthalpyPerKgDa(){ // Enthalpy in kJ / kg of da
		return(1.005*Td + this.MoistContPerKgDa()*(1.883*Td+2500));
	}
	
	public double SigmaHeatPerKgDa(){
		return(this.EnthalpyPerKgDa() - 4.18*this.MoistContPerKgDa()*Tw);
	}
	
	public double Humidity(){
		return(100 * this.VapPressure()/this.SatVapPressure(Td));
	}
	
	public double DuePoint(){
		return((Math.log(this.VapPressure()/0.6105)*237.3)/(17.27-Math.log(this.VapPressure()/0.6105)));
	}
	
	public double Density(){
		return((1+this.MoistContPerKgDa())/this.SpecificVol());
	}
	
	// Methods involving state change
	
	public void DryHeatPerKgDa(double heat){ // kJ heat per kg of da
		double Pw = this.VapPressure();
		if (this.EnthalpyPerKgDa()+heat<En_Min || this.EnthalpyPerKgDa()+heat>En_Max){
			//TODO: raise error
		}
		//TODO: Discuss with sir that what all things are constant on dry heat addition. also a inverse equation. 
		double Td = this.Td + (heat/(1.005 + this.MoistContPerKgDa()*1.883));
		if (Td< this.DuePoint()){
			//TODO: Raise a Notification.
			double targetE = this.EnthalpyPerKgDa()+heat;
			double lmd = 5, move =0;
			this.Td = this.DuePoint();
			this.Tw = this.Td;
			while(true){
				double k = targetE-this.EnthalpyPerKgDa();
				if(Math.abs(k)<0.01) break;
				if(k>0){
			        if(move<0) lmd = lmd/2;
			        move = lmd;
			    }else{
			        if(move>0) lmd = lmd/2;
			        move = -lmd;
			    }
				this.Tw = this.Td = this.Td + move;
			}
			
			
		}else		
		this.Psyc_Pw_Td_B(Pw, Td, B);
	}
	
	
	public double DryHeatPerKgDaTill_Td(double Td){ // kJ heat per kg of da
		double Pw = this.VapPressure();
		double Enth = this.EnthalpyPerKgDa(); // Initial Enthalpy of System
		
		//TODO: Discuss with sir that what all things are constant on dry heat addition. 
		//this.Td = this.Td + (heat/(1.005 + this.MoistContPerKgDa()*1.883));
		if (Td<=this.DuePoint()){
			this.Td = Td;
			this.Tw = Td;
		}else{
			this.Psyc_Pw_Td_B(Pw, Td, B);
		}
		
		return(this.EnthalpyPerKgDa() - Enth);
		
	}
	
	public double DryHeatPerKgDaTill_Tw(double Tw){ // kJ heat per kg of da
		double Pw = this.VapPressure();
		double Enth = this.EnthalpyPerKgDa(); // Initial Enthalpy of System
		
		//TODO: Discuss with sir that what all things are constant on dry heat addition. 
		//this.Td = this.Td + (heat/(1.005 + this.MoistContPerKgDa()*1.883));
		if (Tw<=this.DuePoint()){
			this.Td = Tw;
			this.Tw = Tw;
		}else{
			this.Psyc_Pw_Tw_B(Pw, Tw, B);
		}
		
		return(this.EnthalpyPerKgDa() - Enth);
		
	}
	
	// methods for testing
	
	public void Print(){
		System.out.printf("\nDry Bulb = %f, Wet Bulb = %f \nBarometric Pressure = %f\n",Td,Tw,B);
		System.out.printf("\nVapor Pressure = %f", this.VapPressure());
		System.out.printf("\nSaturated Vapor Pressure = %f", this.SatVapPressure());
		System.out.printf("\nHumity = %f percent", this.Humidity());
		System.out.printf("\nSpecific Volume = %f", this.SpecificVol());
		System.out.printf("\nDensity = %f", this.Density());
		System.out.printf("\nEnthalpy = %f", this.EnthalpyPerKgDa());
		System.out.printf("\nSigma Heat = %f", this.SigmaHeatPerKgDa());
		System.out.printf("\nMoist Content = %f", this.MoistContPerKgDa());
		System.out.printf("\nDue Point = %f", this.DuePoint());
	}

}