package net.minecraft.src;

import org.spoutcraft.client.entity.CraftCreeper;

import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAIAttackOnCollide;
import net.minecraft.src.EntityAIAvoidEntity;
import net.minecraft.src.EntityAICreeperSwell;
import net.minecraft.src.EntityAIHurtByTarget;
import net.minecraft.src.EntityAILookIdle;
import net.minecraft.src.EntityAINearestAttackableTarget;
import net.minecraft.src.EntityAISwimming;
import net.minecraft.src.EntityAIWander;
import net.minecraft.src.EntityAIWatchClosest;
import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityOcelot;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.Item;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class EntityCreeper extends EntityMob {

	int timeSinceIgnited;
	int lastActiveTime;

	public EntityCreeper(World par1World) {
		super(par1World);
		this.texture = "/mob/creeper.png";
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAICreeperSwell(this));
		this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 0.25F, 0.3F));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 0.25F, false));
		this.tasks.addTask(5, new EntityAIWander(this, 0.2F));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.field_48105_bU.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, false));
		this.field_48105_bU.addTask(2, new EntityAIHurtByTarget(this, false));
		//Spout start
		this.spoutEntity = new CraftCreeper(this);
		//Spout end
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 20;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte)-1));
		this.dataWatcher.addObject(17, Byte.valueOf((byte)0));
	}

	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		if(this.dataWatcher.getWatchableObjectByte(17) == 1) {
			par1NBTTagCompound.setBoolean("powered", true);
		}

	}

	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		this.dataWatcher.updateObject(17, Byte.valueOf((byte)(par1NBTTagCompound.getBoolean("powered")?1:0)));
	}

	public void onUpdate() {
		if(this.isEntityAlive()) {
			this.lastActiveTime = this.timeSinceIgnited;
			int var1 = this.getCreeperState();
			if(var1 > 0 && this.timeSinceIgnited == 0) {
				this.worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 0.5F);
			}

			this.timeSinceIgnited += var1;
			if(this.timeSinceIgnited < 0) {
				this.timeSinceIgnited = 0;
			}

			if(this.timeSinceIgnited >= 30) {
				this.timeSinceIgnited = 30;
				if(!this.worldObj.isRemote) {
					if(this.getPowered()) {
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 6.0F);
					} else {
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
					}

					this.setEntityDead();
				}
			}
		}

		super.onUpdate();
	}

	protected String getHurtSound() {
		return "mob.creeper";
	}

	protected String getDeathSound() {
		return "mob.creeperdeath";
	}

	public void onDeath(DamageSource par1DamageSource) {
		super.onDeath(par1DamageSource);
		if(par1DamageSource.getEntity() instanceof EntitySkeleton) {
			this.dropItem(Item.record13.shiftedIndex + this.rand.nextInt(10), 1);
		}

	}

	public boolean attackEntityAsMob(Entity par1Entity) {
		return true;
	}

	public boolean getPowered() {
		return this.dataWatcher.getWatchableObjectByte(17) == 1;
	}

	//Spout start
	public void setPowered(boolean power) {
		this.dataWatcher.updateObject(17, power ? 1 : 0);
	}
	//Spout end

	public float setCreeperFlashTime(float par1) {
		return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * par1) / 28.0F;
	}

	protected int getDropItemId() {
		return Item.gunpowder.shiftedIndex;
	}

	public int getCreeperState() {
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	public void setCreeperState(int par1) {
		this.dataWatcher.updateObject(16, Byte.valueOf((byte)par1));
	}

	public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
		super.onStruckByLightning(par1EntityLightningBolt);
		this.dataWatcher.updateObject(17, Byte.valueOf((byte)1));
	}
}
