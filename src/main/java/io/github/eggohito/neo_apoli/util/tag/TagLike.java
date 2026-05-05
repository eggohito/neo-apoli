package io.github.eggohito.neo_apoli.util.tag;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
@Getter
public class TagLike<E> {

	private static final TagLike<?> EMPTY = new TagLike<>(ImmutableList.of(), ImmutableList.of());

	private final ImmutableList<TagEntry> entries;
	private final ImmutableList<E> elements;

	public boolean contains(E e) {
		return elements().contains(e);
	}

	public boolean isEmpty() {
		return elements().isEmpty();
	}

	@SuppressWarnings("unchecked")
	public static <T> TagLike<T> empty() {
		return (TagLike<T>) EMPTY;
	}

	public static <B extends ByteBuf, T> StreamCodec<B, TagLike<T>> createStreamCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createStreamCodec(lookupFromRegistry(registryLookup));
	}

	public static <B extends ByteBuf, T> StreamCodec<B, TagLike<T>> createStreamCodec(TagEntry.Lookup<T> lookup) {
		return NeoApoliStreamCodecs.TAG_ENTRIES.map(tagEntries -> new Builder<>(tagEntries, lookup).build().getOrThrow(), TagLike::entries).cast();
	}

	public static <T> Codec<TagLike<T>> createCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createCodec(lookupFromRegistry(registryLookup));
	}

	public static <T> Codec<TagLike<T>> createCodec(TagEntry.Lookup<T> lookup) {
		return TagEntry.CODEC.listOf().comapFlatMap(tagEntries -> new Builder<>(tagEntries, lookup).build(), TagLike::entries);
	}

	protected static <T> DataResult<ImmutableList<T>> toElementsWithPartial(List<TagEntry> entries, TagEntry.Lookup<T> lookup) {

		ImmutableList.Builder<T> foundBuilder = ImmutableList.builder();
		List<TagEntry> missing = new ObjectArrayList<>();

		for (var entry : entries) {

			if (!entry.build(lookup, foundBuilder::add)) {
				missing.add(entry);
			}

		}

		ImmutableList<T> found = foundBuilder.build();
		DataResult<ImmutableList<T>> result = DataResult.success(found);

		if (!missing.isEmpty()) {
			result = DataResult.error(() -> lookup.toString() + " is missing the following references: " + missing.stream().map(Objects::toString).collect(Collectors.joining(", ")));
		}

		return result.setPartial(found);

	}

	public static <E> TagEntry.Lookup<E> lookupFromRegistry(HolderLookup.RegistryLookup<E> registryLookup) {

		//noinspection unchecked
		ResourceKey<? extends Registry<E>> registryRef = (ResourceKey<? extends Registry<E>>) registryLookup.key();

		return new TagEntry.Lookup<>() {

			@Override
			public @Nullable E element(ResourceLocation id, boolean required) {
				return registryLookup.get(ResourceKey.create(registryRef, id))
					.map(Holder.Reference::value)
					.orElse(null);
			}

			@Override
			public @Nullable Collection<E> tag(ResourceLocation id) {
				return registryLookup.get(TagKey.create(registryRef, id))
					.map(this::unwrapHolderSet)
					.orElse(null);
			}

			@Override
			public String toString() {
				return "Registry \"" + registryRef.location() + "\"";
			}

			private List<E> unwrapHolderSet(HolderSet<E> holderSet) {
				return holderSet.stream()
					.map(Holder::value)
					.toList();
			}

		};

	}

	@Getter
	public static abstract class AbstractBuilder<T, L extends TagLike<T>, B extends AbstractBuilder<T, L, B>> {

		private final List<TagEntry> entries;
		private final TagEntry.Lookup<T> lookup;

		protected AbstractBuilder(List<TagEntry> entries, TagEntry.Lookup<T> lookup) {
			this.entries = entries;
			this.lookup = lookup;
		}

		public AbstractBuilder(TagEntry.Lookup<T> lookup) {
			this(new ObjectArrayList<>(), lookup);
		}

		public AbstractBuilder(HolderLookup.RegistryLookup<T> registryLookup) {
			this(lookupFromRegistry(registryLookup));
		}

		public abstract B getThis();

		public abstract DataResult<L> build();

		public B add(TagEntry entry) {
			this.entries.add(entry);
			return getThis();
		}

		public B addAll(Collection<TagEntry> entries) {
			this.entries.addAll(entries);
			return getThis();
		}

		public B addAll(TagEntry... entries) {
			return this.addAll(Arrays.asList(entries));
		}

	}

	@Getter
	public static final class Builder<T> extends AbstractBuilder<T, TagLike<T>, Builder<T>> {

		Builder(List<TagEntry> entries, TagEntry.Lookup<T> lookup) {
			super(entries, lookup);
		}

		public Builder(TagEntry.Lookup<T> lookup) {
			super(lookup);
		}

		public Builder(HolderLookup.RegistryLookup<T> registryLookup) {
			super(registryLookup);
		}

		@Override
		public Builder<T> getThis() {
			return this;
		}

		@Override
		public DataResult<TagLike<T>> build() {
			return toElementsWithPartial(entries(), lookup()).map(elements -> new TagLike<>(ImmutableList.copyOf(entries()), ImmutableList.copyOf(elements)));
		}

	}

}
