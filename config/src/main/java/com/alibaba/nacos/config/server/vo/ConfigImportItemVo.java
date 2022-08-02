package com.alibaba.nacos.config.server.vo;

/**
 * import config data item.
 *
 * @author ysq
 * @date 2022/7/22 16:38
 */
public class ConfigImportItemVo {

    private String itemName;

    private String dataId;

    private String group;

    private String content;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
