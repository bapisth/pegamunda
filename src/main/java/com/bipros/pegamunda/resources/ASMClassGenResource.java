package com.bipros.pegamunda.resources;

import com.bipros.pegamunda.baserepo.Calculator;
import com.bipros.pegamunda.runtimeclass.loader.DynamicClassLoader;
import com.bipros.pegamunda.runtimeclass.loader.util.EnityClassGenerator;
import com.bipros.pegamunda.util.LRUCache;
import org.springframework.asm.ClassWriter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/asm/class")
public class ASMClassGenResource {
    private static final LRUCache<String, Class> CACHE = new LRUCache<>(10);
    @GetMapping("/{className}/create")
    public String generateClass(@PathVariable("className") String className) throws IllegalAccessException, InstantiationException {
        ClassWriter classWriter = EnityClassGenerator.generateClass(className);
        DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(ASMClassGenResource.class.getClassLoader());
        String finalClassName = EnityClassGenerator.packageName + "." + className;
        Class<?> aClass = dynamicClassLoader.defineClass(finalClassName, classWriter.toByteArray());
        //Store into the cache for future use
        CACHE.put(finalClassName, aClass);
        System.out.println(aClass.getName());
        Calculator calc = (Calculator) aClass.newInstance();
        System.out.println("2 + 2 = " + calc.add(2, 2));
        return "Class Succsessfully Generated";
    }

    @GetMapping("/{className}/test")
    public String testClass(@PathVariable("className") String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        String key = EnityClassGenerator.packageName + "." + className;
        Class aClass = CACHE.get(key);
        if (aClass == null) throw new ClassNotFoundException();
        Calculator calc = (Calculator) aClass.newInstance();
        int add = calc.add(2, 2);
        System.out.println("2 + 2 = " + add);
        return "Class Succsessfully Generated" + add;
    }
}
