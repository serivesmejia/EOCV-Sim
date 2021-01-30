/*
 * Copyright (c) 2021 Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.github.serivesmejia.eocvsim.tuner.scanner;

import com.github.serivesmejia.eocvsim.tuner.TunableField;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.ReflectUtil;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Type;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class AnnotatedTunableFieldScanner {

    private String lookInPackage;

    public AnnotatedTunableFieldScanner(String lookInPackage) {
        this.lookInPackage = lookInPackage;
    }

    public HashMap<Type, Class<? extends TunableField>> lookForTunableFields() {

        HashMap<Type, Class<? extends TunableField>> tunableFields = new HashMap<>();

        Log.info("AnnotatedTunableFieldScanner", "Scanning in " + lookInPackage + "...");

        //Scan for all classes in the specified package
        ClassGraph classGraph = new ClassGraph().enableAnnotationInfo().acceptPackages(lookInPackage);
        ScanResult result = classGraph.scan();

        for(ClassInfo classInfo : result.getClassesWithAnnotation(RegisterTunableField.class.getName())) {

            try {

                Class<?> foundClass;

                try {
                    foundClass = Class.forName(classInfo.getName());
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                    continue;  //continue because we couldn't get the class...
                }

                if (!ReflectUtil.hasSuperclass(foundClass, TunableField.class)) continue;

                Class<? extends TunableField> foundClassTunableField = (Class<? extends TunableField>) foundClass;
                Type type = ReflectUtil.getTypeArgumentsFrom(foundClassTunableField)[0];

                Log.info("AnnotatedTunableFieldScanner", "Found TunableField for " + type.getTypeName() + " (" + foundClass.getName() + ")");

                tunableFields.put(type, foundClassTunableField);

            } catch(Exception ex) {
                Log.warn("AnnotatedTunableFieldScanner", "Error while processing " + classInfo.getName(), ex);
            }

        }

        Log.info("AnnotatedTunableFieldScanner", "Found " + tunableFields.size() + " TunableField(s)");
        Log.white();

        return tunableFields;

    }

}
