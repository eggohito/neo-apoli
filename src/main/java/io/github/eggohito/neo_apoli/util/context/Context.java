package io.github.eggohito.neo_apoli.util.context;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.github.eggohito.neo_apoli.mixin.access.ContextMapAccessor;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Context implements ContextParameterHolder {

	protected final Level level;
	protected final Validator validator;

	protected ContextMap parameters;
	protected ImmutableSet<ContextAware> activeEntries;

	@Override
	public <T> T required(ContextKey<T> parameter) {
		return this.getParameters().getOrThrow(parameter);
	}

	@Override
	public <T> @Nullable T nullable(ContextKey<T> parameter) {
		return this.getParameters().getOptional(parameter);
	}

	public ContextKeySet getKeySet() {
		return getValidator().getKeySet();
	}

	public Context forChild(String path) {
		return new Context(this.level, this.validator.forChild(path), this.parameters, this.activeEntries);
	}

	public Context forChildWithReference(String path, ReferenceKey key) {
		return new Context(this.level, this.validator.forChildWithReference(path, key), this.parameters, this.activeEntries);
	}

	public boolean isActive(ContextAware entry) {
		return this.getActiveEntries().contains(entry);
	}

	public boolean markActive(ContextAware entry) {

		Set<ContextAware> newEntries = new ObjectOpenHashSet<>(this.activeEntries);
		boolean added = newEntries.add(entry);

		this.activeEntries = ImmutableSet.copyOf(newEntries);
		return added;

	}

	public boolean markInActive(ContextAware entry) {

		Set<ContextAware> newEntries = new ObjectOpenHashSet<>(this.activeEntries);
		boolean removed = newEntries.remove(entry);

		this.activeEntries = ImmutableSet.copyOf(newEntries);
		return removed;

	}

	public boolean hasErrors() {
		return this.getValidator().selfPathHasErrors();
	}

	public boolean hasAnyErrors() {
		return this.getValidator().hasErrors();
	}

	public void report(String message) {
		this.getValidator().report(message);
	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Builder implements ContextParameterHolder {

		private final ContextMap.Builder parameters;
		private ImmutableSet<ContextAware> activeEntries;

		@Getter
		private Validator validator;

		public Builder(Validator validator) {
			this(new ContextMap.Builder(), ImmutableSet.of(), validator);
		}

		public Builder(ContextKeySet keySet) {
			this(new Validator().withKeySet(keySet));
		}

		public Builder(Context context) {

			ContextMap.Builder newParameters = new ContextMap.Builder();
			((ContextMapAccessor) context.getParameters()).getParams().forEach((parameter, obj) -> ((ContextMapAccessor.BuilderAccessor) newParameters).getParams().put(parameter, obj));

			this.parameters = newParameters;
			this.activeEntries = context.getActiveEntries();
			this.validator = context.getValidator();

		}

		public Builder() {
			this(LootContextParamSets.EMPTY);
		}

		@Override
		public <T> T required(ContextKey<T> parameter) {
			return this.parameters.getParameter(parameter);
		}

		@Override
		public <T> @Nullable T nullable(ContextKey<T> parameter) {
			return this.parameters.getOptionalParameter(parameter);
		}

		public <T> Builder add(ContextKey<T> parameter, @NotNull T value) {
			this.parameters.withParameter(parameter, value);
			return this;
		}

		public <T> Builder addIfAbsent(ContextKey<T> parameter, Supplier<@NotNull T> value) {
			return hasParameter(parameter)
				? this
				: add(parameter, value.get());
		}

		public <T> Builder addNullable(ContextKey<T> parameter, @Nullable T value) {
			this.parameters.withOptionalParameter(parameter, value);
			return this;
		}

		public <T> Builder addNullableIfAbsent(ContextKey<T> parameter, Supplier<@Nullable T> value) {
			return hasParameter(parameter)
				? this
				: addNullable(parameter, value.get());
		}

		public <T> Builder addOptional(ContextKey<T> parameter, Optional<T> value) {
			this.addNullable(parameter, value.orElse(null));
			return this;
		}

		public <T> Builder addOptionalIfAbsent(ContextKey<T> parameter, Supplier<Optional<T>> value) {
			return hasParameter(parameter)
				? this
				: addOptional(parameter, value.get());
		}

		public Builder withKeySet(ContextKeySet keySet) {
			this.validator = validator.withKeySet(keySet);
			return this;
		}

		public Builder withValidator(Validator validator) {
			this.validator = validator;
			return this;
		}

		public Context build(Level level) {
			return new Context(level, this.getValidator(), this.parameters.create(this.getKeySet()), this.activeEntries);
		}

		public ContextKeySet getKeySet() {
			return this.getValidator().getKeySet();
		}

		public boolean isActive(ContextAware entry) {
			return this.activeEntries.contains(entry);
		}

	}

	@Getter
	@AllArgsConstructor
	public static class Validator {

		private final ContextKeySet keySet;
		private final Reporter reporter;

		private final Optional<HolderLookup.Provider> lookupProvider;
		private final ImmutableSet<ReferenceKey> references;

		public Validator(ContextKeySet keySet, Reporter reporter) {
			this(keySet, reporter, Optional.empty(), ImmutableSet.of());
		}

		public Validator(ContextKeySet keySet, String path) {
			this(keySet, new Reporter(path));
		}

		public Validator(ContextKeySet keySet) {
			this(keySet, "");
		}

		public Validator(String path) {
			this(NeoApoliContextKeySets.ANY, path);
		}

		public Validator() {
			this("");
		}

		public Validator forChild(String name) {
			return new Validator(this.getKeySet(), this.getReporter().forChild(name), this.getLookupProvider(), this.getReferences());
		}

		public Validator forChildWithReference(String name, ReferenceKey reference) {

			ImmutableSet<ReferenceKey> references = ImmutableSet.<ReferenceKey>builder()
				.addAll(this.references)
				.add(reference)
				.build();

			return new Validator(this.getKeySet(), this.getReporter().forChild(name), this.getLookupProvider(), references);

		}

		public Validator withKeySet(ContextKeySet keySet) {
			return new Validator(keySet, this.getReporter(), this.getLookupProvider(), this.getReferences());
		}

		public Validator withReporter(Reporter reporter) {
			return new Validator(this.getKeySet(), reporter);
		}

		public Validator withLookupProvider(HolderLookup.Provider lookupProvider) {
			return new Validator(this.getKeySet(), this.getReporter(), Optional.ofNullable(lookupProvider), this.getReferences());
		}

		public HolderLookup.Provider getLookupProviderUnsafe() {
			return this.getLookupProvider().orElseThrow(() -> new IllegalStateException("References are not allowed!"));
		}

		public boolean hasLookupProvider() {
			return this.getLookupProvider().isPresent();
		}

		public ImmutableMultimap<String, String> getErrors() {
			return this.getReporter().getErrors();
		}

		public Optional<String> getErrorsFlattened() {
			return this.getReporter().getErrorsFlattened();
		}

		public boolean pathHasErrors(String path) {
			return this.getReporter().pathHasErrors(path);
		}

		public boolean selfPathHasErrors() {
			return this.getReporter().selfPathHasErrors();
		}

		public boolean hasErrors() {
			return this.getReporter().hasErrors();
		}

		public boolean isReferenced(ReferenceKey reference) {
			return this.getReferences().contains(reference);
		}

		public void validate(ContextAware contextAware) {

			Set<ContextKey<?>> missingParameters = Sets.difference(contextAware.getRequiredParameters(), this.getKeySet().allowed());

			if (!missingParameters.isEmpty()) {
				this.report("Parameters [" + missingParameters.stream().map(ContextKey::name).map(ResourceLocation::toString).collect(Collectors.joining(", ")) + "] are not provided in the context for " + (contextAware instanceof StringDisplayable displayable ? displayable.asDisplayString(false) : contextAware) + "!");
			}

		}

		public void report(String message) {
			this.getReporter().report(message);
		}

	}

}
