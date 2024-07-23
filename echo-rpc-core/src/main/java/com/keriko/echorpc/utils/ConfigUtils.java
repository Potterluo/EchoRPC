package com.keriko.echorpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import org.yaml.snakeyaml.Yaml;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.io.InputStream;
import java.util.Objects;

/**
 * 配置工具类
 */
public class ConfigUtils {

    /**
     * 加载配置文件并将其映射到指定的类对象上。
     * 此方法提供了一个简化的途径来从配置文件中动态加载配置到特定的类实例中。
     * 它支持通过类类型和配置前缀来加载配置，使得配置的加载更加灵活和可定制化。
     *
     * @param tClass 需要加载配置的类类型。这个类应该有与配置项名称匹配的公共静态字段。
     * @param prefix 配置项的前缀。这个前缀可以用于从大量的配置项中筛选出特定的配置。
     * @param <T> 类型参数，指示需要加载配置的类类型。
     * @return 类型为T的实例，其字段已根据配置文件中的相应配置进行初始化。
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        // 调用重载的loadConfig方法，使用空字符串作为默认的环境变量前缀
//        System.out.println("prefix: " + prefix);
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 根据配置类类型、环境变量和前缀加载配置。
     * 该方法通过构建配置文件名称（基于环境变量），然后使用Props类加载配置，最后将配置转换为指定的JavaBean类型。
     * 这种方式允许灵活地从配置文件中加载特定环境的配置，适用于多环境配置的场景。
     *
     * @param tClass 配置类的类型，用于指定加载配置的目标JavaBean类型。
     * @param prefix 配置项的前缀，用于从配置文件中筛选特定的配置项。
     * @param environment 环境变量，用于构建特定环境的配置文件名称，如"dev"、"test"、"prod"等。
     * @return 返回一个加载了配置的JavaBean实例，其属性与配置文件中的配置项对应。
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        // 构建配置文件基础名称，默认使用.properties后缀
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }

        // 文件名及扩展名
        String propertiesFile = configFileBuilder.toString() + ".properties";
        String ymlFile = configFileBuilder.toString() + ".yml";
        String yamlFile = configFileBuilder.toString() + ".yaml";

        // 创建一个Props对象或一个Map来存储配置项
        Map<String, Object> configMap = null;

        try {
            //TODO yaml配置未生效
            if (resourceExists(ymlFile)) {
//                // 加载 .yml 文件
//                Yaml yaml = new Yaml();
//                try (InputStream inputStream = ConfigUtils.class.getClassLoader().getResourceAsStream(ymlFile)) {
//                    if (inputStream != null) {
//                        configMap = yaml.loadAs(inputStream, Map.class);
//                    }
//                }
                PropertiesUtil.castToProperties(ymlFile);
            } else if (resourceExists(yamlFile)) {
                // 加载 .yaml 文件
                Yaml yaml = new Yaml();
                try (InputStream inputStream = ConfigUtils.class.getClassLoader().getResourceAsStream(yamlFile)) {
                    if (inputStream != null) {
                        configMap = yaml.loadAs(inputStream, Map.class);
                    }
                }
            } else if (resourceExists(propertiesFile)) {
                // 加载 .properties 文件
//                System.out.println("load properties file: " + propertiesFile);
                Props props = new Props(propertiesFile);
                return props.toBean(tClass, prefix);
            } else {
                throw new IllegalArgumentException("Configuration file not found for the given environment: " + environment);
            }

            if (configMap != null) {
                // 将 Map 转换为指定类型的 JavaBean
                T configBean = convertMapToBean(configMap, tClass, prefix);
                return configBean;
            } else {
                throw new IllegalStateException("Failed to load configuration file");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading configuration file", e);
        }
    }

    private static boolean resourceExists(String resource) {
        return Thread.currentThread().getContextClassLoader().getResource(resource) != null;
    }

    public static <T> T convertMapToBean(Map<String, Object> map, Class<T> tClass, String prefix) {
        try {
            T instance = tClass.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (prefix != null && !key.startsWith(prefix)) {
                    continue;
                }
                assert prefix != null;
                String fieldName = key.substring(prefix.length());
                Field field = tClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(instance, entry.getValue());
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert map to bean", e);
        }
    }
}