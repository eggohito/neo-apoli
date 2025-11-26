package io.github.eggohito.neo_apoli.util.context;

import com.google.common.base.Suppliers;
import com.google.common.collect.*;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface ContextAware {

	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of();
	}

	default void validate(ProblemReporter reporter) {
		reporter.validate(this);
	}

	class ProblemReporter implements net.minecraft.util.ProblemReporter {

		private final ProblemReporter parent;

		@Getter
		private final Optional<HolderLookup.Provider> holderProvider;
		@Getter
		private final ContextKeySet keySet;

		private final Multimap<String, String> errors;
		private final Set<ReferenceKey> referenceStack;

		@Getter
		private final String path;
		private final Supplier<String> fullPathSupplier;

		protected ProblemReporter(ProblemReporter parent, Optional<HolderLookup.Provider> holderProvider, ContextKeySet keySet, Multimap<String, String> errors, Set<ReferenceKey> referenceStack, String path, Supplier<String> fullPathSupplier) {
			this.parent = parent;
			this.holderProvider = holderProvider;
			this.keySet = keySet;
			this.errors = errors;
			this.referenceStack = referenceStack;
			this.path = path;
			this.fullPathSupplier = Suppliers.memoize(fullPathSupplier::get);
		}

		public ProblemReporter(ContextKeySet keySet, String path) {
			this(null, Optional.empty(), keySet, HashMultimap.create(), Set.of(), path, () -> path);
		}

		public ProblemReporter(String path) {
			this(LootContextParamSets.EMPTY, path);
		}

		public ProblemReporter(ContextKeySet keySet) {
			this(keySet, "");
		}

		public ProblemReporter() {
			this(LootContextParamSets.EMPTY);
		}

		@Override
		public ProblemReporter forChild(String path) {
			return new ProblemReporter(this, this.holderProvider, this.keySet, this.errors, this.referenceStack, path, () -> appendPath(path));
		}

		public ProblemReporter forChild(String path, ReferenceKey key) {

			Set<ReferenceKey> referenceStack = ImmutableSet.<ReferenceKey>builder()
				.addAll(this.referenceStack)
				.add(key)
				.build();

			return new ProblemReporter(this, this.holderProvider, this.keySet, this.errors, referenceStack, path, () -> appendPath(path));

		}

		public ProblemReporter withKeySet(ContextKeySet contextType) {
			return new ProblemReporter(this.parent, this.holderProvider, contextType, this.errors, this.referenceStack, this.path, this.fullPathSupplier);
		}

		public ProblemReporter withHolderProvider(@NotNull HolderLookup.Provider wrapperLookup) {
			return new ProblemReporter(this.parent, Optional.of(wrapperLookup), this.keySet, this.errors, this.referenceStack, this.path, this.fullPathSupplier);
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
				builder
					.append("at")
					.append(moreThanOnePaths ? " these paths: " : " path ");
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

		public boolean hasWrapperLookup() {
			return holderProvider.isPresent();
		}

		public String getFullPath() {
			return fullPathSupplier.get();
		}

		public ProblemReporter getParent() {

			if (this.hasParent()) {
				return parent;
			}

			else {
				throw new UnsupportedOperationException("The root reporter cannot have a parent!");
			}

		}

		public boolean hasParent() {
			return parent != null;
		}

		public ProblemReporter getRoot() {

			if (this.parent == null) {
				return this;
			}

			else {
				return this.parent.getRoot();
			}

		}

		public boolean isRoot() {
			return this.parent == null;
		}

		public boolean isInStack(ReferenceKey key) {
			return referenceStack.contains(key);
		}

		public boolean hasAnyErrors() {
			return !this.errors.isEmpty();
		}

		public boolean hasErrors() {
			return this.errors.containsKey(this.getFullPath());
		}

		public void validate(ContextAware contextAware) {

			Set<ContextKey<?>> missingParameters = Sets.difference(contextAware.getRequiredParameters(), keySet.allowed());

			if (!missingParameters.isEmpty()) {
				this.report("Parameters [" + missingParameters.stream().map(ContextKey::name).map(ResourceLocation::toString).collect(Collectors.joining(", ")) + "] are not provided in the context for " + (contextAware instanceof StringDisplayable stringDisplayable ? stringDisplayable.asDisplayString(false) : contextAware) + "!");
			}

		}

		private String appendPath(String path) {
			return this.getFullPath() + path;
		}

	}
}
