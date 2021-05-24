package com.bdilab.aiflow.model.menu;

import lombok.Data;

import java.util.List;


@Data
public class MenuInfo {
    /**
     * MD5加密签名
     */
    private String sign;
	/**
	 * 应用编码
	 */
	private String codingId;
    /**
     * Amenu对象集合
     */
    private List<TransferAmenu> transferAmenuList;


}
