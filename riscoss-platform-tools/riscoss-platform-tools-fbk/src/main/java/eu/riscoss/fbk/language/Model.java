package eu.riscoss.fbk.language;


import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.riscoss.fbk.util.ArrayMap;

public class Model
{
	ArrayMap<String, Proposition>				index		= new ArrayMap<String, Proposition>();
	Map<String, ArrayList<Proposition>>			catalog		= new HashMap<String, ArrayList<Proposition>>();
	
	HashMap<String, HashMap<String, Relation>>	relations	= new HashMap<String, HashMap<String, Relation>>();
	
	
	
	public <T extends Proposition> T addProposition( T p )
	{
		if( index.get( p.getId() ) != null )
		{
			try
			{
				throw new Exception( "Duplicate situation '" + p.getId() + "'" );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			
			return p;
		}
		
		index.put( p.getId(), p );
		
		ArrayList<Proposition> list = catalog.get( p.getStereotype() );
		
		if( list == null )
		{
			list = new ArrayList<Proposition>();
			catalog.put( p.getStereotype(), list );
		}
		
		list.add( p );
		
		return p;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Proposition> T getProposition( String id )
	{
		try
		{
			return (T) index.get( id );
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	public void addRelation( Proposition[] sources, Proposition target,
			Class<? extends Relation> cls )
	{
		try
		{
			Relation sat = (Relation) Class.forName( cls.getName() ).newInstance();
			sat.setTarget( target );
			
			for( Proposition source : sources )
				sat.addSource( source );
			
			addRelation( sat );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addRelation( Relation r )
	{
		HashMap<String, Relation> rels = relations.get( r.getStereotype() );
		
		if( rels == null )
		{
			rels = new HashMap<String, Relation>();
			relations.put( r.getStereotype(), rels );
		}
		
		try
		{
			if( rels.get( r.toString() ) != null ) return;
			
			rels.put( r.toString(), r );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	static class PropositionIterator<T extends Proposition> implements Iterator<T>, Iterable<T>
	{
		ArrayList<Proposition>	list;
		Iterator<Proposition>	it;
		
		PropositionIterator( ArrayList<Proposition> list )
		{
			this.list = list;
			this.it = list.iterator();
		}
		
		@Override
		public boolean hasNext()
		{
			return it.hasNext();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T next()
		{
			Proposition p = it.next();
			
			return (T) p;
		}
		
		@Override
		public void remove()
		{
			
		}
		
		@Override
		public Iterator<T> iterator()
		{
			return this;
		}
		
	}
	
	public ArrayList<Proposition> list( Class<? extends Proposition> cls )
	{
		return catalog.get( cls.getName() );
	}
	
	public Iterable<Relation> relations()
	{
		class RelationIterator implements Iterable<Relation>, Iterator<Relation>
		{
			Iterator<String>	typeIt;
			Iterator<Relation>	relIt	= null;
			
			public RelationIterator( HashMap<String, HashMap<String, Relation>> relations )
			{
				typeIt = relations.keySet().iterator();
			}
			
			@Override
			public Iterator<Relation> iterator()
			{
				return this;
			}
			
			@Override
			public boolean hasNext()
			{
				// First iteration
				if( relIt == null )
				{
					relIt = nextIt();
					
					if( relIt == null )
						return false;
				}
				
				if( relIt.hasNext() == true )
					return true;
				
				if( typeIt.hasNext() == false )
					return false;
				
				relIt = nextIt();
				
				return relIt.hasNext();
			}
			
			@Override
			public Relation next()
			{
				return relIt.next();
			}
			
			Iterator<Relation> nextIt()
			{
				if( typeIt.hasNext() == false )
					return null;
				
				String type = typeIt.next();
				
				HashMap<String, Relation> rels = relations.get( type );
				
				if( rels == null )
					return null;
				
				return rels.values().iterator();
			}
			
			@Override
			public void remove()
			{
			}
			
		}
		
		return new RelationIterator( relations );
	}
	
	public void print( PrintStream out )
	{
		for( String type : propositionTypes() )
		{
			for( Proposition p : propositions() )
			{
				out.println( type + "( " + p.getId() + " )" );
			}
		}
		
		for( String type : relationTypes() )
		{
			for( Relation r : relations( type ) )
			{
				out.println( type + "( " + r.target + ", " + r.sources + " ) " );
			}
		}
	}
	
	public int getPropositionCount( Class<? extends Proposition> cls )
	{
		List<Proposition> list = catalog.get( cls.getName() );
		
		if( list == null )
			return 0;
		
		if( list.size() > 0 )
			return list.size();
		
		return 0;
	}
	
	public Proposition getProposition( Class<? extends Proposition> cls, int idx )
	{
		List<Proposition> list = catalog.get( cls.getName() );
		
		if( list == null )
			return null;
		
		if( idx >= list.size() )
			return null;
		
		return list.get( idx );
	}
	
	public Proposition getProposition( int idx )
	{
		return index.get( idx );
	}
	
	public int getPropositionCount()
	{
		return index.size();
	}
	
	public Iterable<Proposition> propositions()
	{
		return this.index;
	}
	
	public Iterable<String> relationTypes()
	{
		return relations.keySet();
	}
	
	public Iterable<String> propositionTypes()
	{
		return catalog.keySet();
	}
	
	private final ArrayList<Relation> EMPTY_RELATION_LIST = new ArrayList<Relation>();
	
	public Iterable<Relation> relations( String type )
	{
		HashMap<String,Relation> rels = relations.get( type );
		
		if( rels == null ) return EMPTY_RELATION_LIST;
		
		return rels.values();
	}
	
	@SuppressWarnings({ "unchecked" })
	private <T extends Proposition> T clone( T o, String prefix ) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		T newProp = null;
		try
		{
			newProp = (T)o.clone();
			
			newProp.id = prefix + o.id;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		
		if( newProp == null )
		{
			Constructor<?> ctor = o.getClass().getConstructor( String.class );
			
			newProp = (T) ctor.newInstance( prefix + o.getId() );
		}
		
		
		return newProp;
	}
	
	public Model clone( String prefix )
	{
		Model outModel = new Model();
		
		for( Proposition p : propositions() )
		{
			try
			{
				outModel.addProposition( clone( p, prefix ) );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		
		for( Relation r : relations() )
		{
			try
			{
				Relation newr = null;
				
				try
				{
					newr = r.clone();
				}
				catch( Exception ex ) {}
				
				if( newr == null )
					newr = r.getClass().newInstance();
				
				ArrayList<Proposition> sources = new ArrayList<Proposition>();
				
				for( Proposition p : r.getSources() )
				{
					Proposition src = outModel.getProposition( prefix + p.getId() );
					
					sources.add( src );
				}
				
				newr.setSources( sources );
				
				Proposition trg = outModel.getProposition( prefix + r.getTarget().getId() );
				
				newr.setTarget( trg );
				
				outModel.addRelation( newr );
			}
			catch( InstantiationException e )
			{
				e.printStackTrace();
			}
			catch( IllegalAccessException e )
			{
				e.printStackTrace();
			}
		}
		
		return outModel;
	}

	public int countRelations()
	{
		int ret = 0;
		
		for( String rname : relations.keySet() )
		{
			HashMap<String,Relation> map = relations.get( rname );
			ret += map.size();
		}
		
		return ret;
	}

	public Iterable<Proposition> propositions( String classname )
	{
		final List<Proposition> EMPTY_LIST = new ArrayList<Proposition>();
		
		Iterable<Proposition> ret = catalog.get( classname );
		
		if( ret == null ) ret = EMPTY_LIST;
		
		return ret;
	}

	public void addRelation( String[] sources, String target, String stereotype )
	{
		Relation r = new Relation( stereotype );
		
		for( String s : sources )
			r.addSource( getProposition( s ) );
		
		r.setTarget( getProposition( target ) );
		
		addRelation( r );
	}
}
