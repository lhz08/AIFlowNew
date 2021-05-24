/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.bdilab.aiflow.model.menu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 子系统菜单表实体类
 *
 * @author BladeX
 * @since 2021-01-21
 */
@Data
public class TransferAmenu  {


	/**
	* 菜单编号
	*/
		@ApiModelProperty(value = "菜单编号")
		private String code;
	/**
	* 菜单名称
	*/
		@ApiModelProperty(value = "菜单名称")
		private String name;


	/**
	* 排序
	*/
		@ApiModelProperty(value = "排序")
		private Integer sort;


	/**
	* 角色类型 0admin 1user
	*/
		@ApiModelProperty(value = "角色类型 0admin 1user")
		private Integer roleType;
	/**
	 * 子菜单
	 */
	private List<TransferAmenu> transferAmenuList;

}
