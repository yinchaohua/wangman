package com.mg.sn.utils.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mg.sn.mill.model.dto.EquipmentDto;
import com.mg.sn.mill.model.entity.Equipment;
import com.mg.sn.mill.service.impl.EquipmentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

public class TypeConvert {

    private final Logger log = LoggerFactory.getLogger(TypeConvert.class);

    /**
     * JSON转MAP
     * @param obj
     * @return
     */
    public static Map JsonToMap (JSON obj) {
        if (obj == null) {
            return null;
        }

        HashMap<Object, Object> map = Maps.newHashMap();
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            Iterator<String> it = jsonObject.keySet().iterator();
            while(it.hasNext()){
                String key = it.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSON) {
                    JSON valueJson = (JSON) value;
                    return JsonToMap(valueJson);
                }
                map.put(key, value);
            }
        }

        if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            if (jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    return JsonToMap(jsonObject);
                }
            }
        }
        return map;
    }

    /**
     * JSON转LIST
     * @param obj  JSON对象
     * @param <T>  转换对象
     * @return
     */
    public static <T> List<T> jsonToClassList (JSON obj, Class<T> cla) {
        List<T> equipmentDtoList = null;

        if (obj == null) {
            return null;
        }

        HashMap<Object, Object> map = Maps.newHashMap();
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            Iterator<String> it = jsonObject.keySet().iterator();
            while(it.hasNext()){
                String key = it.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSON) {
                    JSON valueJson = (JSON) value;
                    return jsonToClassList(valueJson, cla);
                }

            }
        }

        if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            equipmentDtoList = new ArrayList<T>();

            if (jsonArray.size() > 0) {
                equipmentDtoList = JSON.parseArray(jsonArray.toString(), cla);
//                for (int i = 0; i < jsonArray.size(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    Iterator<String> it = jsonObject.keySet().iterator();
//                    EquipmentDto equipmentDto = new EquipmentDto();
//                    while(it.hasNext()) {
//                        String key = it.next();
//                        Object value = jsonObject.get(key);
//                        if (StringUtils.stringEquals(key, "iotId")) {
//                            equipmentDto.setIotid(value.toString());
//                        } else if (StringUtils.stringEquals(key, "mobile")) {
//                            equipmentDto.setContactUserPhone(value.toString());
//                        } else if (StringUtils.stringEquals(key, "userId")) {
//                            equipmentDto.setContactUserId(value.toString());
//                        } else if (StringUtils.stringEquals(key, "mac")) {
//                            equipmentDto.setMac(value.toString());
//                        } else if (StringUtils.stringEquals(key, "qingLianUserId")) {
//                            equipmentDto.setQingLianUserId(value.toString());
//                        } else if (StringUtils.stringEquals(key, "qingLianUserToken")) {
//                            equipmentDto.setQingLianUserToken(value.toString());
//                        }
//                    }
//                    if (equipmentDto != null) {
//                        equipmentDtoList.add(equipmentDto);
//                    }
//                }
            }
        }
        return equipmentDtoList;
    }

//    public staitc <T> T copyPropertiesJson (Class<T> targetClassT, Object source) {
//        if (source == null) {
//            throw new RuntimeException("原数据为空, 转换异常");
//        }
//
//        Object json = (JSONObject)JSONObject.toJSON(source);
//        T target = JSONObject.toJavaObject(json, targetClassT);
//        return target;
//    }
}
