package com.GenZVirus.CursedBlade.Common.Entities;

import java.util.List;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Init.ModEntityTypes;
import com.GenZVirus.CursedBlade.Common.Item.CursedBladeWeapon.CursedDamage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FlamesOfUndoingEntity extends DamagingProjectileEntity {

	public boolean isInvulnerable = true;
	public int timeLife = 40;
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
		super.onImpact(result);
		if (!this.world.isRemote) {
			if (result.getType() == RayTraceResult.Type.ENTITY) {
				Entity entity = ((EntityRayTraceResult) result).getEntity();
				entity.attackEntityFrom(CursedDamage.CURSED_DAMAGE, (float) this.getDamage());
			}

			AxisAlignedBB CUBE_BOX = VoxelShapes.fullCube().getBoundingBox();
			Vector3d pos_offset = new Vector3d(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
			AxisAlignedBB aabb = CUBE_BOX.offset(pos_offset).grow(3);
			List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this.func_234616_v_(), aabb);
			for (Entity entity : list) {
				if (entity instanceof LivingEntity)
					entity.attackEntityFrom(CursedDamage.CURSED_DAMAGE, (float) this.getDamage());
			}
		}
	}

	@Override
	public void tick() {
		int x1 = shooter.getPosition().getX();
		int y1 = shooter.getPosition().getY();
		int z1 = shooter.getPosition().getZ();
		int x2 = this.getPosition().getX();
		int y2 = this.getPosition().getY();
		int z2 = this.getPosition().getZ();

		if (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1)) > 150) {
			System.out.println("removed");
			this.remove();
		}
		super.tick();
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
