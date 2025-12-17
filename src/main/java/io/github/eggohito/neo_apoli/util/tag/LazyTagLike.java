package io.github.eggohito.neo_apoli.util.tag;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class LazyTagLike<T> extends TagLike<T> {

	private final Supplier<DataResult<ImmutableList<T>>> supplier;

	private LazyTagLike(ImmutableList<TagEntry> entries, Lookup<T> lookup) {
		super(entries, ImmutableList.of());
		this.supplier = Suppliers.memoize(() -> toElementsWithPartial(entries, lookup));
	}

	@Override
	public ImmutableList<T> elements() {
		return supplier.get().resultOrPartial().orElseThrow();	// This shouldn't throw as the supplied result has a partial value
	}

	public DataResult<Unit> loadCache() {
		return supplier.get().map(elements -> Unit.INSTANCE);
	}

	public static <B extends ByteBuf, T> StreamCodec<B, LazyTagLike<T>> createLazyStreamCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createLazyStreamCodec(Lookup.fromRegistry(registryLookup));
	}

	public static <B extends ByteBuf, T> StreamCodec<B, LazyTagLike<T>> createLazyStreamCodec(Lookup<T> lookup) {
		return NeoApoliStreamCodecs.TAG_ENTRIES.map(tagEntries -> new LazyTagLike<>(ImmutableList.copyOf(tagEntries), lookup), LazyTagLike::entries).cast();
	}

	public static <T> Codec<LazyTagLike<T>> createLazyCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createLazyCodec(Lookup.fromRegistry(registryLookup));
	}

	public static <T> Codec<LazyTagLike<T>> createLazyCodec(Lookup<T> lookup) {
		return TagEntry.CODEC.listOf().xmap(tagEntries -> new LazyTagLike<>(ImmutableList.copyOf(tagEntries), lookup), LazyTagLike::entries);
	}

	@Getter
	public static final class Builder<T> {

		private final List<TagEntry> entries;
		private final TagLike.Lookup<T> lookup;

		Builder(List<TagEntry> entries, TagLike.Lookup<T> lookup) {
			this.entries = entries;
			this.lookup = lookup;
		}

		public Builder(TagLike.Lookup<T> lookup) {
			this(new ObjectArrayList<>(), lookup);
		}

		public Builder(HolderLookup.RegistryLookup<T> registryLookup) {
			this(TagLike.Lookup.fromRegistry(registryLookup));
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

		public LazyTagLike<T> build() {
			return new LazyTagLike<>(ImmutableList.copyOf(entries), lookup);
		}

	}

}
