

package com.gp.aiActuator.entity;

import cn.hutool.core.exceptions.ValidateException;
import com.google.gson.Gson;
import com.gp.aiActuator.Engine.NameInMap;
import com.gp.aiActuator.Engine.Validation;
import com.gp.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TeaModel {
    private static final Logger logger = LoggerFactory.getLogger(TeaModel.class);

    public TeaModel() {
    }

    public Map<String, Object> toMap() {
        return changeToMap(this, true);
    }

    public static Map<String, Object> toMap(Object object) {
        return toMap(object, true);
    }

    private static Map<String, Object> toMap(Object object, Boolean exceptStream) {
        Map<String, Object> map = new HashMap();
        if (null != object && object instanceof Map) {
            return (Map)object;
        } else if (null != object && TeaModel.class.isAssignableFrom(object.getClass())) {
            return changeToMap(object, exceptStream);
        } else {
            return map;
        }
    }

    private Map<String, Object> toMap(Boolean exceptStream) {
        return changeToMap(this, exceptStream);
    }

    private static Map<String, Object> changeToMap(Object object, Boolean exceptStream) {
        HashMap<String, Object> map = new HashMap();

        try {
            Field[] var3 = object.getClass().getFields();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Field field = var3[var5];
                NameInMap anno = (NameInMap)field.getAnnotation(NameInMap.class);
                String key;
                if (anno == null) {
                    key = field.getName();
                } else {
                    key = anno.value();
                }

                if (null != field.get(object) && List.class.isAssignableFrom(field.get(object).getClass())) {
                    List<Object> arrayField = (List)field.get(object);
                    List<Object> fieldList = new ArrayList();

                    for(int i = 0; i < arrayField.size(); ++i) {
                        fieldList.add(parseObject(arrayField.get(i)));
                    }

                    map.put(key, fieldList);
                } else if (null != field.get(object) && TeaModel.class.isAssignableFrom(field.get(object).getClass())) {
                    map.put(key, toMap(field.get(object), exceptStream));
                } else if (null != field.get(object) && Map.class.isAssignableFrom(field.get(object).getClass())) {
                    Map<String, Object> valueMap = (Map)field.get(object);
                    Map<String, Object> result = new HashMap();
                    Iterator var11 = valueMap.entrySet().iterator();

                    while(var11.hasNext()) {
                        Map.Entry<String, Object> entry = (Map.Entry)var11.next();
                        result.put(entry.getKey(), parseObject(entry.getValue()));
                    }

                    map.put(key, result);
                } else if ((!exceptStream || null == field.get(object) || !InputStream.class.isAssignableFrom(field.get(object).getClass())) && (!exceptStream || null == field.get(object) || !OutputStream.class.isAssignableFrom(field.get(object).getClass()))) {
                    map.put(key, field.get(object));
                }
            }

            return map;
        } catch (Exception var13) {
            throw new ServiceException(var13.getMessage());
        }
    }

    public static Object parseObject(Object o) {
        if (null == o) {
            return o;
        } else {
            Class clazz = o.getClass();
            Iterator var4;
            if (List.class.isAssignableFrom(clazz)) {
                List<Object> list = (List)o;
                List<Object> result = new ArrayList();
                var4 = list.iterator();

                while(var4.hasNext()) {
                    Object object = var4.next();
                    result.add(parseObject(object));
                }

                return result;
            } else if (!Map.class.isAssignableFrom(clazz)) {
                return TeaModel.class.isAssignableFrom(clazz) ? ((TeaModel)o).toMap(false) : o;
            } else {
                Map<String, Object> map = (Map)o;
                Map<String, Object> result = new HashMap();
                var4 = map.entrySet().iterator();

                while(var4.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry)var4.next();
                    result.put(entry.getKey(), parseObject(entry.getValue()));
                }

                return result;
            }
        }
    }

    private static Object buildObject(Object o, Class self, Type subType) {
        Class valueClass = o.getClass();
        Iterator var6;
        ParameterizedType parameterizedType;
        Type[] types;
        if (Map.class.isAssignableFrom(self) && Map.class.isAssignableFrom(valueClass)) {
            Map<String, Object> valueMap = (Map)o;
            Map<String, Object> result = new HashMap();
            var6 = valueMap.entrySet().iterator();

            while(true) {
                while(var6.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry)var6.next();
                    if (null != subType && !(subType instanceof WildcardType)) {
                        if (subType instanceof Class) {
                            result.put(entry.getKey(), buildObject(entry.getValue(), (Class)subType, (Type)null));
                        } else {
                            parameterizedType = (ParameterizedType)subType;
                            types = parameterizedType.getActualTypeArguments();
                            result.put(entry.getKey(), buildObject(entry.getValue(), (Class)parameterizedType.getRawType(), types[types.length - 1]));
                        }
                    } else {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }

                return result;
            }
        } else if (List.class.isAssignableFrom(self) && List.class.isAssignableFrom(valueClass)) {
            List<Object> valueList = (List)o;
            List<Object> result = new ArrayList();
            var6 = valueList.iterator();

            while(true) {
                while(var6.hasNext()) {
                    Object object = var6.next();
                    if (null != subType && !(subType instanceof WildcardType)) {
                        if (subType instanceof Class) {
                            result.add(buildObject(object, (Class)subType, (Type)null));
                        } else {
                            parameterizedType = (ParameterizedType)subType;
                            types = parameterizedType.getActualTypeArguments();
                            result.add(buildObject(object, (Class)parameterizedType.getRawType(), types[types.length - 1]));
                        }
                    } else {
                        result.add(object);
                    }
                }

                return result;
            }
        } else if (TeaModel.class.isAssignableFrom(self) && Map.class.isAssignableFrom(valueClass)) {
            try {
                return toModel((Map)o, (TeaModel)self.newInstance());
            } catch (Exception var10) {
                throw new ServiceException(var10.getMessage());
            }
        } else {
            return confirmType(self, o);
        }
    }

    private static Type getType(Field field, int index) {
        ParameterizedType genericType = (ParameterizedType)field.getGenericType();
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        Type actualTypeArgument = actualTypeArguments[index];
        return actualTypeArgument;
    }

    public static <T extends TeaModel> T toModel(Map<String, ?> map, T model) {
        T result = model;
        Field[] var3 = model.getClass().getFields();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            NameInMap anno = (NameInMap)field.getAnnotation(NameInMap.class);
            String key;
            if (anno == null) {
                key = field.getName();
            } else {
                key = anno.value();
            }

            Object value = map.get(key);
            if (value != null) {
                result = setTeaModelField(result, field, value, false);
            }
        }

        return result;
    }

    private static <T extends TeaModel> T setTeaModelField(T model, Field field, Object value, boolean userBuild) {
        try {
            Class<?> clazz = field.getType();
            Object resultValue = parseNumber(value, clazz);
            if (TeaModel.class.isAssignableFrom(clazz)) {
                Object data = clazz.getDeclaredConstructor().newInstance();
                if (userBuild) {
                    field.set(model, build(toMap(resultValue, false), (TeaModel)data));
                } else if (!userBuild && Map.class.isAssignableFrom(resultValue.getClass())) {
                    field.set(model, toModel((Map)resultValue, (TeaModel)data));
                } else {
                    field.set(model, resultValue);
                }
            } else if (Map.class.isAssignableFrom(clazz)) {
                field.set(model, buildObject(resultValue, Map.class, getType(field, 1)));
            } else if (List.class.isAssignableFrom(clazz)) {
                field.set(model, buildObject(resultValue, List.class, getType(field, 0)));
            } else {
                field.set(model, confirmType(clazz, resultValue));
            }

            return model;
        } catch (Exception var8) {
            throw new ServiceException(var8.getMessage());
        }
    }

    public static <T extends TeaModel> T build(Map<String, ?> map, T model) {
        T result = model;
        Field[] var3 = model.getClass().getFields();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            String key = field.getName();
            Object value = map.get(key);
            if (value == null) {
                NameInMap anno = (NameInMap)field.getAnnotation(NameInMap.class);
                if (null == anno) {
                    continue;
                }

                key = anno.value();
                value = map.get(key);
                if (null == value) {
                    continue;
                }
            }

            result = setTeaModelField(result, field, value, true);
        }

        return result;
    }

    private static Object parseNumber(Object value, Class clazz) {
        BigDecimal bigDecimal;
        if (value instanceof Double && (clazz == Long.class || clazz == Long.TYPE)) {
            bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.longValue();
        } else if (value instanceof Double && (clazz == Integer.class || clazz == Integer.TYPE)) {
            bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.intValue();
        } else if (!(value instanceof Double) || clazz != Float.class && clazz != Float.TYPE) {
            return value;
        } else {
            bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.floatValue();
        }
    }

    public void validate() {
        Field[] fields = this.getClass().getFields();

        try {
            for(int i = 0; i < fields.length; ++i) {
                Object object = fields[i].get(this);
                Validation validation = (Validation)fields[i].getAnnotation(Validation.class);
                boolean required;
                if (null != validation) {
                    required = validation.required();
                } else {
                    required = false;
                }

                if (required && null == object) {
                    throw new ValidateException("Field " + fields[i].getName() + " is required");
                }

                if (null != validation && null != object) {
                    String pattern = validation.pattern();
                    int maxLength = validation.maxLength();
                    int minLength = validation.minLength();
                    double maximum = validation.maximum();
                    double minimum = validation.minimum();
                    if (!"".equals(pattern) || maxLength > 0 || minLength > 0 || maximum != Double.MAX_VALUE || minimum != Double.MIN_VALUE) {
                        this.determineType(fields[i].getType(), object, pattern, maxLength, minLength, maximum, minimum, fields[i].getName());
                    }
                }
            }

        } catch (Exception var13) {
            throw new ValidateException(var13.getMessage(), var13);
        }
    }

    private void determineType(Class clazz, Object object, String pattern, int maxLength, int minLength, double maximum, double minimum, String fieldName) {
        if (Map.class.isAssignableFrom(clazz)) {
            this.validateMap(pattern, maxLength, minLength, maximum, minimum, (Map)object, fieldName);
        } else if (TeaModel.class.isAssignableFrom(clazz)) {
            ((TeaModel)object).validate();
        } else {
            int j;
            if (List.class.isAssignableFrom(clazz)) {
                List<?> list = (List)object;

                for(j = 0; j < list.size(); ++j) {
                    this.determineType(list.get(j).getClass(), list.get(j), pattern, maxLength, minLength, maximum, minimum, fieldName);
                }
            } else if (clazz.isArray()) {
                Object[] objects = (Object[])((Object[])object);

                for(j = 0; j < objects.length; ++j) {
                    this.determineType(clazz.getComponentType(), objects[j], pattern, maxLength, minLength, maximum, minimum, fieldName);
                }
            } else if (Number.class.isAssignableFrom(clazz)) {
                double value = Double.valueOf(object.toString());
                if (value > maximum) {
                    throw new ValidateException(this.getClass().getName() + "." + fieldName + " exceeds the maximum");
                }

                if (value < minimum) {
                    throw new ValidateException(this.getClass().getName() + "." + fieldName + " less than minimum");
                }
            } else {
                String value = String.valueOf(object);
                if (maxLength > 0 && value.length() > maxLength) {
                    throw new ValidateException(this.getClass().getName() + "." + fieldName + " exceeds the maximum length");
                }

                if (minLength > 0 && value.length() < minLength) {
                    throw new ValidateException(this.getClass().getName() + "." + fieldName + " less than minimum length");
                }

                if (!"".equals(pattern) && !Pattern.matches(pattern, value)) {
                    throw new ValidateException(this.getClass().getName() + "." + fieldName + " regular match failed");
                }
            }
        }

    }

    private void validateMap(String pattern, int maxLength, int minLength, double maximum, double minimum, Map<String, Object> map, String fieldName) {
        Iterator var10 = map.entrySet().iterator();

        while(var10.hasNext()) {
            Map.Entry entry = (Map.Entry)var10.next();
            if (entry.getValue() != null) {
                this.determineType(entry.getValue().getClass(), entry.getValue(), pattern, maxLength, minLength, maximum, minimum, fieldName);
            }
        }

    }

    public static Map<String, Object> buildMap(TeaModel teaModel) {
        return null == teaModel ? null : teaModel.toMap();
    }

    public static void validateParams(TeaModel teaModel, String paramName) {
        if (null == teaModel) {
            throw new ValidateException("parameter " + paramName + " is not allowed as null");
        } else {
            teaModel.validate();
        }
    }

    public static Object confirmType(Class expect, Object object) {
        if (String.class.isAssignableFrom(expect)) {
            if (!(object instanceof Number) && !(object instanceof Boolean)) {
                if (!(object instanceof Map) && !(object instanceof List)) {
                    return object;
                }

                return (new Gson()).toJson(object);
            }

            return object.toString();
        } else if (Boolean.class.isAssignableFrom(expect)) {
            if (object instanceof String) {
                return Boolean.parseBoolean(String.valueOf(object));
            }

            if (object instanceof Integer) {
                if (object.toString().equals("1")) {
                    return true;
                }

                if (object.toString().equals("0")) {
                    return false;
                }
            }
        } else {
            BigDecimal bigDecimal;
            if (Integer.class.isAssignableFrom(expect)) {
                if (object instanceof String) {
                    try {
                        Integer.parseInt(object.toString());
                    } catch (NumberFormatException var4) {
                    }

                    bigDecimal = new BigDecimal(object.toString());
                    return bigDecimal.intValue();
                }

                if (object instanceof Boolean) {
                    return object.toString().equalsIgnoreCase("true") ? 1 : 0;
                }

                if (object instanceof Long) {
                    if ((Long)object > 2147483647L) {
                    }

                    bigDecimal = new BigDecimal(object.toString());
                    return bigDecimal.intValue();
                }

                if (object instanceof Float || object instanceof Double) {
                    bigDecimal = new BigDecimal(object.toString());
                    return bigDecimal.intValue();
                }
            } else if (Long.class.isAssignableFrom(expect)) {
                if (!(object instanceof String) && !(object instanceof Integer)) {
                    if (!(object instanceof Float) && !(object instanceof Double)) {
                        return object;
                    }

                    bigDecimal = new BigDecimal(object.toString());
                    return bigDecimal.longValue();
                }

                try {
                    Long.parseLong(object.toString());
                } catch (NumberFormatException var5) {
                }

                bigDecimal = new BigDecimal(object.toString());
                return bigDecimal.longValue();
            } else if (Float.class.isAssignableFrom(expect)) {
                if (object instanceof String) {
                    try {
                        Float.parseFloat(object.toString());
                    } catch (NumberFormatException var6) {
                    }

                    bigDecimal = new BigDecimal(object.toString());
                    return bigDecimal.floatValue();
                }

                if (object instanceof Double) {
                    if ((Double)object > 3.4028234663852886E38) {
                    }

                    bigDecimal = new BigDecimal(object.toString());
                    return bigDecimal.floatValue();
                }

                if (object instanceof Integer || object instanceof Long) {
                    bigDecimal = new BigDecimal(object.toString());
                    return bigDecimal.floatValue();
                }
            } else if (Double.class.isAssignableFrom(expect)) {
                if (object instanceof String || object instanceof Float) {
                    try {
                        Double.parseDouble(object.toString());
                    } catch (NumberFormatException var7) {
                    }

                    bigDecimal = new BigDecimal(object.toString());
                    return bigDecimal.doubleValue();
                }

                if (object instanceof Integer || object instanceof Long) {
                    bigDecimal = new BigDecimal(object.toString());
                    return bigDecimal.doubleValue();
                }
            }
        }

        return object;
    }
}
