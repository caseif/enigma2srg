/*
 * New BSD License (BSD-new)
 *
 * Copyright (c) 2015 Maxim Roncacé
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the copyright holder nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.caseif.enigma2srg;

import net.caseif.enigma2srg.model.ClassMapping;
import net.caseif.enigma2srg.model.FieldMapping;
import net.caseif.enigma2srg.model.MethodMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static Map<String, ClassMapping> classes = new HashMap<>();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Expected input file as argument!");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("Error: Input file does not exist!");
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            ClassMapping currentClass = null;
            int lineNum = 0;
            for (String line : reader.lines().collect(Collectors.toList())) {
                lineNum++;
                String[] arr = line.trim().split(" ");
                if (arr.length == 0) {
                    continue;
                }
                switch (arr[0]) {
                    case "CLASS": {
                        if (arr.length < 2 || arr.length > 3) {
                            System.err.println("Cannot parse file: malformed class mapping on line " + lineNum);
                            System.exit(1);
                        }

                        String obf = arr[1].replace("none/", "");
                        String deobf = arr.length == 3 ? arr[2] : obf;
                        currentClass = new ClassMapping(obf, deobf);
                        break;
                    }
                    case "FIELD": {
                        if (arr.length != 4) {
                            System.err.println("Cannot parse file: malformed field mapping on line " + lineNum);
                            System.exit(1);
                        }

                        if (currentClass == null) {
                            System.err.println("Cannot parse file: found field mapping before initial class mapping "
                                    + "on line " + lineNum);
                            System.exit(1);
                        }

                        String obf = arr[1];
                        String deobf = arr[2];
                        String type = arr[3];
                        new FieldMapping(currentClass, obf, deobf, type);
                        break;
                    }
                    case "METHOD": {
                        if (arr.length == 3) {
                            continue;
                        }

                        if (arr.length != 4) {
                            System.err.println("Cannot parse file: malformed method mapping on line " + lineNum);
                            System.exit(1);
                        }

                        if (currentClass == null) {
                            System.err.println("Cannot parse file: found method mapping before initial class mapping "
                                    + "on line " + lineNum);
                        }

                        String obf = arr[1];
                        String deobf = arr[2];
                        String sig = arr[3];
                        new MethodMapping(currentClass, obf, deobf, sig);
                        break;
                    }
                    case "ARG": {
                        // SRG doesn't support these
                        break;
                    }
                    default: {
                        System.err.println("Warning: Unrecognized mapping on line " + lineNum);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        classes.values().stream().filter(cm -> !cm.getObfuscatedName().equals(cm.getDeobfuscatedName()))
                .forEach(cm -> System.out.println(classToSrg(cm)));
        classes.values().forEach(cm -> cm.getFieldMappings().values()
                .forEach(fm -> System.out.println(fieldToSrg(fm))));
        classes.values().forEach(cm -> cm.getMethodMappings().values()
                .forEach(mm -> System.out.println(methodToSrg(mm))));
    }

    private static String classToSrg(ClassMapping cm) {
        return "CL: " + cm.getObfuscatedName() + " " + cm.getDeobfuscatedName();
    }

    private static String fieldToSrg(FieldMapping fm) {
        return "FD: "
                + fm.getParent().getObfuscatedName() + "/" + fm.getObfuscatedName() + " "
                + fm.getParent().getDeobfuscatedName() + "/" + fm.getDeobfuscatedName();
    }

    private static String methodToSrg(MethodMapping mm) {
        return "MD: "
                + mm.getParent().getObfuscatedName() + "/" + mm.getObfuscatedName() + " "
                + mm.getSignature() + " "
                + mm.getParent().getDeobfuscatedName() + "/" + mm.getDeobfuscatedName() + " "
                + mm.getDeobfuscatedSignature();
    }


}
