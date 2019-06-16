package com.streamer.fun.side;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.calcite.adapter.enumerable.CallImplementor;
import org.apache.calcite.adapter.enumerable.NullPolicy;
import org.apache.calcite.adapter.enumerable.ReflectiveCallNotNullImplementor;
import org.apache.calcite.adapter.enumerable.RexImpTable;
import org.apache.calcite.adapter.enumerable.RexToLixTranslator;
import org.apache.calcite.linq4j.tree.ConstantExpression;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.NewExpression;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.schema.FunctionParameter;
import org.apache.calcite.schema.ImplementableFunction;
import org.apache.calcite.schema.ScalarFunction;
import org.apache.calcite.schema.impl.ReflectiveFunctionBase.ParameterListBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMultimap;

public class SideTableFactory implements ScalarFunction, ImplementableFunction {

	private CallImplementor implementor;

	private Method method;

	public SideTableFactory() {
	}

	public SideTableFactory(Method method, CallImplementor implementor) {
		this.method = method;
		this.implementor = implementor;
	}

	@Override
	public List<FunctionParameter> getParameters() {
		return new ParameterListBuilder().addMethodParameters(method).build();
	}

	public ImmutableMultimap<String, ScalarFunction> createAll(Class<?> clazz, String fun, String config) {
		ImmutableMultimap.Builder<String, ScalarFunction> builder = ImmutableMultimap.builder();
		for (Method method : clazz.getMethods()) {
			if (method.getDeclaringClass() == Object.class) {
				continue;
			}
			builder.put(fun, create(method, fun, config));
		}
		return builder.build();
	}

	public ScalarFunction create(Class<?> clazz, String methodName, String fun, String config) {
		Method method = findMethod(clazz, methodName);
		if (method == null) {
			return null;
		}
		return create(method, fun, config);
	}

	public ScalarFunction create(Method method, String fun, String config) {
		CallImplementor implementor = createImplementor(method, fun, config);
		return new SideTableFactory(method, implementor);
	}

	@Override
	public RelDataType getReturnType(RelDataTypeFactory typeFactory) {
		return typeFactory.createJavaType(method.getReturnType());
	}

	@Override
	public CallImplementor getImplementor() {
		return implementor;
	}

	private CallImplementor createImplementor(Method method, String fun, String config) {

		return RexImpTable.createImplementor(new ReflectiveCallNotNullImplementor(method) {
			private Logger logger = LoggerFactory.getLogger(getClass());

			@Override
			public Expression implement(RexToLixTranslator translator, RexCall call, List<Expression> expressions) {
				// 只有基础类型才能被构造
				ConstantExpression fExpression = Expressions.constant(fun, String.class);
				ConstantExpression cExpression = Expressions.constant(config, String.class);
				logger.info("Expression fun:{}", fun);
				try {
					NewExpression target = Expressions.new_(
							method.getDeclaringClass().getConstructor(new Class[] { String.class, String.class }),
							fExpression, cExpression);
					return Expressions.call(target, method, expressions);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					return null;
				}
			}
		}, NullPolicy.NONE, false);
	}

	private static Method findMethod(Class<?> clazz, String name) {
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(name)) {
				return method;
			}
		}
		return null;
	}

}
