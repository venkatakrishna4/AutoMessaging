package com.krish.automessaging.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.krish.automessaging.datamodel.record.WhatsAppOptionsRecord;

@Component
public class WhatsAppTemplateUtils {

    public static final String WHATSAPP_MESSAGE_TEXT_TYPE = "text";
    public static final String WHATSAPP_MESSAGE_TEXT_OTP = "otp";
    public static final String WHATSAPP_MESSAGE_TEMPLATE_TYPE = "template";

    public Map<String, Object> getWhatsAppMessageByType(WhatsAppOptionsRecord whatsappOptions) {
        String type = whatsappOptions.type();
        if (StringUtils.isBlank(type)) {
            type = WHATSAPP_MESSAGE_TEXT_TYPE;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("messaging_product", "whatsapp");
        data.put("recipient_type", "individual");
        data.put("to", whatsappOptions.toPhone());
        data.put("type", whatsappOptions.type());
        if (type.equalsIgnoreCase(WHATSAPP_MESSAGE_TEXT_OTP)) {
            data.put("type", WHATSAPP_MESSAGE_TEXT_TYPE);
            data.put(WHATSAPP_MESSAGE_TEXT_TYPE, getMessageBodyByType(type, whatsappOptions));
        } else {
            data.put(getType(whatsappOptions.type()), getMessageBodyByType(type, whatsappOptions));
        }

        return data;
    }

    private String getType(String type) {
        switch (type) {
        case WHATSAPP_MESSAGE_TEMPLATE_TYPE:
            return WHATSAPP_MESSAGE_TEMPLATE_TYPE;
        default:
            return WHATSAPP_MESSAGE_TEXT_TYPE;
        }
    }

    private Map<String, Object> getMessageBodyByType(String type, WhatsAppOptionsRecord whatsappOptions) {
        Map<String, Object> map = new HashMap<>();
        switch (StringUtils.lowerCase(type)) {
        case WHATSAPP_MESSAGE_TEMPLATE_TYPE: {
            map.put("name", "hello_world");
            Map<String, String> templateMap = new HashMap<>();
            templateMap.put("code", "en_US");
            map.put("language", templateMap);
            return map;
        }
        default: {
            map.put("body", whatsappOptions.message());
            return map;
        }
        }
    }
}
