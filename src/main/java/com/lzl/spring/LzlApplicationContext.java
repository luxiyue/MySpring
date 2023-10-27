package com.lzl.spring;

import com.lzl.spring.annotations.Autowired;
import com.lzl.spring.annotations.Component;
import com.lzl.spring.annotations.ComponentScan;
import com.lzl.spring.annotations.Scope;
import com.lzl.springmvc.annotations.Controller;

import java.beans.Introspector;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LzlApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private ArrayList<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    /**
     * ①设置configClass
     * ②扫描@ComponentScan指定的目录 ，生成  BeanDefinition，并顺便将实现了BeanPostProcessor的bean放入beanPostProcessorList中去
     * ③将标有@Scope注解的且value为“singleton”的bean实例化放入 singletonObjects中去
     * @param configClass
     */
    public LzlApplicationContext(Class configClass) {
        this.configClass = configClass;
        refresh();
    }



    private void refresh() {
        //扫描  ---》 BeanDefinition --- > beanDefinitionMap
        obtainFreshBeanFactory();
        //简单实现：注册bean的后置处理器
        registerBeanPostProcessors();
        onRefresh();
        finishBeanFactoryInitialization();
    }



    private void finishBeanFactoryInitialization() {
        //实例化单例Bean
        for (String beanName : beanDefinitionMap.keySet()) {
            getBean(beanName);
        }
    }




    protected void onRefresh(){
    }


    private void obtainFreshBeanFactory() {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();//扫描路径 （com.lzl.service）
            path = path.replace(".","/");// com/lzl/service

            ClassLoader classLoader = LzlApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());//E:\JavaCode\MySpring\out\production\MySpring\com\lzl\service
            if (file.isDirectory()) {//如果是文件夹
                //找到该文件夹下的所有普通file
                List<File> files = getFilesOfDirectory(file);
                for (File f : files) {
                    String fileName = f.getAbsolutePath();//绝对路径:E:\JavaCode\MySpring\out\production\MySpring\com\lzl\service\UserService.class
                    if (fileName.endsWith(".class")){
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                        className = className.replace("\\",".").replace("/",".");//com.lzl.service.UserService
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            if (!clazz.isInterface() && !clazz.isEnum() && isAnnotated(clazz,Component.class) ) {//是个Bean
                                String beanName = getBeanName(clazz);
                                if (beanName==null || beanName.equals("")) {
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                }else {
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(beanName,beanDefinition);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }



    private void registerBeanPostProcessors() {
        beanDefinitionMap.values().forEach(beanDefinition -> {
            if (BeanPostProcessor.class.isAssignableFrom(beanDefinition.getType())) {
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) getBean(beanDefinition.getType());
                beanPostProcessorList.add(beanPostProcessor);
            }
        });
    }


    /**
     * 找到该文件夹下的所有普通file
     * @param directory
     * @return
     */
    private List<File> getFilesOfDirectory(File directory) {
        List<File> result = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return result;
        }
        for (File file : files) {
            if (file.isFile()) {
                result.add(file);
            }
            if (file.isDirectory()){
                result.addAll(getFilesOfDirectory(file));
            }
        }
        return result;
    }


    /**
     * ①createBeanInstance
     * ②populateBean
     * ③ invokeAwareMethods  ->  postProceesor:Before  -> initMethods --> postProcessor:After
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object createBean(String beanName,BeanDefinition beanDefinition){
        return doCreateBean(beanName, beanDefinition);
    }
    private Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        try {
            Object instance = createBeanInstance(clazz);
            populateBean(clazz, instance);
            Object exposedObject = initializeBean(beanName, instance);
            return exposedObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private Object initializeBean(String beanName, Object bean) {
        //invokeAwareMethods:Aware回调
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) bean).setBeanName(beanName);
        }
        //beanPostProcessor-Before
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            bean = beanPostProcessor.postProcessBeforeInitialization(beanName, bean);
        }

        //invokeInitMethods:初始化
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }

        //beanPostProcessor-After
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            bean = beanPostProcessor.postProcessAfterInitialization(beanName, bean);
        }

        return bean;
    }

    private Object createBeanInstance(Class clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object instance = clazz.getConstructor().newInstance();
        return instance;
    }

    private void populateBean(Class clazz, Object instance) throws IllegalAccessException {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Autowired.class)) {
                f.setAccessible(true);
                f.set(instance,getBean(f.getName()));
            }
        }
    }


    /**
     * 如果是Singleton则从容器中获取，
     * 如果是ProtoType则直接new生成。
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        return doGetBean(beanName);
    }
    private Object doGetBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        }else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")) {
                Object bean = singletonObjects.get(beanName);
                if (bean == null) {
                    bean = createBean(beanName, beanDefinition);
                    singletonObjects.put(beanName,bean);
                }
                return bean;
            }else {//多例
                return createBean(beanName,beanDefinition);
            }
        }
    }


    public <T> T getBean(Class<T> clazz){
        // 1.推断该类的对应的beanName
        String beanName = null;
        if (isAnnotated(clazz,Component.class)) {//是个Bean
            beanName = this.getBeanName(clazz);
            if (beanName==null || beanName.equals("")) {
                beanName = Introspector.decapitalize(clazz.getSimpleName());
            }
        }
        return (T) getBean(beanName);
    }




    public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        Map<String, T> result = new HashMap<>();
        singletonObjects.entrySet().forEach(entry -> {
            if (clazz.isAssignableFrom(entry.getValue().getClass()) ) {
                result.put(entry.getKey(), (T)entry.getValue());
            }
        });
        return result;
    }


    public boolean isAnnotated(Class clazz, Class annotationClazz) {
        if (clazz.isAnnotationPresent(annotationClazz)) {
            return true;
        }
        Annotation[] annotations = clazz.getAnnotations();
        if (annotations != null && annotations.length != 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAnnotationPresent(annotationClazz)) {
                    return true;
                }
            }
        }
        return false;
    }


////////////////////下面纯属自己乱写的
    private String getBeanName(Class<?> clazz){
        try {
            if (clazz.isAnnotationPresent(Component.class)) {
                Component componet = clazz.getAnnotation(Component.class);
                String beanName = componet.value();//bean的名字
                return beanName;
            }
            Annotation[] annotations = clazz.getAnnotations();
            if (annotations != null && annotations.length != 0) {
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().isAnnotationPresent(Component.class)) {
                        Controller controller = (Controller) annotation;
                        return controller.value();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
