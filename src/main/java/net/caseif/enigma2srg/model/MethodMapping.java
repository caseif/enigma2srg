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
package net.caseif.enigma2srg.model;

import net.caseif.enigma2srg.Main;

public class MethodMapping extends Mapping {

    private final ClassMapping parent;
    private final String sig;

    public MethodMapping(ClassMapping parent, String obfuscated, String deobfuscated, String signature) {
        super(obfuscated, deobfuscated);
        this.parent = parent;

        // reconstruct the type signature to eliminate the fucking "none/" prefixes that Enigma feels the need to add in
        StringBuilder sigBuilder = new StringBuilder();
        char[] chars = signature.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 'L') {
                StringBuilder classNameBuilder = new StringBuilder();
                i++;
                while (chars[i] != ';') {
                    classNameBuilder.append(chars[i]);
                    i++;
                }
                String className = classNameBuilder.toString();
                if (className.startsWith("none/")) {
                    className = className.substring("none/".length());
                }
                sigBuilder.append("L").append(className).append(";");
            } else {
                sigBuilder.append(chars[i]);
            }
        }
        this.sig = sigBuilder.toString();

        parent.addMethodMapping(this);
    }

    public ClassMapping getParent() {
        return parent;
    }

    public String getSignature() {
        return sig;
    }

    public String getDeobfuscatedSignature() {
        final String keyPrefix = "none/";

        char[] chars = sig.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 'L') {
                builder.append('L');
                i++;
                StringBuilder classNameBuilder = new StringBuilder();
                while (chars[i] != ';') {
                    classNameBuilder.append(chars[i]);
                    i++;
                }
                String className = classNameBuilder.toString();
                boolean hasMapping = false;
                if (className.startsWith(keyPrefix)) {
                    className = className.substring(keyPrefix.length());
                }
                ClassMapping classMapping = Main.classes.get(className);
                if (classMapping != null) {
                    builder.append(classMapping.getDeobfuscatedName());
                    hasMapping = true;
                }

                if (!hasMapping) {
                    builder.append(className);
                }
                builder.append(';');
            } else {
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }

}
