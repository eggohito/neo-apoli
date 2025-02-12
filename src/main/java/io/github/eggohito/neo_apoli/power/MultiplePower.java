package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.Map;

public class MultiplePower extends Power {

	private static final Codec<String> SUB_POWER_NAME_CODEC = PrimitiveCodec.STRING.validate(MultiplePower::validateSubPowerName);

	private static final Codec<Map<String, Power>> SUB_POWERS_CODEC = CodecUtil.filteredUnboundedMap(SUB_POWER_NAME_CODEC, Power.BASE_CODEC, "type", "name", "description", "hidden").validate(
		subPowers -> {

			for (Map.Entry<String, Power> subPower : subPowers.entrySet()) {

				if (subPower.getValue() instanceof MultiplePower) {
					return DataResult.error(() -> "The sub-power \"" + subPower.getKey() + "\" is using the \"" + NeoApoliRegistries.POWER_TYPE.getId(PowerTypes.MULTIPLE) + "\" power type, which is not allowed!");
				}

			}

			return DataResult.success(subPowers);

		}
	);

	public static final MapCodec<MultiplePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).and(
		MapCodec.assumeMapUnsafe(SUB_POWERS_CODEC).forGetter(MultiplePower::getSubPowers)
	).apply(instance, MultiplePower::new));

	@SuppressWarnings("unchecked")
	public static final PacketCodec<RegistryByteBuf, MultiplePower> PACKET_CODEC = createCommonPacketCodec(
		(buf, multiplePower) -> {

			Map<String, Power> subPowers = multiplePower.getSubPowers();
			buf.writeVarInt(subPowers.size());

			subPowers.forEach((name, subPower) -> {

				PowerType<Power> subPowerType = (PowerType<Power>) subPower.getType();
				RegistryKey<PowerType<?>> subPowerTypeId = NeoApoliRegistries.POWER_TYPE.getKey(subPowerType).orElseThrow(() -> new IllegalStateException("Sub-power \"" + name + "\" has a power type that isn't registered in the power type registry!"));

				buf.writeString(name);
				buf.writeRegistryKey(subPowerTypeId);

				subPowerType.packetCodec().encode(buf, subPower);

			});

		},
		(buf, metadata) -> {

			Map<String, Power> subPowers = new Object2ObjectOpenHashMap<>();
			int size = buf.readVarInt();

			for (int i = 0; i < size; i++) {

				String name = buf.readString();
				RegistryKey<PowerType<?>> subPowerTypeKey = buf.readRegistryKey(NeoApoliRegistryKeys.POWER_TYPE);

				PowerType<Power> subPowerType = (PowerType<Power>) NeoApoliRegistries.POWER_TYPE.getValueOrThrow(subPowerTypeKey);
				Power power = subPowerType.packetCodec().decode(buf);

				subPowers.put(name, power);

			}

			return new MultiplePower(metadata, subPowers);

		}
	);

	private final Map<String, Power> subPowers;

	public MultiplePower(Metadata metadata, Map<String, Power> subPowers) {
		super(metadata);
		this.subPowers = subPowers;
	}

	@Override
	public PowerType<? extends Power> getType() {
		return PowerTypes.MULTIPLE;
	}

	public Map<String, Power> getSubPowers() {
		return subPowers;
	}

	public static DataResult<String> validateSubPowerName(String name) {

		if (name.isEmpty()) {
			return DataResult.error(() -> "Empty sub-power names are not allowed!");
		}

		else if (Identifier.isPathValid(name)) {
			return DataResult.success(name);
		}

		else {
			return DataResult.error(() -> "Non [a-z0-9/._-] character in sub-power name: " + name);
		}

	}

}
