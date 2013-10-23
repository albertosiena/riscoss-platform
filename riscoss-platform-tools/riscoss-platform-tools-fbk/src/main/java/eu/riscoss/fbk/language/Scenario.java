package eu.riscoss.fbk.language;

import java.util.ArrayList;
import java.util.HashMap;

public class Scenario
{
	public static class PreferencePair
	{
		public String	mostPreferred;
		public String	lessPreferred;
	}
	
	public static class Pair
	{
		public String	label;
		public String	value;
		
		public Pair( String k, String v )
		{
			label = k;
			value = v;
		}
		
		public String toString()
		{
			return "[" + label + "=" + value + "]";
		}
	}
	
	HashMap<String, ArrayList<Pair>>	constraints	= new HashMap<String, ArrayList<Pair>>();
	HashMap<String,String>				values = new HashMap<String,String>();
	Model								model;
	
	CostStructure						costs		= new CostStructure();
	
	ArrayList<PreferencePair>			preferences	= new ArrayList<PreferencePair>();
	
	static final ArrayList<Pair>		emptylist	= new ArrayList<Pair>();
	
	public Scenario( Model model )
	{
		this.model = model;
	}
	
	public void addConstraint( String id, String label )
	{
		ArrayList<Pair> list = constraints.get( id );
		if( list == null )
		{
			list = new ArrayList<Pair>();
			constraints.put( id, list );
		}
		list.add( new Pair( label, null ) );
	}
	
	public void addConstraint( String id, String label, String value )
	{
		ArrayList<Pair> list = constraints.get( id );
		if( list == null )
		{
			list = new ArrayList<Pair>();
			constraints.put( id, list );
		}
		list.add( new Pair( label, value ) );
	}
	
	public void removeConstraints()
	{
		for( String key : constraints.keySet() )
		{
			constraints.get( key ).clear();
		}
		constraints.clear();
	}
	
	public ArrayList<Pair> constraintsOf( String id )
	{
		ArrayList<Pair> ret = constraints.get( id );
		
		if( ret == null )
		{
			ret = emptylist;
		}
		
		return ret;
	}
	
	public CostStructure getCostStructure()
	{
		return this.costs;
	}
	
	public void addPreference( String mostPreferred, String lessPreferred )
	{
		PreferencePair p = new PreferencePair();
		
		p.mostPreferred = mostPreferred;
		p.lessPreferred = lessPreferred;
		
		preferences.add( p );
	}
	
	public Iterable<PreferencePair> preferences()
	{
		return preferences;
	}

	public Iterable<String> keys()
	{
		return constraints.keySet();
	}
}
