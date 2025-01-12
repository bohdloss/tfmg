package com.drmangotea.tfmg.mixin;


import com.drmangotea.tfmg.content.electricity.base.IElectric;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer;
import com.simibubi.create.content.equipment.goggles.IProxyHoveringInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GoggleOverlayRenderer.class)
public class GoggleOverlayRendererMixin {

    /**
     * handles multimeter tooltips
     */

    //@Inject(at = @At("HEAD"), method = "renderOverlay", cancellable = true, remap = false)
    //private static void renderOverlay(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height, CallbackInfo ci) {
    //    Minecraft mc = Minecraft.getInstance();
    //    ClientLevel world = mc.level;
    //    HitResult objectMouseOver = mc.hitResult;
    //    BlockHitResult result = (BlockHitResult) objectMouseOver;
//
    //    BlockPos pos = result.getBlockPos();
//
    //    pos = proxiedOverlayPosition(world, pos);
//
    //    BlockEntity be = world.getBlockEntity(pos);
    //
//
    //    if(be instanceof IElectric){
    //        if(!(mc.player.getMainHandItem().is(TFMGItems.MULTIMETER.get())))
    //            ci.cancel();
    //    }
//
    //}
    @Shadow
    public static BlockPos proxiedOverlayPosition(Level level, BlockPos pos) {
        BlockState targetedState = level.getBlockState(pos);
        if (targetedState.getBlock() instanceof IProxyHoveringInformation proxy)
            return proxy.getInformationSource(level, pos, targetedState);
        return pos;
    }
}
