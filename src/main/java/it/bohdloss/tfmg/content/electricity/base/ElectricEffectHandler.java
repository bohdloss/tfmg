package it.bohdloss.tfmg.content.electricity.base;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.RotationIndicatorParticleData;
import it.bohdloss.tfmg.base.spark.ElectricSparkParticle;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ElectricEffectHandler {
    int shortedTime;
    float shortedEffect;
    int particleSpawnCountdown;
    IElectric ebe;

    public ElectricEffectHandler(IElectric ebe) {
        this.ebe = ebe;
    }

    public void tick() {
        Level world = ebe.getLevel();

        if (world.isClientSide) {
            if (shortedTime > 0) {
                if (--shortedTime == 0) {
                    if (ebe.isShortCircuited()) {
                        shortedEffect = 1;
                        spawnEffect(new ElectricSparkParticle.Data(), 0.2f, 5);
                    } else {
                        shortedEffect = -1;
                        spawnEffect(new ElectricSparkParticle.Data(), .075f, 2);
                    }
                }
            }

            if (shortedEffect != 0) {
                shortedEffect -= shortedEffect * .1f;
                if (Math.abs(shortedEffect) < 1 / 128f) {
                    shortedEffect = 0;
                }
            }

        } else if (particleSpawnCountdown > 0) {
            if (--particleSpawnCountdown == 0) {
                spawnPowerIndicators();
            }
        }
    }

    public void queuePowerIndicators() {
        particleSpawnCountdown = 2;
    }

    public void spawnEffect(ParticleOptions particle, float maxMotion, int amount) {
        Level world = ebe.getLevel();
        if (world == null) {
            return;
        }
        if (!world.isClientSide) {
            return;
        }
        RandomSource r = world.random;
        for (int i = 0; i < amount; i++) {
            Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, r, maxMotion);
            Vec3 position = VecHelper.getCenterOf(ebe.getBlockPos());
            world.addParticle(particle, position.x, position.y, position.z, motion.x, motion.y, motion.z);
        }
    }

    public void spawnPowerIndicators() {
        float voltage = ebe.getVoltage();
        if (voltage == 0) {
            return;
        }

        BlockState state = ebe.getBlockState();
        Block block = state.getBlock();
        if (!(block instanceof IElectricBlock kb)) {
            return;
        }

        float radius1 = kb.getElectricParticleInitialRadius();
        float radius2 = kb.getElectricParticleTargetRadius();

        Direction.Axis axis = null;//kb.getRotationAxis(state);
        BlockPos pos = ebe.getBlockPos();
        Level world = ebe.getLevel();
        if (axis == null) {
            return;
        }
        if (world == null) {
            return;
        }

        Vec3 vec = VecHelper.getCenterOf(pos);
        IRotate.SpeedLevel speedLevel = IRotate.SpeedLevel.of(voltage);
        int color = speedLevel.getColor();
        int particleSpeed = speedLevel.getParticleSpeed();
        particleSpeed *= Math.signum(voltage);

        if (world instanceof ServerLevel) {
            RotationIndicatorParticleData particleData =
                    new RotationIndicatorParticleData(color, particleSpeed, radius1, radius2, 10, axis);
            ((ServerLevel) world).sendParticles(particleData, vec.x, vec.y, vec.z, 20, 0, 0, 0, 1);
        }
    }

    public void triggerShortCircuitEffect() {
        shortedTime = shortedTime == 0 ? 2 : 0;
    }
}
