package io.github.eggohito.neo_apoli.util.tag;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class LazyTagLike<T> {

	private final List<TagEntry> entries;
	private final Supplier<DataResult<List<T>>> supplier;

	private LazyTagLike(List<TagEntry> entries, TagLike.Lookup<T> lookup) {
		this.entries = entries;
		this.supplier = Suppliers.memoize(() -> TagLike.toElementsWithPartial(entries, lookup));
	}

	public List<TagEntry> entries() {
		return entries;
	}

	public List<T> elements() {
		return supplier.get().resultOrPartial().orElseThrow();	// This shouldn't throw as the supplied result has a partial value
	}

	public DataResult<Unit> load() {
		return supplier.get().map(ts -> Unit.INSTANCE);
	}

	public static <B extends ByteBuf, T> StreamCodec<B, LazyTagLike<T>> createStreamCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createStreamCodec(TagLike.Lookup.fromRegistry(registryLookup));
	}

	public static <B extends ByteBuf, T> StreamCodec<B, LazyTagLike<T>> createStreamCodec(TagLike.Lookup<T> lookup) {
		return NeoApoliStreamCodecs.TAG_ENTRIES.map(tagEntries -> new LazyTagLike<>(tagEntries, lookup), LazyTagLike::entries).cast();
	}

	public static <T> Codec<LazyTagLike<T>> createCodec(HolderLookup.RegistryLookup<T> registryLookup) {
		return createCodec(TagLike.Lookup.fromRegistry(registryLookup));
	}

	public static <T> Codec<LazyTagLike<T>> createCodec(TagLike.Lookup<T> lookup) {
		return TagEntry.CODEC.listOf().xmap(tagEntries -> new LazyTagLike<>(tagEntries, lookup), LazyTagLike::entries);
	}

	public static final class Builder<T> {

		private final List<TagEntry> entries;
		private final TagLike.Lookup<T> lookup;

		public Builder(List<TagEntry> entries, TagLike.Lookup<T> lookup) {
			this.entries = new ObjectArrayList<>(entries);
			this.lookup = lookup;
		}

		public Builder(List<TagEntry> entries, HolderLookup.RegistryLookup<T> registryLookup) {
			this(entries, TagLike.Lookup.fromRegistry(registryLookup));
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
			return new LazyTagLike<>(entries, lookup);
		}

	}

}
