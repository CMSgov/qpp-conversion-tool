package gov.cms.qpp.conversion.model.validation;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

	private void initWith(SubPopulation subPop, String value)
			throws InvocationTargetException, IllegalAccessException {
		for (Method mutator : mutators) {
			mutator.invoke(subPop, value);
		}
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(SubPopulation.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

}