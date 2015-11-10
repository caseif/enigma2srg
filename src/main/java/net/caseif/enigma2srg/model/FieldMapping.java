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

public class FieldMapping extends Mapping {

    private final ClassMapping parent;
    private final String type;

    public FieldMapping(ClassMapping parent, String obfuscated, String deobfuscated, String type) {
        super(obfuscated, deobfuscated);
        this.parent = parent;
        this.type = type.startsWith("Lnone/") ? "L" + type.substring("Lnone/".length()) : type;

        parent.addFieldMapping(this);
    }

    public ClassMapping getParent() {
        return parent;
    }

    public String getType() {
        return type;
    }

    public String getDeobfuscatedType() {
        final String keyPrefix = "Lnone/";
        if (getType().startsWith("L") && getType().endsWith(";")) {
            String className = getType().substring(1, getType().length() - 1);
            if (className.startsWith(keyPrefix)) {
                className = className.substring(keyPrefix.length());
            }
            ClassMapping typeMapping = Main.classes.get(className);
            if (typeMapping == null) {
                return getType();
            }
            return "L" + typeMapping.getDeobfuscatedName() + ";";
        }
        return getType();
    }

}
