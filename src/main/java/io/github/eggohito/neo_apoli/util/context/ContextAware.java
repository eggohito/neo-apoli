package io.github.eggohito.neo_apoli.util.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.*;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface ContextAware {

	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of();
	}

	default void validate(ErrorReporter reporter) {
		reporter.validate(this);
	}

	String asDisplayString();

	default String asDisplayString(boolean capitalized) {
		String displayString = this.asDisplayString();
		return capitalized
			? StringUtils.capitalize(displayString)
			: StringUtils.uncapitalize(displayString);
	}

	class ErrorReporter implements net.minecraft.util.ErrorReporter {

		private final Optional<ErrorReporter> parent;
		private final Optional<RegistryWrapper.WrapperLookup> wrapperLookup;

		private final ContextType contextType;

		private final Multimap<String, String> errors;
		private final Set<ContextKey> referenceStack;

		private final String path;
		private final Supplier<String> fullPathSupplier;

		protected ErrorReporter(Optional<ErrorReporter> parent, Optional<RegistryWrapper.WrapperLookup> wrapperLookup, ContextType contextType, Multimap<String, String> errors, Set<ContextKey> referenceStack, String path, Supplier<String> fullPathSupplier) {
			this.parent = parent;
			this.wrapperLookup = wrapperLookup;
			this.contextType = contextType;
			this.errors = errors;
			this.referenceStack = referenceStack;
			this.path = path;
			this.fullPathSupplier = Suppliers.memoize(fullPathSupplier::get);
		}

		public ErrorReporter(ContextType contextType) {
			this(Optional.empty(), Optional.empty(), contextType, HashMultimap.create(), Set.of(), "", () -> "");
		}

		public ErrorReporter() {
			this(LootContextTypes.EMPTY);
		}

		@Override
		public ErrorReporter makeChild(String path) {
			return new ErrorReporter(Optional.of(this), this.wrapperLookup, this.contextType, this.errors, this.referenceStack, path, () -> appendPath(path));
		}

		public ErrorReporter makeChild(String path, ContextKey key) {

			Set<ContextKey> referenceStack = ImmutableSet.<ContextKey>builder()
				.addAll(this.referenceStack)
				.add(key)
				.build();

			return new ErrorReporter(Optional.of(this), this.wrapperLookup, this.contextType, this.errors, referenceStack, path, () -> appendPath(path));

		}

		public ErrorReporter withContextType(ContextType contextType) {
			return new ErrorReporter(this.parent, this.wrapperLookup, contextType, this.errors, this.referenceStack, this.path, this.fullPathSupplier);
		}

		public ErrorReporter withWrapperLookup(@NotNull RegistryWrapper.WrapperLookup wrapperLookup) {
			return new ErrorReporter(this.parent, Optional.of(wrapperLookup), this.contextType, this.errors, this.referenceStack, this.path, this.fullPathSupplier);
		}

		@Override
		public void report(String message) {
			this.errors.put(this.getFullPath(), message);
		}

		public ImmutableMultimap<String, String> getErrorsAsMap() {
			return ImmutableMultimap.copyOf(this.errors);
		}

		public String getErrorsAsString() {

			Multimap<String, String> errorsMap = this.getErrorsAsMap();
			StringBuilder builder = new StringBuilder();

			boolean moreThanOnePaths = errorsMap.size() > 1;

			if (!errorsMap.isEmpty()) {
				builder.append("at path ").append(moreThanOnePaths ? "these paths:" : "");
			}

			errorsMap.asMap().forEach((path, errors) -> {

				builder
					.append(moreThanOnePaths ? "\n\t - " : "")
					.append(path).append(": ");

				for (var error : errors) {
					builder
						.append(errors.size() > 1 ? "\n\t\t * " : "")
						.append(error);
				}

			});

			return builder.toString();

		}

		public RegistryWrapper.WrapperLookup getWrapperLookup() {
			return wrapperLookup.orElseThrow(() -> new UnsupportedOperationException("Registry wrapper lookup is not present!"));
		}

		public boolean hasWrapperLookup() {
			return wrapperLookup.isPresent();
		}

		public String getPath() {
			return path;
		}

		public String getFullPath() {
			return fullPathSupplier.get();
		}

		public ContextType getContextType() {
			return contextType;
		}

		public ErrorReporter getParent() {
			return parent.orElseThrow(() -> new UnsupportedOperationException("The root reporter cannot have a parent!"));
		}

		public ErrorReporter getRoot() {

			if (this.parent.isEmpty()) {
				return this;
			}

			else {
				return this.parent.get().getRoot();
			}

		}

		public boolean isInStack(ContextKey key) {
			return referenceStack.contains(key);
		}

		public boolean hasErrors() {
			return !this.errors.isEmpty();
		}

		public boolean pathHasErrors() {
			return this.errors.containsKey(this.getFullPath());
		}

		public void validate(ContextAware contextAware) {

			Set<ContextParameter<?>> missingParameters = Sets.difference(contextAware.getAllowedParameters(), contextType.getAllowed());

			if (!missingParameters.isEmpty()) {
				this.report("Parameters [" + missingParameters.stream().map(ContextParameter::getId).map(Identifier::toString).collect(Collectors.joining(", ")) + "] are not provided in the context for " + contextAware.asDisplayString(false) + "!");
			}

		}

		private String appendPath(String path) {
			String fullPath = this.getFullPath();
			return fullPath + (fullPath.isEmpty() ? "" : ".") + (path.contains(".") ? "'" + path + "'" : path);
		}

	}
}
