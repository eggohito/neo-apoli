package io.github.eggohito.neo_apoli.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.Optional;

public final class InventoryUtil {

	public static Optional<ItemEntity> dropItem(Entity thrower, ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
		return dropItem(thrower, stack, throwRandomly, retainOwnership, 40);
	}

	public static Optional<ItemEntity> dropItem(Entity thrower, ItemStack stack, boolean throwRandomly, boolean retainOwnership, int pickupDelay) {

		if (stack.isEmpty()) {
			return Optional.empty();
		}

		float pitch = thrower.getPitch();
		float yaw = thrower.getYaw();

		Random random = thrower.getRandom();
		double y = thrower.getEyeY() - 0.30000001192092896;

		ItemEntity itemEntity = new ItemEntity(thrower.getWorld(), thrower.getX(), y, thrower.getZ(), stack);
		itemEntity.setPickupDelay(pickupDelay);

		if (retainOwnership) {
			itemEntity.setThrower(thrower);
		}

		float f, g;
		if (throwRandomly) {

			f = random.nextFloat() * 0.5F;
			g = random.nextFloat() * 6.2831855F;

			itemEntity.setVelocity(-MathHelper.sin(g) * f, 0.20000000298023224, MathHelper.cos(g) * f);

		}

		else {

			f =  0.3F;
			g = MathHelper.sin(pitch * 0.017453292F);

			float h = MathHelper.cos(pitch * 0.017453292F);
			float i = MathHelper.sin(yaw * 0.017453292F);
			float j = MathHelper.cos(yaw * 0.017453292F);

			float k = random.nextFloat() * 6.2831855F;
			float l = 0.02F * random.nextFloat();

			itemEntity.setVelocity((-i * h * f) + Math.cos(k) * l, -g * f + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F, (j * h * f) + Math.sin(k) * l);

		}

		return Optional.of(itemEntity);

	}

}
