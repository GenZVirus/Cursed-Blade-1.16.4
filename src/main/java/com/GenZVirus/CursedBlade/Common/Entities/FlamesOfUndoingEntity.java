package com.GenZVirus.CursedBlade.Common.Entities;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Init.ModEntityTypes;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon.CursedBeamDamage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FlamesOfUndoingEntity extends DamagingProjectileEntity {

	public boolean isInvulnerable = true;
	private double damage = 7.0D;
	private LivingEntity shooter;

	public FlamesOfUndoingEntity(EntityType<? extends FlamesOfUndoingEntity> type, World worldIn) {
		super(type, worldIn);
		this.shooter = CursedBlade.CURSED_PLAYER;
	}

	public FlamesOfUndoingEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
		super(ModEntityTypes.FLAMES_OF_UNDOING.get(), shooter, accelX, accelY, accelZ, worldIn);
		this.shooter = shooter;
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	/**
	 * Returns true if the entity is on fire. Used by render to add the fire effect
	 * on rendering.
	 */
	public boolean isBurning() {
		return false;
	}

	public LivingEntity getShooter() {
		return this.shooter;
	}

	/**
	 * Called when this EntityFireball hits a block or entity.
	 */
	protected void onImpact(RayTraceResult result) {
	}

	@Override
	public void tick() {
		if (shooter == null) {
			this.remove();
			return;
		}

		double offset = 0.5D;
		double pitch = shooter.getPitchYaw().x;
		double yaw = shooter.getPitchYaw().y;
		double pitchRadian = pitch * (Math.PI / 180); // X rotation
		double yawRadian = yaw * (Math.PI / 180); // Y rotation
		double newPosX = offset * -Math.sin(yawRadian) * Math.cos(pitchRadian);
		double newPosY = offset * -Math.sin(pitchRadian);
		double newPosZ = offset * Math.cos(yawRadian) * Math.cos(pitchRadian);
		this.rotationPitch = (float) pitch;
		this.rotationYaw = (float) yaw;
		this.setRawPosition(shooter.getPosX() + newPosX, 1.0D + shooter.getPosY() + newPosY, shooter.getPosZ() + newPosZ);
		if (!(this.shooter.getActiveItemStack().getItem() instanceof CursedBladeWeapon)) {
			this.remove();
		}
		if(this.world.isRemote()) return;
		AxisAlignedBB aabb = new AxisAlignedBB(this.getPosition()).grow(150);
		this.rayTraceEntities(shooter, this.getPositionVec(), this.getPositionVec().add(newPosX * 130, newPosY * 130, newPosZ * 130), aabb, (entity) -> {
			return !(entity instanceof FlamesOfUndoingEntity);
		}, 3.5D);
	}

	@Nullable
	public void rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance) {
		World world = shooter.world;
		double d0 = distance;

		for (Entity entity1 : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter)) {
			AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double) entity1.getCollisionBorderSize());
			Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);
			if (axisalignedbb.contains(startVec)) {
				if (d0 >= 0.0D) {
					CursedBladeWeapon.attemptToDamageEntity(entity1, CursedBeamDamage.CURSED_BEAM_DAMAGE, (float) this.getDamage() / 10);
				}
			} else if (optional.isPresent()) {
				Vector3d vector3d1 = optional.get();
				double d1 = entity1.getPositionVec().squareDistanceTo(vector3d1);
				if (d1 < d0 || d0 == 0.0D) {
					if (entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity() && !entity1.canRiderInteract()) {
						if (d0 == 0.0D) {
							CursedBladeWeapon.attemptToDamageEntity(entity1, CursedBeamDamage.CURSED_BEAM_DAMAGE, (float) this.getDamage() / 10);
						}
					} else {
						CursedBladeWeapon.attemptToDamageEntity(entity1, CursedBeamDamage.CURSED_BEAM_DAMAGE, (float) this.getDamage() / 10);
					}
				}
			}
		}
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getDamage() {
		return this.damage;
	}

	@Override
	protected IParticleData getParticle() {
		return ParticleTypes.SOUL_FIRE_FLAME;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this
	 * Entity.
	 */
	public boolean canBeCollidedWith() {
		return false;
	}

	protected boolean isFireballFiery() {
		return false;
	}
}
