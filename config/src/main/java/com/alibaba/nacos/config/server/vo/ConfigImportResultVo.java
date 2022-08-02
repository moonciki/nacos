package com.alibaba.nacos.config.server.vo;

import com.alibaba.nacos.config.server.model.ConfigAllInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * batch config save detail result.
 *
 * @author ysq
 * @date 2022/7/22 10:14
 */
public class ConfigImportResultVo {

    //新增的
    private List<ConfigAllInfo> newList;

    //修改的
    private List<ConfigAllInfo> modifyList;

    //删除的
    private List<ConfigAllInfo> deleteList;

    //未变更的
    private List<ConfigAllInfo> noChangeList;

    //跳过的
    private List<ConfigAllInfo> skipList;

    //错误中断的
    private List<ConfigImportItemVo> errorList;

    //无法解析的错误
    private List<ConfigImportItemVo> unrecognizedList;

    public List<ConfigAllInfo> getNewList() {
        return newList;
    }

    public void setNewList(List<ConfigAllInfo> newList) {
        this.newList = newList;
    }

    public List<ConfigAllInfo> getModifyList() {
        return modifyList;
    }

    public void setModifyList(List<ConfigAllInfo> modifyList) {
        this.modifyList = modifyList;
    }

    public List<ConfigAllInfo> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(List<ConfigAllInfo> deleteList) {
        this.deleteList = deleteList;
    }

    public List<ConfigAllInfo> getNoChangeList() {
        return noChangeList;
    }

    public void setNoChangeList(List<ConfigAllInfo> noChangeList) {
        this.noChangeList = noChangeList;
    }

    public List<ConfigAllInfo> getSkipList() {
        return skipList;
    }

    public void setSkipList(List<ConfigAllInfo> skipList) {
        this.skipList = skipList;
    }

    public List<ConfigImportItemVo> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ConfigImportItemVo> errorList) {
        this.errorList = errorList;
    }

    public List<ConfigImportItemVo> getUnrecognizedList() {
        return unrecognizedList;
    }

    public void setUnrecognizedList(List<ConfigImportItemVo> unrecognizedList) {
        this.unrecognizedList = unrecognizedList;
    }

    /**
     * add new .
     * @param configInfo configInfo
     */
    public void addNew(ConfigAllInfo configInfo) {
        if (newList == null) {
            newList = new ArrayList<>();
        }
        this.newList.add(configInfo);
    }

    /**
     * add modify .
     * @param configInfo configInfo
     */
    public void addModify(ConfigAllInfo configInfo) {
        if (modifyList == null) {
            modifyList = new ArrayList<>();
        }
        this.modifyList.add(configInfo);
    }

    /**
     * add delete .
     * @param configInfo configInfo
     */
    public void addDelete(ConfigAllInfo configInfo) {
        if (deleteList == null) {
            deleteList = new ArrayList<>();
        }
        this.deleteList.add(configInfo);
    }

    /**
     * add NoChange .
     * @param configInfo configInfo
     */
    public void addNoChange(ConfigAllInfo configInfo) {
        if (noChangeList == null) {
            noChangeList = new ArrayList<>();
        }
        this.noChangeList.add(configInfo);
    }

    /**
     * add skip .
     * @param configInfo configInfo
     */
    public void addSkip(ConfigAllInfo configInfo) {
        if (skipList == null) {
            skipList = new ArrayList<>();
        }
        this.skipList.add(configInfo);
    }

    /**
     * add error.
     * @param configItem configItem
     */
    public void addError(ConfigImportItemVo configItem) {
        if (errorList == null) {
            errorList = new ArrayList<>();
        }
        this.errorList.add(configItem);
    }

    /**
     * add Unrecognized.
     * @param configItem configItem
     */
    public void addUnrecognized(ConfigImportItemVo configItem) {
        if (errorList == null) {
            errorList = new ArrayList<>();
        }
        this.errorList.add(configItem);
    }

}
