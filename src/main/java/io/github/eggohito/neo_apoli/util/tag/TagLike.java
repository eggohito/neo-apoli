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
public class TagLike<T> {

	private final ImmutableList<TagEntry> entries;
	private final ImmutableList<T> elements;

	public static <B extends ByteBuf, T> StreamCodec<B, TagLike<T>> createStreamCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createStreamCodec(Lookup.fromRegistry(registryLookup));
	}

	public static <B extends ByteBuf, T> StreamCodec<B, TagLike<T>> createStreamCodec(Lookup<T> lookup) {
		return NeoApoliStreamCodecs.TAG_ENTRIES.map(tagEntries -> build(tagEntries, lookup).getOrThrow(), TagLike::entries).cast();
	}

	public static <T> Codec<TagLike<T>> createCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createCodec(Lookup.fromRegistry(registryLookup));
	}

	public static <T> Codec<TagLike<T>> createCodec(Lookup<T> lookup) {
		return TagEntry.CODEC.listOf().comapFlatMap(tagEntries -> build(tagEntries, lookup), TagLike::entries);
	}

	private static <T> DataResult<TagLike<T>> build(List<TagEntry> entries, Lookup<T> lookup) {
		return toElementsWithPartial(entries, lookup).map(elements -> new TagLike<>(ImmutableList.copyOf(entries), ImmutableList.copyOf(elements)));
	}

	protected static <T> DataResult<ImmutableList<T>> toElementsWithPartial(List<TagEntry> entries, Lookup<T> lookup) {

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
			result = DataResult.error(() -> lookup.name() + " is missing the following references: " + missing.stream().map(Objects::toString).collect(Collectors.joining(", ")));
		}

		return result.setPartial(found);

	}

	@Getter
	public static final class Builder<T> {

		private final List<TagEntry> entries;
		private final Lookup<T> lookup;

		Builder(List<TagEntry> entries, Lookup<T> lookup) {
			this.entries = new ObjectArrayList<>(entries);
			this.lookup = lookup;
		}

		public Builder(Lookup<T> lookup) {
			this(new ObjectArrayList<>(), lookup);
		}

		public Builder(HolderLookup.RegistryLookup<T> registryLookup) {
			this(Lookup.fromRegistry(registryLookup));
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

	public interface Lookup<E> extends TagEntry.Lookup<E> {

		String name();

		static <E> Lookup<E> fromRegistry(HolderLookup.RegistryLookup<E> registryLookup) {

			//noinspection unchecked
			ResourceKey<? extends Registry<E>> registryRef = (ResourceKey<? extends Registry<E>>) registryLookup.key();

			return new Lookup<>() {

				@Nullable
				@Override
				public E element(ResourceLocation id, boolean required) {
					return registryLookup.get(ResourceKey.create(registryRef, id))
						.map(Holder.Reference::value)
						.orElse(null);
				}

				@Nullable
				@Override
				public Collection<E> tag(ResourceLocation id) {
					return registryLookup.get(TagKey.create(registryRef, id))
						.map(Lookup::unwrapHolders)
						.orElse(null);
				}

				@Override
				public String name() {
					return "Registry \"" + registryRef.location() + "\" tag-like";
				}

			};

		}

		static <E> List<E> unwrapHolders(HolderSet<E> holders) {
			return holders
				.stream()
				.map(Holder::value)
				.toList();
		}

	}

}
