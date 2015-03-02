package fnui.util

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import groovyjarjarasm.asm.*
import groovyjarjarasm.asm.commons.Remapper
import groovyjarjarasm.asm.commons.RemappingClassAdapter

/**
 * The FnuiClassUtils provides some static methods to extract information from classes.
 */
@CompileStatic
@Log4j
abstract class FnuiClassUtils {

    /**
     * Finds the most specific common package of the class collection. In Java-based applications
     * there is normally one (or a small set) of common base package(s) for all classes.
     *
     * This method tries to guess this set of base packages.
     *
     * Note: Classes in the default package are ignored.
     *
     * Example:
     *  - a.b.A
     *  - a.b.B
     *  - a.C
     *  - b.a.D
     *  - b.a.E
     *  =>
     *  ['a', 'b.a']
     *
     * @param classes to find the common base package for.
     * @return a list of common base packages of the class collection
     */
    static List<String> getBasePackagesFor(Collection<Class> classes) {
        List<String> packages = []

        for (Class c : classes) {
            def candidate = c.package?.name

            if (!candidate || packages.any { member -> candidate.startsWith(member) }) {
                continue
            }
            packages = packages.findAll { member -> !member.startsWith(candidate) } as List<String>
            packages << candidate
        }

        return packages
    }

    /**
     * Extract all used classes for the provided class. Calls getReferencedClassNames
     * and then use the {@link ClassReader} to retrieve the class from Bytecode.
     *
     * @param clazz
     * @return
     */
    static Set<Class> getReferencedClasses(Class clazz) {
        Set<Class> classes = new HashSet<>()

        for (String className : getReferencedClassNames(clazz)) {
            try {
                classes << Class.forName(className)
            } catch (e) {
                log.debug("Issue while loading ${className}: ", e)
            }
        }

        classes
    }

    /**
     * Extract the names of all used classes for the provided class with help of
     * the {@link ClassReader} by retrieving classes from Bytecode.
     *
     * For details see: http://stackoverflow.com/questions/3734825/find-out-which-classes-of-a-given-api-are-used
     *
     * @param clazz
     * @return
     */
    static Set<String> getReferencedClassNames(Class clazz) {
        ClassReader r = new ClassReader(clazz.getResourceAsStream(clazz.simpleName + '.class'))
        def collector = new ClassReferenceCollector()

        try {
            ClassVisitor visitor = new RemappingClassAdapter(new EmptyVisitor(), collector)
            r.accept(visitor, 0)
        } catch (IllegalStateException e) {
            log.debug "Could not extract classes from clazz '${clazz.name}'", e
        }

        return collector.classNames
    }

    private static class ClassReferenceCollector extends Remapper {
        Set<String> classNames = new HashSet<>()

        Set<String> getClassNames() {
            classNames
        }

        @Override
        public String mapType(String type) {
            classNames << type.replace('/', '.')
            return type
        }

        @Override
        Object mapValue(Object o) {
            if (o instanceof groovyjarjarasm.asm.Type) {
                classNames << o.className
            }
            return o
        }

        @Override
        String map(String s) {
            classNames << s.replace('/', '.')
            return s
        }
    }

    /**
     * A visitor implementation which is doing nothing but visiting everything.
     *
     * Taken from: http://websvn.ow2.org/filedetails.php?repname=asm&path=%2Ftrunk%2Fasm%2Ftest%2Fperf%2Forg%2Fobjectweb%2Fasm%2FALLPerfTest.java
     */
    private static class EmptyVisitor extends ClassVisitor {

        AnnotationVisitor av = new AnnotationVisitor(Opcodes.ASM5) {

            @Override
            public AnnotationVisitor visitAnnotation(String name, String desc) {
                return this
            }

            @Override
            public AnnotationVisitor visitArray(String name) {
                return this
            }
        }

        public EmptyVisitor() {
            super(Opcodes.ASM5)
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return av
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return av
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return new FieldVisitor(Opcodes.ASM5) {

                @Override
                public AnnotationVisitor visitAnnotation(String description, boolean visible) {
                    return av
                }

                @Override
                public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String description, boolean visible) {
                    return av
                }
            }
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {
            return new MethodVisitor(Opcodes.ASM5) {

                @Override
                public AnnotationVisitor visitAnnotationDefault() {
                    return av
                }

                @Override
                public AnnotationVisitor visitAnnotation(String description, boolean visible) {
                    return av
                }

                @Override
                public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String description, boolean visible) {
                    return av
                }

                @Override
                public AnnotationVisitor visitParameterAnnotation(int parameter, String description, boolean visible) {
                    return av
                }

                @Override
                public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String description, boolean visible) {
                    return av
                }

                @Override
                public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String description, boolean visible) {
                    return av
                }

                @Override
                public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String description, boolean visible) {
                    return av
                }
            }
        }
    }
}
