package me.dulce.commonutils;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnnotationScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationScanner.class);

    @Autowired private ApplicationContext applicationContext;

    /**
     * Finds all classes in packages defined in spring using @ComponentScan with a given annotation
     *
     * @param annotationToSearch The annotation to search for
     * @param <AnnotationType> Type of the annotation
     * @return A Map mapping all Classes with the annotation to the annotation instance
     */
    public <AnnotationType extends Annotation>
            Map<Class<?>, AnnotationType> getAllClassesWithAnnotation(
                    Class<AnnotationType> annotationToSearch) {
        List<String> basePackages = getComponentScanBasePackages();
        List<Class<?>> annotatedClasses = new ArrayList<>();

        for (String packageName : basePackages) {
            annotatedClasses.addAll(scanForAnnotatedClasses(packageName, annotationToSearch));
        }

        return annotatedClasses.stream()
                .collect(
                        Collectors.toMap(
                                annotatedClass -> annotatedClass,
                                annotatedClass ->
                                        annotatedClass.getAnnotation(annotationToSearch)));
    }

    private List<String> getComponentScanBasePackages() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(ComponentScan.class);

        ArrayList<String> beanValues = new ArrayList<>();
        for (String beanNameFound : beanNames) {
            beanValues.addAll(getAllBeanValues(beanNameFound));
        }
        return beanValues;
    }

    private List<String> getAllBeanValues(String beanName) {
        List<String> beanValues = new ArrayList<>();
        Set<ComponentScan> annotations =
                applicationContext.findAllAnnotationsOnBean(beanName, ComponentScan.class, false);

        for (ComponentScan scanComponent : annotations) {
            beanValues.addAll(Arrays.asList(scanComponent.basePackages()));
        }

        return beanValues;
    }

    private <Type> List<Class<Type>> scanForAnnotatedClasses(
            String basePackage, Class<? extends Annotation> annotationToSearch) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationToSearch));

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(basePackage);
        return getClassesFromBeanDefinition(beanDefinitions);
    }

    @SuppressWarnings("unchecked cast")
    private <Type> List<Class<Type>> getClassesFromBeanDefinition(
            Collection<BeanDefinition> beanDefinitions) {
        List<Class<Type>> classes = new ArrayList<>();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                classes.add((Class<Type>) Class.forName(beanDefinition.getBeanClassName()));
            } catch (ClassNotFoundException classNotFoundException) {
                LOGGER.error(
                        "Cannot find class {}",
                        classNotFoundException.getClass().getTypeName(),
                        classNotFoundException);
            }
        }
        return classes;
    }

    /**
     * Gets all methods in a class with a given annotation
     *
     * @param classToCheck The class to check
     * @param annotationToSearch The annotation to look for
     * @return A list of all methods with a given annotation
     */
    public List<Method> getMethodsWithAnnotationFromClass(
            Class<?> classToCheck, Class<? extends Annotation> annotationToSearch) {
        return MethodUtils.getMethodsListWithAnnotation(
                classToCheck, annotationToSearch, true, true);
    }
}
