package com.formation.container;

import java.lang.reflect.*;

import com.formation.interfaces.DoitEtrePoli;

public class ProxyFactory {

	public static Object createProxy(Object target) {
		Class<?>[] interfaces = target.getClass().getInterfaces();

		return Proxy.newProxyInstance(target.getClass().getClassLoader(), interfaces, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Method realMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());

				if (realMethod.isAnnotationPresent(DoitEtrePoli.class)) {
					System.out.println("bonjour");
					Object result = method.invoke(target, args);
					System.out.println("au revoir");
					return result;
				} else {
					return method.invoke(target, args);
				}
			}
		});
	}
}
