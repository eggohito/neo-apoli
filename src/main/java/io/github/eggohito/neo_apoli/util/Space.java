package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public enum Space {

	WORLD,
	LOCAL,
	LOCAL_HORIZONTAL,
	LOCAL_HORIZONTAL_NORMALIZED,
	VELOCITY,
	VELOCITY_NORMALIZED,
	VELOCITY_HORIZONTAL,
	VELOCITY_HORIZONTAL_NORMALIZED;

	public static final Codec<Space> CODEC = CodecUtil.enumType(Space.class);
	public static final StreamCodec<ByteBuf, Space> STREAM_CODEC = StreamCodecUtil.enumType(Space.class);

	/**
	 *
	 * 	<p>Provides the matrix transforms from the base specified by the input vector to the cardinal base. The input
	 * 	vector is the Z (forward) axis of the base, while the calculated X axis is orthogonal to the "left" of the Z.</p>
	 *
	 * 	<p>The Y is such as Z is the cross product of X and Y. If the input vector were to be vertical, the yaw is used
	 * 	to infer the X and Y vectors of the base.</p>
	 *
	 * 	<p>After determining the vectors of the base, it builds the transformation matrix by laying each into a column
	 * 	(if you consider vectors as being in columns for multiplications.)</p>
	 *
	 *  @param vector the input vector the base is inferred from (forward vector of local space)
	 *  @param yaw the yaw of local space
	 * 	@author Alluysl
	 *  @author Apace
	 *  @return the transformation matrix from local to global space
	 */
	private static Matrix3f getBaseTransformMatrixFromNormalizedDirectionVector(Vector3f vector, float yaw) {

		float xX, xZ;	// X vector
		float zX = 0.0F, zY = vector.y(), zZ = 0.0F; // Z vector

		if (Math.abs(zY) != 1.0F) { // Z not vertical, can infer X from it

			// Z
			zX = vector.x();
			zZ = vector.z();

			//	X (orthogonal to the projection of Z on the global XZ plane
			xX = vector.z();
			xZ = -vector.x();

			//	Normalize X
			float xFactor = (float) (1 / Math.sqrt(xX * xX + xZ * xZ));

			xX *= xFactor;
			xZ *= xFactor;

		}

		else {

			// If the orientation vector points straight up or down, use the yaw to determine the X vector (it's "on the left".)
			// The pitch doesn't affect the X vector as it's a rotation around the same vector;

			float trigonometricYaw = -yaw * Mth.DEG_TO_RAD; // pi / 180 = 0.0174532925

			xX = Mth.cos(trigonometricYaw);
			xZ = -Mth.sin(trigonometricYaw);

		}

		Matrix3f result = new Matrix3f();

		// X
		result.set(0, 0, xX);
		result.set(1, 0, 0.0f); // X vector is horizontal, set its Y component (a10 (mathematically a21)) to 0
		result.set(2, 0, xZ);

		// Y (cross product of Z and X, simplified by the fact that X has a Y component of 0
		result.set(0, 1, zY * xZ);
		result.set(1, 1, zZ * xX - zX * xZ);
		result.set(2, 1, -zY * xX);

		// Z
		result.set(0, 2, zX);
		result.set(1, 2, zY);
		result.set(2, 2, zZ);

		return result;

	}

	/**
	 * 	<p>Transforms a vector from local space to global space. The base inferred from its forward vector is orthogonal.</p>
	 *
	 * 	@param baseForwardVector the base's forward (Z) vector
	 * 	@param vector the vector to transform
	 * 	@param baseYaw the yaw of the base (used in case the forward vector lacks information to infer the base)
	 * 	@param normalizeBase whether to normalize the base, if so all three vectors of the base will be normalized, otherwise they'll all have the length of the input forward vector
	 *	@author Alluysl
	 */
	public static void transformVectorToBase(Vector3f baseForwardVector, Vector3f vector, float baseYaw, boolean normalizeBase) {

		float baseScale = baseForwardVector.length();
		if (baseScale <= 0.007F) {	// Tweak value if too high, may be a bit too aggressive
			vector.zero();
		}

		else {

			Vector3f normalizedBase = baseForwardVector.normalize();	// The function called below assumes the base is normalized to simplify calculations (Y calculated as cross product of Z and X guaranteed to be normalized if X and Z are normalized)
			Matrix3f transformMatrix = getBaseTransformMatrixFromNormalizedDirectionVector(normalizedBase, baseYaw);

			if (!normalizeBase) {	// If the base wasn't supposed to get normalized, re-scale to compensate for the prior normalization
				transformMatrix.scale(baseScale, baseScale, baseScale);
			}

			vector.mulTranspose(transformMatrix);	// Matrix multiplication, vector is now in the new base :D

		}

	}

	/**
	 *	<p>Transforms a vector from the local space of this instance to global space. The "local" space may be world
	 * 	 *  space (no transformation), relative to the entity's rotation, or the entity's velocity.</p>
	 *
	 *	@param vector the vector to transform
	 *	@param entity the entity to align the local space to
	 	@author apace100, Alluysl
	 */
	public void globalize(Vector3f vector, Entity entity) {
		globalize(vector, entity.getKnownMovement().toVector3f(), new Vector2f(entity.getXRot(), entity.getYRot()));
	}

	/**
	 *  <p>Transforms a vector from the local space of this instance to global space. The "local" space may be world
	 *  space (no transformation), relative to a specific rotation (pitch/yaw), or a specific velocity.</p>
	 *
	 *  @param vector the vector to transform
	 *  @param velocity the velocity
	 *  @param rotation2d the pitch and yaw
	 *  @author apace100, Alluysl, eggohito
	 */
	public void globalize(Vector3f vector, Vector3f velocity, Vector2f rotation2d) {

		Vector3f baseForwardVector;

		switch (this) {
			case LOCAL, LOCAL_HORIZONTAL, LOCAL_HORIZONTAL_NORMALIZED -> {

				baseForwardVector = calculate3dRotation(rotation2d.x(), rotation2d.y());

				if (this != LOCAL) {
					baseForwardVector.setComponent(1, 0.0F);
				}

				transformVectorToBase(baseForwardVector, vector, rotation2d.y(), this == LOCAL_HORIZONTAL_NORMALIZED);

			}
			case VELOCITY, VELOCITY_NORMALIZED, VELOCITY_HORIZONTAL, VELOCITY_HORIZONTAL_NORMALIZED -> {

				baseForwardVector = velocity;

				if (this == VELOCITY_HORIZONTAL || this == VELOCITY_HORIZONTAL_NORMALIZED) {
					baseForwardVector.setComponent(1, 0.0F);
				}

				transformVectorToBase(baseForwardVector, vector, rotation2d.y(), this == VELOCITY_NORMALIZED || this == VELOCITY_HORIZONTAL_NORMALIZED);

			}
		}

	}

	/**
	 * @param xRot the pitch
	 * @param yRot the yaw
	 * @return a {@linkplain Vector3f 3D rotation vector}
	 */
	public static Vector3f calculate3dRotation(float xRot, float yRot) {

		float f = xRot * Mth.DEG_TO_RAD;
		float g = -yRot * Mth.DEG_TO_RAD;

		float z = Mth.cos(g);
		float x = Mth.sin(g);

		float horizontal = Mth.cos(f);
		float vertical = Mth.sin(f);

		return new Vector3f(x * horizontal, -vertical, z * horizontal);

	}

}
