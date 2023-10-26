package com.lzl.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        //扫描  ---》 BeanDefinition --- > beanDefinitionMap
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
                            if (clazz.isAnnotationPresent(Component.class)) {//是个Bean
                                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                    BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.newInstance();
                                    beanPostProcessorList.add(beanPostProcessor);
                                }
                                Component componet = clazz.getAnnotation(Component.class);
                                String beanName = componet.value();//bean的名字
                                if (beanName.equals("")) {
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
                        }catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        //实例化单例Bean
        for (String beanName : beanDefinitionMap.keySet()) {
//            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
//            if (beanDefinition.getScope().equals("singleton")) {
//                Object bean = createBean(beanName,beanDefinition);
//                singletonObjects.put(beanName,bean);
//            }
            getBean(beanName);
        }
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
        Class clazz = beanDefinition.getType();
        try {
            Object instance = clazz.getConstructor().newInstance();
            //populateBean:简单版依赖注入
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(Autowired.class)) {
                    f.setAccessible(true);
                    f.set(instance,getBean(f.getName()));
                }
            }
            //invokeAwareMethods:Aware回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware)instance).setBeanName(beanName);
            }
            //beanPostProcessor-Before
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(beanName,instance);
            }

            //invokeInitMethods:初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean)instance).afterPropertiesSet();
            }

            //beanPostProcessor-After
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(beanName,instance);
            }

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 如果是Singleton则从容器中获取，
     * 如果是ProtoType则直接new生成。
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        }else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")) {
                Object bean = singletonObjects.get(beanName);
                if (bean == null) {
                    Object o = createBean(beanName, beanDefinition);
                    singletonObjects.put(beanName,o);
                }
                return bean;
            }else {//多例
                return createBean(beanName,beanDefinition);
            }
        }
    }

}
