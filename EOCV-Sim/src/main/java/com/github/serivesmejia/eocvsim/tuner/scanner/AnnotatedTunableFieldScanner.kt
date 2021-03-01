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
package com.github.serivesmejia.eocvsim.tuner.scanner

import com.github.serivesmejia.eocvsim.tuner.TunableField
import com.github.serivesmejia.eocvsim.tuner.TunableFieldAcceptor
import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.ReflectUtil
import io.github.classgraph.ClassGraph
import java.lang.reflect.Type
import java.util.*

@Suppress("UNCHECKED_CAST")
class AnnotatedTunableFieldScanner(private val lookInPackage: String) {

    data class ScanResult(val tunableFields: HashMap<Type, Class<out TunableField<*>>>,
                          val acceptors: HashMap<Class<out TunableField<*>>, Class<out TunableFieldAcceptor>>)

    fun scan(): ScanResult {
        val tunableFields = HashMap<Type, Class<out TunableField<*>>>()

        Log.info("AnnotatedTunableFieldScanner", "Scanning in $lookInPackage...")

        //Scan for all classes in the specified package
        val classGraph = ClassGraph().enableAnnotationInfo().acceptPackages(lookInPackage)
        val result = classGraph.scan()

        //SCANNING FOR TUNABLE FIELDS

        for (classInfo in result.getClassesWithAnnotation(RegisterTunableField::class.java.name)) {
            try {
                val foundClass: Class<*> = try {
                    Class.forName(classInfo.name)
                } catch (ex: ClassNotFoundException) {
                    Log.error("AnnotatedTunableFieldScanner", "Unable to find class ${classInfo.name}", ex)
                    continue  //continue because we couldn't get the class...
                }

                if (!ReflectUtil.hasSuperclass(foundClass, TunableField::class.java)) continue

                val foundClassTunableField = foundClass as Class<out TunableField<*>>
                val type = ReflectUtil.getTypeArgumentsFrom(foundClassTunableField)[0]

                Log.info(
                    "AnnotatedTunableFieldScanner",
                    "Found TunableField for " + type.typeName + " (" + foundClass.name + ")"
                )

                tunableFields[type] = foundClassTunableField
            } catch (ex: Exception) {
                Log.warn("AnnotatedTunableFieldScanner", "Error while processing " + classInfo.name, ex)
            }
        }

        Log.info("AnnotatedTunableFieldScanner", "Found " + tunableFields.size + " TunableField(s)")
        Log.blank()

        //SCANNING FOR TUNABLE FIELD ACCEPTORS

        val acceptors = HashMap<Class<out TunableField<*>>, Class<out TunableFieldAcceptor>>()

        for (classInfo in result.getClassesWithAnnotation(RegisterTunableFieldAcceptor::class.java.name)) {
            val foundClass: Class<*> = try {
                Class.forName(classInfo.name)
            } catch (ex: ClassNotFoundException) {
                Log.error("AnnotatedTunableFieldScanner", "Unable to find class ${classInfo.name}", ex)
                continue  //continue because we couldn't get the class...
            }

            if (!ReflectUtil.hasSuperclass(foundClass, TunableFieldAcceptor::class.java)) continue

            val foundClassAcceptor = foundClass as Class<out TunableFieldAcceptor>

            for(annotation in foundClassAcceptor.annotations) {
                if(annotation.annotationClass == RegisterTunableFieldAcceptor::class) {
                    val acceptorAnnotation = annotation as RegisterTunableFieldAcceptor
                    val type = acceptorAnnotation.tunableFieldType.java

                    acceptors[type] = foundClassAcceptor

                    Log.info("AnnotatedTunableFieldScanner", "Found TunableFieldAcceptor for ${type.typeName} (${foundClassAcceptor.name})")
                }
            }

            Log.info("AnnotatedTunableFieldScanner", "Found " + acceptors.size + " TunableFieldAcceptors(s)")
            Log.blank()
        }

        return ScanResult(tunableFields, acceptors)
    }

}