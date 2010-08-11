package org.springframework.persistence.graph.neo4j;

import java.lang.reflect.Constructor;

import org.neo4j.graphdb.Node;
import org.springframework.persistence.support.EntityInstantiator;

import sun.reflect.ReflectionFactory;

/**
 * Uses Sun internal libraries used in deserializations to instantiate
 * objects bypassing constructor.
 * 
 * Code based on this http://www.javaspecialists.eu/archive/Issue175.html
 * TODO check license implications
 * 
 * @author rodjohnson
 *
 */
public class ConstructorBypassingGraphEntityInstantiator implements EntityInstantiator<NodeBacked,Node> {
	
	private static <T> T createWithoutConstructorInvocation(Class<T> clazz) {
	    return createWithoutConstructorInvocation(clazz, Object.class);
	  }

	@SuppressWarnings("unchecked")
	private static <T> T createWithoutConstructorInvocation(Class<T> clazz, Class<? super T> parent) {
		try {
			ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
			Constructor objDef = parent.getDeclaredConstructor();
			Constructor intConstr = rf.newConstructorForSerialization(clazz,
					objDef);
			return clazz.cast(intConstr.newInstance());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("Cannot create object", e);
		}
	}

	@Override
	public <T extends NodeBacked> T createEntityFromState(Node n, Class<T> c) {
		T t = createWithoutConstructorInvocation(c);
		t.setUnderlyingNode(n);
		return t; 
	}

}