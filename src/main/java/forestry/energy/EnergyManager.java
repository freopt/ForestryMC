package forestry.energy;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.config.Config;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.energy.compat.EnergyStorageWrapper;
import forestry.energy.compat.mj.MjConnectorWrapper;
import forestry.energy.compat.mj.MjHelper;
import forestry.energy.compat.mj.MjPassiveProviderWrapper;
import forestry.energy.compat.mj.MjReadableWrapper;
import forestry.energy.compat.mj.MjReceiverWrapper;
import forestry.energy.compat.mj.MjRedstoneReceiverWrapper;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.IMjRedstoneReceiver;

public class EnergyManager extends EnergyStorage implements IStreamable, INbtReadable, INbtWritable {
	private EnergyTransferMode externalMode = EnergyTransferMode.BOTH;
	@Nullable
	private Consumer<Integer> changeHandler;

	public EnergyManager(int maxTransfer, int capacity) {
		super(EnergyHelper.scaleForDifficulty(capacity), EnergyHelper.scaleForDifficulty(maxTransfer), EnergyHelper.scaleForDifficulty(maxTransfer));
	}

	public void setChangeHandler(@Nullable Consumer<Integer> changeHandler) {
		this.changeHandler = changeHandler;
	}

	public void setExternalMode(EnergyTransferMode externalMode) {
		this.externalMode = externalMode;
	}

	public EnergyTransferMode getExternalMode() {
		return externalMode;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		final int energy;
		if (nbt.hasKey("EnergyManager")) { // legacy
			NBTTagCompound energyManagerNBT = nbt.getCompoundTag("EnergyManager");
			NBTTagCompound energyStorageNBT = energyManagerNBT.getCompoundTag("EnergyStorage");
			energy = energyStorageNBT.getInteger("Energy");
		} else {
			energy = nbt.getInteger("Energy");
		}

		setEnergyStored(energy);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("Energy", energy);
		return nbt;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeVarInt(this.energy);
	}

	@Override
	public void readData(PacketBufferForestry data) {
		int energyStored = data.readVarInt();
		setEnergyStored(energyStored);
	}

	public int getMaxEnergyReceived() {
		return this.maxReceive;
	}

	/**
	 * Drains an amount of energy, due to decay from lack of work or other factors
	 */
	public void drainEnergy(int amount) {
		setEnergyStored(energy - amount);
	}

	/**
	 * Creates an amount of energy, generated by engines
	 */
	public void generateEnergy(int amount) {
		setEnergyStored(energy + amount);
	}

	public void setEnergyStored(int energyStored) {
		this.energy = energyStored;
		if (this.energy > capacity) {
			this.energy = capacity;
			if (changeHandler != null) {
				changeHandler.accept(energy);
			}
		} else if (this.energy < 0) {
			this.energy = 0;
			if (changeHandler != null) {
				changeHandler.accept(0);
			}
		}
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int value = super.extractEnergy(maxExtract, simulate);
		if (changeHandler != null && value != 0 && !simulate) {
			changeHandler.accept(energy);
		}
		return value;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int value = super.receiveEnergy(maxReceive, simulate);
		if (changeHandler != null && value != 0 && !simulate) {
			changeHandler.accept(energy);
		}
		return value;
	}

	public boolean hasCapability(Capability<?> capability) {
		return Config.enableRF && capability == CapabilityEnergy.ENERGY ||
			Config.enableMJ && hasMjCapability(capability);
	}

	private boolean hasMjCapability(Capability<?> capability) {
		return capability == MjHelper.CAP_READABLE ||
			capability == MjHelper.CAP_CONNECTOR ||
			capability == MjHelper.CAP_PASSIVE_PROVIDER && externalMode.canExtract() ||
			capability == MjHelper.CAP_REDSTONE_RECEIVER && externalMode.canReceive() ||
			capability == MjHelper.CAP_RECEIVER && externalMode.canReceive();
	}

	@Nullable
	public <T> T getCapability(Capability<T> capability) {
		if (!hasCapability(capability)) {
			return null;
		}
		if (capability == CapabilityEnergy.ENERGY) {
			IEnergyStorage energyStorage = new EnergyStorageWrapper(this, externalMode);
			return CapabilityEnergy.ENERGY.cast(energyStorage);
		}  else if (MjHelper.isMjCapability(capability)) {
			Capability<IMjConnector> mjConnector = MjHelper.CAP_CONNECTOR;
			Capability<IMjPassiveProvider> mjPassiveProvider = MjHelper.CAP_PASSIVE_PROVIDER;
			Capability<IMjReadable> mjReadable = MjHelper.CAP_READABLE;
			Capability<IMjReceiver> mjReceiver = MjHelper.CAP_RECEIVER;
			Capability<IMjRedstoneReceiver> mjRedstoneReceiver = MjHelper.CAP_REDSTONE_RECEIVER;

			if (capability == mjPassiveProvider && externalMode.canExtract()) {
				return mjPassiveProvider.cast(new MjPassiveProviderWrapper(this));
			} else if (capability == mjReceiver && externalMode.canReceive()) {
				return mjReceiver.cast(new MjReceiverWrapper(this));
			} else if (capability == mjRedstoneReceiver && externalMode.canReceive()) {
				return mjRedstoneReceiver.cast(new MjRedstoneReceiverWrapper(this));
			} else if (capability == mjReadable) {
				return mjReadable.cast(new MjReadableWrapper(this));
			} else if (capability == mjConnector) {
				return mjConnector.cast(new MjConnectorWrapper(this));
			}
		}
		return null;
	}

	public int calculateRedstone() {
		return MathHelper.floor(((float) energy / (float) capacity) * 14.0F) + (energy > 0 ? 1 : 0);
	}

}
