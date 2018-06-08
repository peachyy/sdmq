package io.sdmq.common.extension;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import io.sdmq.queue.core.ConsumeQueueProvider;

/**
 * Created by Xs.Tao on 2017/12/20.
 */
public final class ExtensionLoader {

    private static volatile Map<Class<?>, Object> extensionMap = new ConcurrentHashMap<>();

    private static volatile Map<Class<?>, List<?>> extensionListMap = new ConcurrentHashMap<>();

    private ExtensionLoader() {
    }

    public static <T> T getExtension(Class<T> clazz) {
        T extension = (T) extensionMap.get(clazz);
        if (extension == null) {
            extension = newExtension(clazz);
            if (extension != null) {
                extensionMap.put(clazz, extension);
            }
        }
        return extension;
    }

    public static <T> List<T> getExtensionList(Class<T> clazz) {
        List<T> extensions = (List<T>) extensionListMap.get(clazz);
        if (extensions == null) {
            extensions = newExtensionList(clazz);
            if (!extensions.isEmpty()) {
                extensionListMap.put(clazz, extensions);
            }
        }
        return extensions;
    }

    public static <T> T newExtension(Class<T> clazz) {
        String defaultImp = getDefaultSPI(clazz);
        if (StringUtils.isEmpty(defaultImp)) {
            throw new RuntimeException(String.format("请配置 %s SPI默认实现", clazz.getName()));
        }
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        for (T service : serviceLoader) {
            if (service.getClass().isAnnotationPresent(ExtNamed.class)
                    && defaultImp.equalsIgnoreCase(getExNamed(service.getClass()))) {
                return service;
            }
        }
        return null;
    }

    private static <T> String getDefaultSPI(Class<T> clazz) {
        String spi = System.getProperty(clazz.getName());
        if (!StringUtils.isEmpty(spi)) {
            return spi;
        }
        if (clazz.isAnnotationPresent(SPI.class)) {
            SPI annotation = clazz.getAnnotation(SPI.class);
            return annotation.value();
        }
        return null;
    }

    private static <T> String getExNamed(Class<T> clazz) {
        if (clazz.isAnnotationPresent(ExtNamed.class)) {
            ExtNamed annotation = clazz.getAnnotation(ExtNamed.class);
            return annotation.value();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(ExtensionLoader.getExtension(ConsumeQueueProvider.class));
        System.out.println(ExtensionLoader.getExtension(ConsumeQueueProvider.class));
        System.out.println(ExtensionLoader.getExtension(ConsumeQueueProvider.class));
    }

    public static <T> List<T> newExtensionList(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        List<T>          extensions    = new ArrayList<>();
        for (T service : serviceLoader) {
            extensions.add(service);
        }
        return extensions;
    }

//    /**
//     * @param namespaceFun   命名空间函数
//     * @param namespaceValue 指定命名空间的值
//     */
//    public static <T> T getExtension(Class<T> clazz, String namespaceFun, String namespaceValue) {
//        List<T> lst = ExtensionLoader.getExtensionList(clazz);
//        if (lst == null || lst.size() == 0) {
//            throw new RuntimeException(String.format("请配置 %s 厂商实现", clazz.getName()));
//        }
//        if (lst != null && lst.size() > 0) {
//            for (T ext : lst) {
//                Method method = ReflectionUtils.findMethod(clazz, namespaceFun);
//                Assert.notNull(method, String.format("厂商接口 %s 没有找到命名空间函数 %s", clazz.getName(), namespaceFun));
//                String namespaceValue_ = Objects.toString(ReflectionUtils.invokeMethod(method, ext));
//                if (namespaceValue_.equalsIgnoreCase(namespaceValue)) {
//
//                    return ext;
//                }
//            }
//        }
//        return null;
//    }
}