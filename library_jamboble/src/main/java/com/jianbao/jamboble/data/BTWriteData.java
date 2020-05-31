package com.jianbao.jamboble.data;



/**
 * 返回下一步写入蓝牙的数据
 * @author 毛晓飞
 *
 */
public class BTWriteData extends BTData{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//血糖
	public byte[] command;
}