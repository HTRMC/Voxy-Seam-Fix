package me.htrmc.voxyseamfix;

import java.util.HashSet;
import java.util.Set;

public final class ShaderPatcher {
    private ShaderPatcher() {}

    private static final Set<String> WARNED = new HashSet<>();

    private static String glsl(String... lines) {
        return String.join("\n", lines);
    }

    private static final String UNIFORM_ANCHOR = glsl(
            "    vec3 cameraSubPos;",
            "};");

    private static final String UNIFORM_PATCH = glsl(
            "    vec3 cameraSubPos;",
            "    float voxyseamfix_fluidSurfaceY;",
            "};");

    private static final String MODEL_ANCHOR = glsl(
            "bool modelIsShaded(BlockModel model) {",
            "    return ((model.flagsA)&8u) != 0;",
            "}");

    private static final String MODEL_PATCH = glsl(
            "bool modelIsShaded(BlockModel model) {",
            "    return ((model.flagsA)&8u) != 0;",
            "}",
            "",
            "bool voxyseamfix_modelIsFluid(BlockModel model) {",
            "    return ((model.flagsA)&16u) != 0;",
            "}");

    private static final String QUAD_ANCHOR = glsl(
            "    vec3 quadStart = extractPos(rawQuad);",
            "    float depthOffset = extractFaceIndentation(faceData);",
            "    quadStart += swizzelDataAxis(face>>1, vec3(faceSize.xz, mix(depthOffset, 1-depthOffset, float(face&1u))));",
            "",
            "    quad.lodScale = lodScale;",
            "    quad.axis = face>>1;",
            "    quad.basePoint = (quadStart*lodScale)+vec3(baseSection<<5);");

    private static final String QUAD_PATCH = glsl(
            "    vec3 voxyseamfix_rawPos = extractPos(rawQuad);",
            "    vec3 quadStart = voxyseamfix_rawPos;",
            "    //An indentation is a depth in blocks (a fluid's top face is 14/16), not a fraction of a voxel, so",
            "    //divide out the lod scale before it gets multiplied back in. Otherwise the sea sits",
            "    //0.125*lodScale low, by a different amount on every level.",
            "    float voxyseamfix_indentation = extractFaceIndentation(faceData);",
            "    float depthOffset = voxyseamfix_indentation/lodScale;",
            "    quadStart += swizzelDataAxis(face>>1, vec3(faceSize.xz, mix(depthOffset, 1-depthOffset, float(face&1u))));",
            "",
            "    quad.lodScale = lodScale;",
            "    quad.axis = face>>1;",
            "    quad.basePoint = (quadStart*lodScale)+vec3(baseSection<<5);",
            "",
            "    //Mipping never lets air win, so a fluid surface rounds up to the top of whichever voxel straddles",
            "    //it and the sea ends up higher on every coarser level. Pin those faces to the one real plane. The",
            "    //test is on the voxel top rather than basePoint, because basePoint already has the indentation",
            "    //taken off it and the voxel is what the mip actually rounded. Every comparison against a NaN",
            "    //voxyseamfix_fluidSurfaceY is false, which is how the pin gets disabled.",
            "    if (face == 1u && voxyseamfix_modelIsFluid(model)) {",
            "        float voxyseamfix_voxelTop = (voxyseamfix_rawPos.y + 1.0)*lodScale + float(baseSection.y<<5);",
            "        if (voxyseamfix_voxelTop >= voxyseamfix_fluidSurfaceY && voxyseamfix_voxelTop - lodScale < voxyseamfix_fluidSurfaceY) {",
            "            quad.basePoint.y = voxyseamfix_fluidSurfaceY - voxyseamfix_indentation;",
            "        }",
            "    }");

    public static String patch(String id, String source) {
        if (!VoxySeamFix.config().enabled) {
            return source;
        }

        boolean hasUniform = source.contains(UNIFORM_ANCHOR);
        boolean hasModel = source.contains(MODEL_ANCHOR);
        boolean hasQuad = source.contains(QUAD_ANCHOR);

        if (!hasQuad && source.contains("void setupQuad(")) {
            warnOnce("quad", "voxy's setupQuad no longer matches our anchor, fluid surfaces are NOT being pinned."
                    + " This build of voxy-seam-fix is out of date for this voxy version.");
        }

        if (!hasUniform && !hasModel && !hasQuad) {
            return source;
        }

        if (hasQuad && !(hasUniform && hasModel)) {
            warnOnce("partial", id + " has setupQuad but not the uniform block and BlockModel helpers,"
                    + " skipping the pin to avoid emitting a shader that will not compile");
            hasQuad = false;
        }

        if (hasUniform) {
            source = source.replace(UNIFORM_ANCHOR, UNIFORM_PATCH);
        }
        if (hasModel) {
            source = source.replace(MODEL_ANCHOR, MODEL_PATCH);
        }
        if (hasQuad) {
            source = source.replace(QUAD_ANCHOR, QUAD_PATCH);
            VoxySeamFix.LOGGER.info("voxy-seam-fix: pinned fluid surfaces in {}", id);
        }
        return source;
    }

    private static void warnOnce(String key, String message) {
        if (WARNED.add(key)) {
            VoxySeamFix.LOGGER.warn("voxy-seam-fix: {}", message);
        }
    }
}
