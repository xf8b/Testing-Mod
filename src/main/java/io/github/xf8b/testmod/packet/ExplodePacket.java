package io.github.xf8b.testmod.packet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ExplodePacket {
    private static BlockPos pos;
    private static BlockRayTraceResult blockRayTraceResult;

    public ExplodePacket(BlockPos pos, BlockRayTraceResult blockRayTraceResult) {
        ExplodePacket.pos = pos;
        ExplodePacket.blockRayTraceResult = blockRayTraceResult;
    }

    public static ExplodePacket decode(final PacketBuffer buffer) {
        ExplodePacket.pos = buffer.readBlockPos();
        ExplodePacket.blockRayTraceResult = buffer.readBlockRay();
        return new ExplodePacket(pos, blockRayTraceResult);
    }

    public static void encode(final PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBlockRay(blockRayTraceResult);
    }

    public static void handle(final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity player = ctx.get().getSender();
            final World world = player.world;

            player.markPlayerActive();

            BlockPos blockRayTraceResultPos = blockRayTraceResult.getPos();

            if (world.isAreaLoaded(pos, 64)) {
                world.createExplosion(EntityType.TNT.create(world), blockRayTraceResultPos.getX(), blockRayTraceResultPos.getY(), blockRayTraceResultPos.getZ(), 4.0F, Explosion.Mode.BREAK);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}