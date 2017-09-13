package gov.cms.qpp.conversion.model;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Context;

class ConstructorCache extends ClassValue<Function<Context, Object>> {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(ConstructorCache.class);

	@Override
	protected Function<Context, Object> computeValue(Class<?> clazz) {
		try {
			return createHandlerConstructor(clazz);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			DEV_LOG.warn("Unable to create constructor handle", e);
			return ignore -> null;
		}
	}


	private Function<Context, Object> createHandlerConstructor(Class<?> handlerClass)
			throws NoSuchMethodException, IllegalAccessException {
		try {
			Constructor<?> constructor = handlerClass.getConstructor(Context.class);
			MethodHandle handle = MethodHandles.lookup().unreflectConstructor(constructor)
					.asType(MethodType.methodType(Object.class, Context.class));

			return constructorContextArgument(handle);
		} catch (NoSuchMethodException thatsOk) {
			Constructor<?> constructor = getNoArgsConstructor(handlerClass);
			MethodHandle handle = MethodHandles.lookup().unreflectConstructor(constructor)
					.asType(MethodType.methodType(Object.class));

			return constructorNoArgs(handle);
		}
	}

	private Constructor<?> getNoArgsConstructor(Class<?> type) throws NoSuchMethodException {
		Constructor<?> constructor = getNoArgsConstructor(type.getConstructors());
		if (constructor == null) {
			constructor = getNoArgsConstructor(type.getDeclaredConstructors());

			if (constructor == null) {
				throw new NoSuchMethodException(type + " does not have a no-args constructor (public OR private)");
			}

		}

		constructor.setAccessible(true);
		return constructor;
	}

	private Constructor<?> getNoArgsConstructor(Constructor<?>[] constructors) {
		for (Constructor<?> constructor : constructors) {
			if (constructor.getParameterCount() == 0) {
				return constructor;
			}
		}

		return null;
	}

	private Function<Context, Object> constructorContextArgument(MethodHandle handle) {
		return passedContext -> {
			try {
				return handle.invokeExact(passedContext);
			} catch (Exception codeProblem) {
				DEV_LOG.warn("Unable to invoke constructor handle", codeProblem);
				return null;
			} catch (Throwable severeRuntimeError) {
				throw new SevereRuntimeException(severeRuntimeError);
			}
		};
	}

	private Function<Context, Object> constructorNoArgs(MethodHandle handle) {
		return ignore -> {
			try {
				return handle.invokeExact();
			} catch (Exception codeProblem) {
				DEV_LOG.warn("Unable to invoke no-args constructor handle", codeProblem);
				return null;
			} catch (Throwable severeRuntimeError) {
				throw new SevereRuntimeException(severeRuntimeError);
			}
		};
	}

}
