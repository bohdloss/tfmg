package it.bohdloss.tfmg.base.spark;

import com.simibubi.create.content.equipment.bell.BasicParticleData;
import com.simibubi.create.content.equipment.bell.CustomRotationParticle;
import it.bohdloss.tfmg.registry.TFMGParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

public class ElectricSparkParticle extends CustomRotationParticle {
    private final SpriteSet animatedSprite;
    protected int startTicks;
    protected int endTicks;
    protected int numLoops;
    protected int startFrames = 17;
    protected int loopFrames = 16;
    protected int endFrames = 20;
    protected int totalFrames = 53;

    public ElectricSparkParticle(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz,
                                 SpriteSet spriteSet, ParticleOptions data) {
        super(worldIn, x, y, z, spriteSet, 0);
        this.animatedSprite = spriteSet;
        this.quadSize = 0.5f;
        this.setSize(this.quadSize, this.quadSize);

        this.loopLength = loopFrames + (int) (this.random.nextFloat() * 5f - 4f);
        this.startTicks = startFrames + (int) (this.random.nextFloat() * 5f - 4f);
        this.endTicks = endFrames + (int) (this.random.nextFloat() * 5f - 4f);
        this.numLoops = (int) (1f + this.random.nextFloat() * 2f);

        this.setFrame(0);
        this.mirror = this.random.nextBoolean();
    }

    public void setFrame(int frame) {
        if (frame >= 0 && frame < totalFrames)
            setSprite(animatedSprite.get(frame, totalFrames));
    }

    public static class Data extends BasicParticleData<ElectricSparkParticle> {
        @Override
        public @NotNull IBasicParticleFactory<ElectricSparkParticle> getBasicFactory() {
            return (worldIn, x, y, z, vx, vy, vz, spriteSet) -> new ElectricSparkParticle(worldIn, x, y, z, vx, vy, vz,
                    spriteSet, this);
        }
        @Override
        public @NotNull ParticleType<?> getType() {
            return TFMGParticleTypes.ELECTRIC_SPARK.get();
        }
    }
}
