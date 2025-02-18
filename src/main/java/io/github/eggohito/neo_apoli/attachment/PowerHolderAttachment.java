package io.github.eggohito.neo_apoli.attachment;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.PowerIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class PowerHolderAttachment {

	public static final Codec<PowerHolderAttachment> CODEC = new Codec<>() {

		@Override
		public <T> DataResult<Pair<PowerHolderAttachment, T>> decode(DynamicOps<T> ops, T input) {

			if (ops instanceof RegistryOps<T> registryOps) {

				RegistryOps<NbtElement> nbtOps = registryOps.withDelegate(NbtOps.INSTANCE);
				DataResult<Pair<List<Entry>, T>> entriesResult = Entry.CODEC.listOf().decode(ops, input);

				return entriesResult.map(entriesAndInput -> {

					Map<PowerIdentifier, Power> powers = new Object2ObjectOpenHashMap<>();
					Map<PowerIdentifier, Set<Identifier>> sources = new Object2ObjectOpenHashMap<>();

					for (Entry entry : entriesAndInput.getFirst()) {

						PowerIdentifier id = entry.powerId();
						DataResult<Power> powerResult = PowerManager.getAsResult(id)
							.flatMap(p -> Power.BASE_CODEC.encodeStart(registryOps, p)
								.flatMap(t -> Power.BASE_CODEC.parse(registryOps, t)));

						switch (powerResult) {
							case DataResult.Success<Power> success -> {

								Power power = success.value();
								NbtElement powerData = entry.data();

								try {
									power.fromNbt(nbtOps, powerData);
								}

								catch (Exception e) {
									NeoApoli.LOGGER.warn("Power type of power \"{}\" has changed. Skipping data...", id);
								}

								powers.put(id, power);
								sources.put(id, entry.sources());

							}
							case DataResult.Error<Power> error ->
								NeoApoli.LOGGER.warn("Couldn't decode power \"{}\" from power holder attachment (skipping): {}", id, error.message());
						}

					}

					return Pair.of(new PowerHolderAttachment(powers, sources), input);

				});

			}

			else {
				return DataResult.error(() -> "Couldn't decode power holder attachment without registry ops!");
			}

		}

		@Override
		public <T> DataResult<T> encode(PowerHolderAttachment input, DynamicOps<T> ops, T prefix) {

			if (ops instanceof RegistryOps<T> registryOps) {

				RegistryOps<NbtElement> nbtOps = registryOps.withDelegate(NbtOps.INSTANCE);
				ListBuilder<T> listBuilder = ops.listBuilder();

				for (Map.Entry<PowerIdentifier, Power> powerEntry : input.powers.entrySet()) {

					PowerIdentifier id = powerEntry.getKey();
					Power power = powerEntry.getValue();

					Set<Identifier> sources = input.sources.get(id);
					listBuilder.add(Entry.CODEC.encodeStart(registryOps, new Entry(id, power.getType(), sources, power.toNbt(nbtOps))));

				}

				return listBuilder.build(prefix);

			}

			else {
				return DataResult.error(() -> "Couldn't encode power holder attachment without registry ops!");
			}

		}

	};

	public static final PacketCodec<RegistryByteBuf, PowerHolderAttachment> PACKET_CODEC = new PacketCodec<>() {

		@Override
		public PowerHolderAttachment decode(RegistryByteBuf buf) {

			Map<PowerIdentifier, Power> powers = new Object2ObjectOpenHashMap<>();
			Map<PowerIdentifier, Set<Identifier>> sources = new Object2ObjectOpenHashMap<>();

			RegistryOps<NbtElement> nbtOps = buf.getRegistryManager().getOps(NbtOps.INSTANCE);
			int size = buf.readVarInt();

			for (int i = 0; i < size; i++) {

				Entry entry = Entry.PACKET_CODEC.decode(buf);
				PowerIdentifier id = entry.powerId();

				DataResult<Power> powerResult = PowerManager.getAsResult(entry.powerId())
					.flatMap(p -> Power.BASE_CODEC.encodeStart(nbtOps, p)
						.flatMap(nbtElement -> Power.BASE_CODEC.parse(nbtOps, nbtElement)));

				switch (powerResult) {
					case DataResult.Success<Power> success -> {

						Power power = success.value();
						NbtElement powerData = entry.data();

						try {
							power.fromNbt(nbtOps, powerData);
						}

						catch (Exception e) {
							NeoApoli.LOGGER.warn("Power type of power \"{}\" has changed. Skipping data...", id);
						}

						powers.put(id, power);
						sources.put(id, entry.sources());

					}
					case DataResult.Error<Power> error ->
						NeoApoli.LOGGER.warn("Couldn't receive power \"{}\" from power holder attachment (skipping): {}", id, error.message());
				}

			}

			return new PowerHolderAttachment(powers, sources);

		}

		@Override
		public void encode(RegistryByteBuf buf, PowerHolderAttachment value) {

			RegistryOps<NbtElement> nbtOps = buf.getRegistryManager().getOps(NbtOps.INSTANCE);
			var powerEntries = value.powers.entrySet();

			buf.writeVarInt(powerEntries.size());

			for (Map.Entry<PowerIdentifier, Power> powerEntry : powerEntries) {

				PowerIdentifier id = powerEntry.getKey();
				Power power = powerEntry.getValue();

				Set<Identifier> sources = value.sources.get(id);
				Entry.PACKET_CODEC.encode(buf, new Entry(id, power.getType(), sources, power.toNbt(nbtOps)));

			}

		}

	};

	private final Map<PowerIdentifier, Power> powers;
	private final Map<PowerIdentifier, Set<Identifier>> sources;

	public PowerHolderAttachment(Map<PowerIdentifier, Power> powers, Map<PowerIdentifier, Set<Identifier>> sources) {
		this.powers = powers;
		this.sources = sources;
	}

	public PowerHolderAttachment() {
		this(new Object2ObjectOpenHashMap<>(), new Object2ObjectOpenHashMap<>());
	}

	public boolean grantPower(Entity entity, Power power, Identifier source) {

		List<Power> grantedPowers = new ObjectArrayList<>();
		boolean result = this.grantPowerInternal(entity, power, source, grantedPowers::add);

		for (Power grantedPower : grantedPowers) {

			grantedPower.onAdded(entity);
			grantedPower.onGained(entity);

		}

		return result;
	}

	private boolean grantPowerInternal(Entity entity, Power power, Identifier source, Consumer<Power> consumer) {

		PowerIdentifier id = PowerManager.getId(power);
		Set<Identifier> sources = this.sources.computeIfAbsent(id, k -> new ObjectOpenHashSet<>());

		if (sources.contains(source)) {
			return false;
		}

		RegistryOps<NbtElement> nbtOps = entity.getRegistryManager().getOps(NbtOps.INSTANCE);
		power = Power.BASE_CODEC.encodeStart(nbtOps, power)
			.flatMap(nbtElement -> Power.BASE_CODEC.parse(nbtOps, nbtElement))
			.getOrThrow();

		sources.add(source);
		consumer.accept(power);

		this.powers.put(id, power);
		this.sources.put(id, sources);

		if (power instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().values().forEach(subPower -> this.grantPowerInternal(entity, subPower, source, consumer));
		}

		return true;

	}

	public boolean revokePower(Entity entity, Power power, Identifier source) {

		List<PowerIdentifier> revokedPowers = new ObjectArrayList<>();
		boolean result = this.revokePowerInternal(entity, power, source, revokedPowers::add);

		powers.keySet().removeIf(revokedPowers::contains);
		sources.keySet().removeIf(revokedPowers::contains);

		return result;

	}

	private boolean revokePowerInternal(Entity entity, Power power, Identifier source, Consumer<PowerIdentifier> consumer) {

		PowerIdentifier powerId = PowerManager.getId(power);
		Set<Identifier> sources = this.sources.getOrDefault(powerId, new ObjectOpenHashSet<>());

		if (!sources.contains(source)) {
			return false;
		}

		sources.remove(source);

		if (sources.isEmpty() && powers.containsKey(powerId)) {

			power = powers.get(powerId);
			consumer.accept(powerId);

			power.onRemoved(entity);
			power.onLost(entity);

		}

		if (power instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().values().forEach(subPower -> this.revokePowerInternal(entity, subPower, source, consumer));
		}

		return true;

	}

	public void fullSync(Entity entity) {
		entity.setAttached(NeoApoliAttachmentTypes.POWER_HOLDER, this);
	}

	public record Entry(PowerIdentifier powerId, PowerType<?> powerType, Set<Identifier> sources, NbtElement data) {

		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PowerIdentifier.CODEC.fieldOf("id").forGetter(Entry::powerId),
			NeoApoliRegistries.POWER_TYPE.getCodec().fieldOf("type").forGetter(Entry::powerType),
			NeoApoliCodecs.MUTABLE_IDENTIFIER_SET.fieldOf("sources").forGetter(Entry::sources),
			NeoApoliCodecs.NBT_ELEMENT.fieldOf("data").forGetter(Entry::data)
		).apply(instance, Entry::new));

		public static final PacketCodec<RegistryByteBuf, Entry> PACKET_CODEC = PacketCodec.tuple(
			PowerIdentifier.PACKET_CODEC, Entry::powerId,
			PacketCodecs.registryValue(NeoApoliRegistryKeys.POWER_TYPE), Entry::powerType,
			NeoApoliPacketCodecs.MUTABLE_IDENTIFIER_SET, Entry::sources,
			PacketCodecs.UNLIMITED_NBT_ELEMENT, Entry::data,
			Entry::new
		);

	}

}
