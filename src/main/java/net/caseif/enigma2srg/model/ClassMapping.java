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

import java.util.HashMap;
import java.util.Map;

public class ClassMapping extends Mapping {

    private Map<String, FieldMapping> fields = new HashMap<>();
    private Map<String, MethodMapping> methods = new HashMap<>();

    public ClassMapping(String obfuscated, String deobfuscated) {
        super(obfuscated.startsWith("none/") ? obfuscated.substring("none/".length()) : obfuscated, deobfuscated);
        Main.classes.put(getObfuscatedName(), this);
    }

    public Map<String, FieldMapping> getFieldMappings() {
        return fields;
    }

    public Map<String, MethodMapping> getMethodMappings() {
        return methods;
    }

    void addFieldMapping(FieldMapping mapping) {
        fields.put(mapping.getObfuscatedName(), mapping);
    }

    void addMethodMapping(MethodMapping mapping) {
        methods.put(mapping.getObfuscatedName(), mapping);
    }

}
