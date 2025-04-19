package io.github.eggohito.neo_apoli.util.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface ContextAware {

	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of();
	}

	default void validate(ErrorReporter reporter) {
		reporter.validate(this);
	}

	class ErrorReporter implements net.minecraft.util.ErrorReporter {

		private final String name;
		private final Supplier<String> pathSupplier;

		private final ContextType contextType;
		private final Set<RegistryKey<?>> referenceStack;

		private final Multimap<String, String> errors;
		private final Optional<RegistryWrapper.WrapperLookup> wrapperLookup;

		protected ErrorReporter(String name, Supplier<String> pathSupplier, ContextType contextType, Set<RegistryKey<?>> referenceStack, Multimap<String, String> errors, Optional<RegistryWrapper.WrapperLookup> wrapperLookup) {
			this.name = name;
			this.pathSupplier = Suppliers.memoize(pathSupplier::get);
			this.contextType = contextType;
			this.referenceStack = referenceStack;
			this.errors = errors;
			this.wrapperLookup = wrapperLookup;
		}

		public ErrorReporter(ContextType contextType) {
			this("", () -> "", contextType, Set.of(), HashMultimap.create(), Optional.empty());
		}

		@Override
		public ErrorReporter makeChild(String name) {
			String path = this.getPath();
			return new ErrorReporter(name, () -> appendPath(path, name), this.contextType, this.referenceStack, this.errors, this.wrapperLookup);
		}

		public ErrorReporter makeChild(String name, RegistryKey<?> key) {

			String path = this.getPath();
			Set<RegistryKey<?>> referenceStack = ImmutableSet.<RegistryKey<?>>builder()
				.addAll(this.referenceStack)
				.add(key)
				.build();

			return new ErrorReporter(name, () -> appendPath(path, name), this.contextType, referenceStack, this.errors, this.wrapperLookup);

		}

		public ErrorReporter withContextType(ContextType contextType) {
			return new ErrorReporter(this.name, this.pathSupplier, contextType, this.referenceStack, this.errors, this.wrapperLookup);
		}

		public ErrorReporter withWrapperLookup(@NotNull RegistryWrapper.WrapperLookup wrapperLookup) {
			return new ErrorReporter(this.name, this.pathSupplier, this.contextType, this.referenceStack, this.errors, Optional.of(wrapperLookup));
		}

		@Override
		public void report(String message) {
			this.errors.put(this.getPath(), message);
		}

		public ImmutableMultimap<String, String> getErrorsAsMap() {
			return ImmutableMultimap.copyOf(this.errors);
		}

		public Optional<String> getErrorsAsString() {

			Multimap<String, String> errorsMap = this.getErrorsAsMap();
			StringBuilder builder = new StringBuilder();

			if (errorsMap.isEmpty()) {
				return Optional.empty();
			}

			errorsMap.asMap().forEach((path, errors) -> {

				builder.append("at ").append(path).append(": ");
				String separator = errors.size() > 1 ? "\n\t - " : "";

				for (var error : errors) {
					builder.append(separator).append(error);
				}

				if (errorsMap.size() > 1) {
					builder.append("\n");
				}

			});

			return Optional.of(builder.toString());

		}

		public RegistryWrapper.WrapperLookup getWrapperLookup() {
			return wrapperLookup.orElseThrow(() -> new UnsupportedOperationException("Registry wrapper lookup is not present!"));
		}

		public String getName() {
			return name;
		}

		public String getPath() {
			return pathSupplier.get();
		}

		public boolean hasWrapperLookup() {
			return wrapperLookup.isPresent();
		}

		public boolean isInStack(RegistryKey<?> key) {
			return referenceStack.contains(key);
		}

		public void validate(ContextAware contextAware) {

			Set<ContextParameter<?>> missingParameters = Sets.difference(contextAware.getAllowedParameters(), contextType.getAllowed());

			if (!missingParameters.isEmpty()) {
				this.report("Parameters " + missingParameters + " are not provided in this context!");
			}

		}

		private static String appendPath(String path, String name) {
			return path + (path.isEmpty() ? "" : ".") + (name.contains(".") ? "'" + name + "'" : name);
		}

	}
}
