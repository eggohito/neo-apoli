package io.github.eggohito.neo_apoli.context;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class ContextParams implements ContextParamsHolder {

	private final Map<ContextKey<?>, Object> params;
	private final Set<ContextKey<?>> optional;

	ContextParams(Map<ContextKey<?>, Object> params, Set<ContextKey<?>> optional) {
		this.params = params;
		this.optional = optional;
	}

	@Override
	public ContextKeySet toKeySet() {
		return ContextHelper.toKeySet(params.keySet(), optional);
	}

	@Override
	public @Nullable <T> T getNullable(ContextKey<T> parameter) {
		return (T) params.get(parameter);
	}

	public Builder toBuilder() {
		return new Builder(this.params, this.optional);
	}

	public static class Builder implements ContextParamsHolder {

		private final Map<ContextKey<?>, Object> params;
		private final Set<ContextKey<?>> optional;

		Builder(Map<ContextKey<?>, Object> params, Set<ContextKey<?>> optional) {
			this.params = params;
			this.optional = optional;
		}

		public Builder() {
			this(new IdentityHashMap<>(), new ObjectOpenHashSet<>());
		}

		@Override
		public ContextKeySet toKeySet() {
			return ContextHelper.toKeySet(params.keySet(), optional);
		}

		@Override
		public @Nullable <T> T getNullable(ContextKey<T> parameter) {
			return (T) params.get(parameter);
		}

		public <T> Builder withNullable(Context.Parameter<T> key, @Nullable T value) {

			this.params.put(key,value);

			if (value == null) {
				optional.add(key);
			}

			return this;

		}

		public <T> Builder withNullableIfAbsent(Context.Parameter<T> key, Supplier<@Nullable T> value) {
			return hasParameter(key) ? this : withNullable(key, value.get());
		}

		public <T> Builder withRequired(Context.Parameter<T> key, @NotNull T value) {
			return this.withNullable(key, value);
		}

		public <T> Builder withRequiredIfAbsent(Context.Parameter<T> key, Supplier<@NotNull T> value) {
			return hasParameter(key) ? this : withRequired(key, value.get());
		}

		public <T> Builder withOptional(Context.Parameter<T> key, Optional<T> value) {
			return this.withNullable(key, value.orElse(null));
		}

		public <T> Builder withOptionalIfAbsent(Context.Parameter<T> key, Supplier<Optional<T>> value) {
			return  hasParameter(key) ? this : withOptional(key, value.get());
		}

		public ContextParams buildWithRequirements(ContextKeySet keySet) {

			Set<ContextKey<?>> disallowed = Sets.difference(this.params.keySet(), keySet.allowed());

			if (!disallowed.isEmpty()) {
				throw new IllegalArgumentException("Disallowed parameters in parameter set: " + disallowed);
			}

			else {

				Set<ContextKey<?>> required = Sets.difference(keySet.required(), this.params.keySet());

				if (!required.isEmpty()) {
					throw new IllegalArgumentException("Missing required parameters: " + required);
				}

				else {
					return build();
				}

			}

		}

		public ContextParams build() {
			return new ContextParams(params, optional);
		}

	}

}
