package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.CodecUtil;
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

	public static final MapCodec<MultiplePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addMetadataFields(instance).and(
		MapCodec.assumeMapUnsafe(SUB_POWERS_CODEC).forGetter(MultiplePower::getSubPowers)
	).apply(instance, MultiplePower::new));

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
