package efakturaplus.models;

public class Party{
	public String name;
	public String streetName;
	public String cityName;
	public String postalZone;
	public String countryIdCode;
	
	@Override
	public String toString() {
		return name+", "+streetName;
	}

}
