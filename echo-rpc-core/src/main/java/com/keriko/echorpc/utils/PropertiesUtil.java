package com.keriko.echorpc.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Deng.Weiping
 * @since 2023/11/28 13:57
 */
@Slf4j
public class PropertiesUtil {

    /**
     * yaml 转 Properties
     *
     * @param input
     * @return
     */
    public static String castToProperties(String input) {
        Map<String, Object> propertiesMap = new LinkedHashMap<>();
        Map<String, Object> yamlMap = new Yaml().load(input);
        flattenMap("", yamlMap, propertiesMap);
        StringBuffer strBuff = new StringBuffer();
        propertiesMap.forEach((key, value) -> strBuff.append(key)
                .append("=")
                .append(value)
                .append(StrUtil.LF));
        return strBuff.toString();
    }

    /**
     * Properties 转 Yaml
     *
     * @param input
     * @return
     */
    public static String castToYaml(String input) {
        try {
            Map<String, Object> properties = readProperties(input);
            return properties2Yaml(properties);
        } catch (Exception e) {
            log.error("property 转 Yaml 转换失败", e);
        }
        return null;
    }

    private static Map<String, Object> readProperties(String input) {
        // 使用 LinkedHashMap 保证顺序
        Map<String, Object> propertiesMap = new LinkedHashMap<>();
        for (String line : input.split(StrUtil.LF)) {
            if (StrUtil.isNotBlank(line)) {
                // 使用正则表达式解析每一行中的键值对
                Pattern pattern = Pattern.compile("\\s*([^=\\s]*)\\s*=\\s*(.*)\\s*");
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String key = matcher.group(1);
                    String value = matcher.group(2);
                    propertiesMap.put(key, value);
                }
            }
        }
        return propertiesMap;
    }

    /**
     * 递归 Map 集合，转为 Properties集合
     *
     * @param prefix
     * @param yamlMap
     * @param treeMap
     */
    private static void flattenMap(String prefix, Map<String, Object> yamlMap, Map<String, Object> treeMap) {
        yamlMap.forEach((key, value) -> {
            String fullKey = prefix + key;
            if (value instanceof LinkedHashMap) {
                flattenMap(fullKey + ".", (LinkedHashMap) value, treeMap);
            } else if (value instanceof ArrayList) {
                List values = (ArrayList) value;
                for (int i = 0; i < values.size(); i++) {
                    String itemKey = String.format("%s[%d]", fullKey, i);
                    Object itemValue = values.get(i);
                    if (itemValue instanceof String) {
                        treeMap.put(itemKey, itemValue);
                    } else {
                        flattenMap(itemKey + ".", (LinkedHashMap) itemValue, treeMap);
                    }
                }
            } else {
                treeMap.put(fullKey, value.toString());
            }
        });
    }

    /**
     * properties 格式转化为 yaml 格式字符串
     *
     * @param properties
     * @return
     */
    private static String properties2Yaml(Map<String, Object> properties) {
        if (CollUtil.isEmpty(properties)) {
            return null;
        }
        Map<String, Object> map = parseToMap(properties);
        StringBuffer stringBuffer = map2Yaml(map);
        return stringBuffer.toString();
    }

    /**
     * 递归解析为 LinkedHashMap
     *
     * @param propMap
     * @return
     */
    private static Map<String, Object> parseToMap(Map<String, Object> propMap) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            if (CollUtil.isEmpty(propMap)) {
                return resultMap;
            }
            propMap.forEach((key, value) -> {
                if (key.contains(".")) {
                    String currentKey = key.substring(0, key.indexOf("."));
                    if (resultMap.get(currentKey) != null) {
                        return;
                    }
                    Map<String, Object> childMap = getChildMap(propMap, currentKey);
                    Map<String, Object> map = parseToMap(childMap);
                    resultMap.put(currentKey, map);
                } else {
                    resultMap.put(key, value);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }


    /**
     * 获取拥有相同父级节点的子节点
     *
     * @param propMap
     * @param currentKey
     * @return
     */
    private static Map<String, Object> getChildMap(Map<String, Object> propMap, String currentKey) {
        Map<String, Object> childMap = new LinkedHashMap<>();
        try {
            propMap.forEach((key, value) -> {
                if (key.contains(currentKey + ".")) {
                    key = key.substring(key.indexOf(".") + 1);
                    childMap.put(key, value);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childMap;
    }

    /**
     * map集合转化为yaml格式字符串
     *
     * @param map
     * @return
     */
    public static StringBuffer map2Yaml(Map<String, Object> map) {
        //默认deep 为零，表示不空格，deep 每加一层，缩进两个空格
        return map2Yaml(map, 0);
    }

    /**
     * 把Map集合转化为yaml格式 String字符串
     *
     * @param propMap map格式配置文件
     * @param deep    树的层级，默认deep 为零，表示不空格，deep 每加一层，缩进两个空格
     * @return
     */
    private static StringBuffer map2Yaml(Map<String, Object> propMap, int deep) {
        StringBuffer yamlBuffer = new StringBuffer();
        try {
            if (CollUtil.isEmpty(propMap)) {
                return yamlBuffer;
            }
            String space = getSpace(deep);
            for (Map.Entry<String, Object> entry : propMap.entrySet()) {
                Object valObj = entry.getValue();
                if (entry.getKey().contains("[") && entry.getKey().contains("]")) {
                    String key = entry.getKey().substring(0, entry.getKey().indexOf("[")) + ":";
                    yamlBuffer.append(space + key + "\n");
                    propMap.forEach((itemKey, itemValue) -> {
                        if (itemKey.startsWith(key.substring(0, entry.getKey().indexOf("[")))) {
                            yamlBuffer.append(getSpace(deep + 1) + "- ");
                            if (itemValue instanceof Map) {
                                StringBuffer valStr = map2Yaml((Map<String, Object>) itemValue, 0);
                                String[] split = valStr.toString().split(StrUtil.LF);
                                for (int i = 0; i < split.length; i++) {
                                    if (i > 0) {
                                        yamlBuffer.append(getSpace(deep + 2));
                                    }
                                    yamlBuffer.append(split[i]).append(StrUtil.LF);
                                }
                            } else {
                                yamlBuffer.append(itemValue + "\n");
                            }
                        }
                    });
                    break;
                } else {
                    String key = space + entry.getKey() + ":";
                    if (valObj instanceof String) { //值为value 类型，不用再继续遍历
                        yamlBuffer.append(key + " " + valObj + "\n");
                    } else if (valObj instanceof List) { //yaml List 集合格式
                        yamlBuffer.append(key + "\n");
                        List<String> list = (List<String>) entry.getValue();
                        String lSpace = getSpace(deep + 1);
                        for (String str : list) {
                            yamlBuffer.append(lSpace + "- " + str + "\n");
                        }
                    } else if (valObj instanceof Map) { //继续递归遍历
                        Map<String, Object> valMap = (Map<String, Object>) valObj;
                        yamlBuffer.append(key + "\n");
                        StringBuffer valStr = map2Yaml(valMap, deep + 1);
                        yamlBuffer.append(valStr.toString());
                    } else {
                        yamlBuffer.append(key + " " + valObj + "\n");
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yamlBuffer;
    }

    /**
     * 获取缩进空格
     *
     * @param deep
     * @return
     */
    private static String getSpace(int deep) {
        StringBuffer buffer = new StringBuffer();
        if (deep == 0) {
            return "";
        }
        for (int i = 0; i < deep; i++) {
            buffer.append("  ");
        }
        return buffer.toString();
    }

}