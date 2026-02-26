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

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TagLike<E> {

	private final ImmutableList<TagEntry> entries;
	private final ImmutableList<E> elements;

	public static <B extends ByteBuf, T> StreamCodec<B, TagLike<T>> createStreamCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createStreamCodec(lookupFromRegistry(registryLookup));
	}

	public static <B extends ByteBuf, T> StreamCodec<B, TagLike<T>> createStreamCodec(TagEntry.Lookup<T> lookup) {
		return NeoApoliStreamCodecs.TAG_ENTRIES.map(tagEntries -> build(tagEntries, lookup).getOrThrow(), TagLike::entries).cast();
	}

	public static <T> Codec<TagLike<T>> createCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createCodec(lookupFromRegistry(registryLookup));
	}

	public static <T> Codec<TagLike<T>> createCodec(TagEntry.Lookup<T> lookup) {
		return TagEntry.CODEC.listOf().comapFlatMap(tagEntries -> build(tagEntries, lookup), TagLike::entries);
	}

	private static <T> DataResult<TagLike<T>> build(List<TagEntry> entries, TagEntry.Lookup<T> lookup) {
		return toElementsWithPartial(entries, lookup).map(elements -> new TagLike<>(ImmutableList.copyOf(entries), ImmutableList.copyOf(elements)));
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
					.map(TagLike::unwrapHolderSet)
					.orElse(null);
			}

			@Override
			public String toString() {
				return "Registry \"" + registryRef.location() + "\"";
			}

		};

	}

	private static <E> List<E> unwrapHolderSet(HolderSet<E> holderSet) {
		return holderSet.stream()
			.map(Holder::value)
			.toList();
	}

	@Getter
	public static final class Builder<T> {

		private final List<TagEntry> entries;
		private final TagEntry.Lookup<T> lookup;

		Builder(List<TagEntry> entries, TagEntry.Lookup<T> lookup) {
			this.entries = entries;
			this.lookup = lookup;
		}

		public Builder(TagEntry.Lookup<T> lookup) {
			this(new ObjectArrayList<>(), lookup);
		}

		public Builder(HolderLookup.RegistryLookup<T> registryLookup) {
			this(lookupFromRegistry(registryLookup));
		}

		public Builder<T> add(TagEntry entry) {
			this.entries.add(entry);
			return this;
		}

		public Builder<T> addAll(TagEntry... entries) {
			return addAll(Arrays.asList(entries));
		}

		public Builder<T> addAll(Collection<TagEntry> entries) {
			entries.forEach(this::add);
			return this;
		}

		public DataResult<TagLike<T>> build() {
			return TagLike.build(entries, lookup);
		}

	}

}
