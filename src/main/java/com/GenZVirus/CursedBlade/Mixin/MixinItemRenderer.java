package com.GenZVirus.CursedBlade.Mixin;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.GenZVirus.CursedBlade.CursedBlade.CursedBladeStats;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeVertexBuilder;
import net.minecraftforge.client.model.pipeline.LightUtil;

@OnlyIn(Dist.CLIENT)
@Mixin(IForgeVertexBuilder.class)
public interface MixinItemRenderer {
	
	@Overwrite(remap = false)
	default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float[] baseBrightness, float red, float green, float blue, float alpha, int[] lightmapCoords, int overlayCoords, boolean readExistingColor) {
        int[] aint = bakedQuad.getVertexData();
        Vector3i faceNormal = bakedQuad.getFace().getDirectionVec();
        Vector3f normal = new Vector3f((float)faceNormal.getX(), (float)faceNormal.getY(), (float)faceNormal.getZ());
        Matrix4f matrix4f = matrixEntry.getMatrix();
        normal.transform(matrixEntry.getNormal());
        int intSize = DefaultVertexFormats.BLOCK.getIntegerSize();
        int vertexCount = aint.length / intSize;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for(int v = 0; v < vertexCount; ++v) {
                ((Buffer)intbuffer).clear();
                intbuffer.put(aint, v * 8, 8);
                float f = bytebuffer.getFloat(0);
                float f1 = bytebuffer.getFloat(4);
                float f2 = bytebuffer.getFloat(8);
                float cr;
                float cg;
                float cb;
                float ca;
                if (readExistingColor) {
                    float r = (float)(bytebuffer.get(12) & 255) / 255.0F;
                    float g = (float)(bytebuffer.get(13) & 255) / 255.0F;
                    float b = (float)(bytebuffer.get(14) & 255) / 255.0F;
                    float a = (float)(bytebuffer.get(15) & 255) / 255.0F;
                    cr = r * baseBrightness[v] * red;
                    cg = g * baseBrightness[v] * green;
                    cb = b * baseBrightness[v] * blue;
                    ca = a * alpha;
                } else {
                    cr = baseBrightness[v] * red;
                    cg = baseBrightness[v] * green;
                    cb = baseBrightness[v] * blue;
                    ca = alpha;
                }

                int lightmapCoord = applyBakedLighting(lightmapCoords[v], bytebuffer);
                float f9 = bytebuffer.getFloat(16);
                float f10 = bytebuffer.getFloat(20);
                Vector4f pos = new Vector4f(f, f1, f2, 1.0F);
                pos.transform(matrix4f);
                applyBakedNormals(normal, bytebuffer, matrixEntry.getNormal());
                if(bakedQuad.getSprite().getName().getPath().contains("cb_white")) {
                	float cb_red = 0.0F;
                	float cb_green = 0.0F;
                	float cb_blue = 0.0F;
                	if(CursedBladeStats.STATUS.equals("Awakened")) {
                		cb_green = (float) Math.min(0.4F * CursedBladeStats.KILL_COUNTER / 10000.0F, 0.7F);
                		cb_blue = (float) Math.min(0.7F * CursedBladeStats.KILL_COUNTER / 10000.0F, 0.7F);
                	} else {
                		cb_red = (float) Math.min(0.7F * CursedBladeStats.KILL_COUNTER / 1000.0F, 0.7F);
                	}
                	cr = cb_red;
                	cg = cb_green;
                	cb = cb_blue;
                }
                ((IVertexBuilder)this).addVertex(pos.getX(), pos.getY(), pos.getZ(), cr, cg, cb, ca, f9, f10, overlayCoords, lightmapCoord, normal.getX(), normal.getY(), normal.getZ());
            }
        }
    }

    default int applyBakedLighting(int lightmapCoord, ByteBuffer data) {
        int bl = lightmapCoord&0xFFFF;
        int sl = (lightmapCoord>>16)&0xFFFF;
        int offset = LightUtil.getLightOffset(0) * 4; // int offset for vertex 0 * 4 bytes per int
        int blBaked = Short.toUnsignedInt(data.getShort(offset));
        int slBaked = Short.toUnsignedInt(data.getShort(offset + 2));
        bl = Math.max(bl, blBaked);
        sl = Math.max(sl, slBaked);
        return bl | (sl<<16);
    }

    default void applyBakedNormals(Vector3f generated, ByteBuffer data, Matrix3f normalTransform) {
        byte nx = data.get(28);
        byte ny = data.get(29);
        byte nz = data.get(30);
        if (nx != 0 || ny != 0 || nz != 0) {
            generated.set(nx / 127f, ny / 127f, nz / 127f);
            generated.transform(normalTransform);
        }
    }

}
