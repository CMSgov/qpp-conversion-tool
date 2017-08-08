package gov.cms.qpp.conversion.model.validation;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * SubPopulation Test class to increase JaCoCo Code Coverage
 */
public class SubPopulationTest {
	private static List<Method> mutators;

	@BeforeClass
	public static void setup() {
		mutators = Arrays.stream(SubPopulation.class.getDeclaredMethods())
				.filter(method -> method.getName().startsWith("set"))
				.filter(method ->
						Arrays.equals(method.getParameterTypes(), new Class<?>[] {String.class}))
				.collect(Collectors.toList());
	}

	@Test
	public void getStrata1() {
		SubPopulation sp = new SubPopulation();
		String strata1 = sp.getStrata1();
		assertNull(strata1);
	}

	@Test
	public void getStrata2() {
		SubPopulation sp = new SubPopulation();
		String strata2 = sp.getStrata2();
		assertNull(strata2);
	}

	@Test
	public void testEquals() throws InvocationTargetException, IllegalAccessException {
		SubPopulation sub = new SubPopulation();
		SubPopulation nullSub = new SubPopulation();
		SubPopulation emptyValues = new SubPopulation();
		initWith(emptyValues, "");

		for (Method mutator : mutators) {
			mutator.invoke(sub, "meep");

			SubPopulation empty = new SubPopulation();
			SubPopulation copy = new SubPopulation(sub);

			assertEquals("sub pop should equal detail", sub, sub);
			assertEquals("Copied sub pop should equal original", sub, copy);
			assertNotEquals("Empty sub pop should not equal initialized sub pop", empty, sub);
			assertNotEquals("Empty values sub pop should not equal initialized sub pop", emptyValues, sub);
			assertNotEquals("Null sub pop should not equal initialized sub pop", nullSub, sub);

			mutator.invoke(nullSub, "meep");
			mutator.invoke(emptyValues, "meep");
		}
	}

	private void initWith(SubPopulation subPop, String value)
			throws InvocationTargetException, IllegalAccessException {
		for (Method mutator : mutators) {
			mutator.invoke(subPop, value);
		}
	}

	@Test
	public void moreEqualsTesting() throws InvocationTargetException, IllegalAccessException {
		SubPopulation subPop = new SubPopulation();
		SubPopulation nullSubPop = new SubPopulation();
		initWith(subPop, "meep");
		initWith(nullSubPop, null);

		assertEquals("Null sub pop should equal Null sub pop",
				nullSubPop, new SubPopulation(nullSubPop));
		assertNotEquals("sub pop should not equal null", subPop, null);
		assertNotEquals("sub pop should not equal a non-SubPopulation class",
				subPop, "meep");
	}

	@Test
	public void testHashCode() throws InvocationTargetException, IllegalAccessException {
		SubPopulation subPop = new SubPopulation();
		SubPopulation nullSubPop = new SubPopulation();
		initWith(subPop, "meep");
		initWith(nullSubPop, null);

		assertNotEquals(subPop.hashCode(), nullSubPop.hashCode());
	}

}