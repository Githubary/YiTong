package org.example.enumerage;

/**
 * description:
 *
 * @author liuhuayang
 * date: 2024/5/9 16:48
 */
public enum Role {

    ADMIN("admin","管理员"),
    USER("user","普通用户"),
    ;

    private final String value;
    private final String desc;
    Role(String value, String desc){
        this.value = value;
        this.desc = desc;
    }
    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}


