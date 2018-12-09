package com.ilufang;

import amidst.Util;
import amidst.minecraft.LocalMinecraftInterface;
import amidst.minecraft.Minecraft;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Main {
    private static final int ARG_MCJARFILE = 1;
    private static final int ARG_SEED = 2;
    private static final int ARG_CHUNKX = 3;
    private static final int ARG_CHUNKY = 4;

    private static LocalMinecraftInterface mc;

    public static void main(String[] args) {
        if (args.length<5) {
            System.err.println("Invalid Args. Usage: [jarFile] [seed] [chunkX] [chunkY]");
            //return;
        }

        File lockFile = new File("AMIDST_LOCK");
        if (lockFile.exists()) {
            System.err.println("Concurrent modification.");
            //return;
        }

        //File mcJar = new File(args[ARG_MCJARFILE]);
        File mcJar = new File("/Users/ilufang/Library/Application Support/minecraft/versions/1.8.1/1.8.1.jar");
        try {
            lockFile.createNewFile();
            Util.setMinecraftDirectory();
            mc = new LocalMinecraftInterface(new Minecraft(mcJar));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load Minecraft jar.");
            lockFile.delete();
            return;
        }
        //mc.createWorld(Long.parseLong(args[ARG_SEED]), "default", "");
        mc.createWorld(-8221753507381494678L, "default", "");


        // Create Biomes
        // Stored in bigchunks of 1024x1024. <-- 64x64 Minecraft Chunk
        // Generate 8x8 bigchunks at one Java runtime
        // Fileformat:
        // Part 1: 1024x1024 Bytes of UInt8 Biome data for all blocks
        // Part 2: 1 Byte for Number of slime chunks, followed by sets of 2 UInt8 for Relative Chunk Coordinates
        // Part 3: 1 Byte for Number of villages, followed by sets of 3 UInt8 for Relative Chunk Coordinates (2byte) and Coordinate in Chunk (1byte)
        // Part 4-8: Similar calculation for Witch Hut, Desert Temple, Jungle Temple, Stronghold, and Ocean Monument

        //int beginX = Integer.parseInt(args[ARG_CHUNKX]);
        //int beginY = Integer.parseInt(args[ARG_CHUNKY]);
        int beginX = 0, beginY = 0;
        for (int x=0; x<=0; x++) for (int y=0; y<=0; y++) {
            try {
                System.out.println("Generating "+x+","+y);
                File biomeFile = new File("biomes" + x + "_" + y);
                if (!biomeFile.exists()) {
                    biomeFile.createNewFile();
                }
                FileOutputStream os = new FileOutputStream(biomeFile);
                //DataOutputStream dataos = new DataOutputStream(os);
                // 1. Biomes
                int[] biomes = mc.getBiomeData(x*1024, y*1024, 1024, 1024);
                byte[] bytebiomes = new byte[biomes.length];
                for (int i=0; i<biomes.length; i++) {
                    bytebiomes[i] = (byte)biomes[i];
                    //dataos.writeByte(biomes[i]);
                }
                os.write(bytebiomes);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("File operation error.");
                lockFile.delete();
                return;
            }
        }
        lockFile.delete();
        System.out.println("Completed.");

    }
}
