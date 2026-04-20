package io.github.eggohito.neo_apoli.api.power;

import com.google.common.collect.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public record PowersAttachment(ImmutableMap<PowerIdentifier, Power.Instance<?>> instances, ImmutableSetMultimap<PowerIdentifier, ResourceLocation> sources, Optional<String> decodingErrors, int version) {

	public static final Codec<PowersAttachment> CODEC = new Codec<>() {

		@Override
		public <T> DataResult<Pair<PowersAttachment, T>> decode(DynamicOps<T> ops, T input) {
			return Packed.CODEC.parse(ops, input).flatMap(packed -> unpack(ops, packed)).map(attachment -> Pair.of(attachment, input));
		}

		@Override
		public <T> DataResult<T> encode(PowersAttachment input, DynamicOps<T> ops, T prefix) {
			return input.pack(ops).flatMap(packed -> Packed.CODEC.encode(packed, ops, prefix));
		}

	};

	public static final StreamCodec<RegistryFriendlyByteBuf, PowersAttachment> STREAM_CODEC = new StreamCodec<>() {

		@Override
		public @NotNull PowersAttachment decode(RegistryFriendlyByteBuf buf) {

			boolean present = buf.readBoolean();
			Optional<Packed> packed = present ? Optional.of(Packed.STREAM_CODEC.decode(buf)) : Optional.empty();

			RegistryOps<Tag> nbtOps = buf.registryAccess().createSerializationContext(NbtOps.INSTANCE);
			return packed.flatMap(self -> unpack(nbtOps, self).resultOrPartial()).orElseGet(PowersAttachment::new);

		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, PowersAttachment input) {

			RegistryOps<Tag> nbtOps = buf.registryAccess().createSerializationContext(NbtOps.INSTANCE);
			Optional<Packed> packedList = input.pack(nbtOps).resultOrPartial();

			buf.writeBoolean(packedList.isPresent());
			packedList.ifPresent(self -> Packed.STREAM_CODEC.encode(buf, self));

		}

	};

	public PowersAttachment() {
		this(ImmutableMap.of(), ImmutableSetMultimap.of(), Optional.empty(), Powers.VERSION);
	}

	private <T> DataResult<Packed> pack(DynamicOps<T> ops) {

		Set<Entry> entries = new ObjectLinkedOpenHashSet<>();
		DataResult<Unit> identity = DataResult.success(Unit.INSTANCE);

		this.instances.forEach((reference, instance) -> {

			Set<ResourceLocation> sources = this.sources.get(reference);
			T data = identity.apply2stable((unit, t) -> t, instance.encodeData(ops)).mapOrElse(Function.identity(), error -> ops.emptyMap());

			Entry packed = new Entry(reference, instance.getPower().getType(), sources, new Dynamic<>(ops, data));
			entries.add(packed);

		});

		ImmutableSet<Entry> immutableEntries = ImmutableSet.copyOf(entries);
		return DataResult.success(new Packed(immutableEntries, version()));

	}

	//  TODO:   Pass the identity as the result since decoding/encoding data attachments in FAPI in version
	//          26.1.x now promotes its partial result
	/**
	 *  <p>Enforce partial result as success result since FAPI doesn't allow partial results when serializing data
	 *  attachments (yet?)</p>
	 *
	 *  <p>Also cache the errors encountered during decoding to be logged at a later point with the context of the
	 *  attachment target (which in this case, is an {@link net.minecraft.world.entity.Entity})</p>
	 */
	private static <T> DataResult<PowersAttachment> unpack(DynamicOps<T> ops, Packed packed) {

		Object2ObjectMap<PowerIdentifier, Power.Instance<?>> instancesMap = new Object2ObjectLinkedOpenHashMap<>();
		SetMultimap<PowerIdentifier, ResourceLocation> sourcesMap = LinkedHashMultimap.create();

		Set<Entry> entries = packed.entries();
		DataResult<Unit> identity = DataResult.success(Unit.INSTANCE);

		for (var entry : entries) {

			PowerIdentifier powerId = entry.id();
			DataResult<Power> powerResult = PowerManager.getAsResult(powerId);

			identity = identity.apply2stable((unit, power) -> unit, powerResult);

			if (powerResult.isError()) {
				continue;
			}

			Power power = powerResult.getOrThrow();
			Power.Instance<?> instance = power.createInstance();

			Dynamic<T> data = entry.data().convert(ops);
			Set<ResourceLocation> sources = entry.sources();

			if (Objects.equals(entry.type(), power.getType())) {
				identity = identity.apply2stable((identityUnit, decodeUnit) -> identityUnit, instance.decodeData(ops, data.getValue()));
			}

			else {
				identity = identity.apply2stable((unit, o) -> unit, DataResult.error(() -> "Couldn't transfer old data of " + entry.id().asDisplayString(false) + ", as it's now using a different power type!"));
			}

			instancesMap.put(powerId, instance);
			sourcesMap.putAll(powerId, sources);

		}

		PowersAttachment result = new PowersAttachment(ImmutableMap.copyOf(instancesMap), ImmutableSetMultimap.copyOf(sourcesMap), identity.error().map(DataResult.Error::message), packed.version());
		return DataResult.success(result);

	}

	record Packed(Set<Entry> entries, int version) {

		public static final Codec<Packed> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Entry.SET_CODEC.fieldOf("powers").forGetter(Packed::entries),
			Codec.INT.fieldOf("version").forGetter(Packed::version)
		).apply(instance, Packed::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Packed> STREAM_CODEC = StreamCodec.composite(
			Entry.SET_STREAM_CODEC, Packed::entries,
			ByteBufCodecs.INT, Packed::version,
			Packed::new
		);

	}

	record Entry(PowerIdentifier id, PowerType<?> type, Set<ResourceLocation> sources, Dynamic<?> data) {

		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PowerIdentifier.CODEC.fieldOf("id").forGetter(Entry::id),
			PowerType.CODEC.fieldOf("type").forGetter(Entry::type),
			NeoApoliCodecs.NON_EMPTY_IDENTIFIER_SET.fieldOf("sources").forGetter(Entry::sources),
			Codec.PASSTHROUGH.fieldOf("data").forGetter(Entry::data)
		).apply(instance, Entry::new));

		public static final Codec<Set<Entry>> SET_CODEC = CODEC.listOf().xmap(ImmutableSet::copyOf, ImmutableList::copyOf);

		public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
			PowerIdentifier.STREAM_CODEC, Entry::id,
			PowerType.STREAM_CODEC, Entry::type,
			NeoApoliStreamCodecs.NON_EMPTY_IDENTIFIER_SET, Entry::sources,
			NeoApoliStreamCodecs.REGISTRY_PASSTHROUGH, Entry::data,
			Entry::new
		);

		public static final StreamCodec<RegistryFriendlyByteBuf, Set<Entry>> SET_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list()).map(ImmutableSet::copyOf, ImmutableList::copyOf);

	}

	public record Mutable(Object2ObjectMap<PowerIdentifier, Power.Instance<?>> instances, SetMultimap<PowerIdentifier, ResourceLocation> sources) {

		public Mutable(PowersAttachment attachment) {
			this(new Object2ObjectLinkedOpenHashMap<>(attachment.instances()), LinkedHashMultimap.create(attachment.sources()));
		}

		public PowersAttachment toImmutable() {
			return new PowersAttachment(ImmutableMap.copyOf(this.instances), ImmutableSetMultimap.copyOf(this.sources), Optional.empty(), Powers.VERSION);
		}

	}

}
