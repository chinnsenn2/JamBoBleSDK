package com.jianbao.jamboble.data;


/**
 * 来自蓝牙设备的血压数据
 *
 * @author 毛晓飞
 */
public class OnCallBloodSugarData extends BloodSugarData {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //血糖
    public String strBloodSugar;
    //时间
    public String strDate;

    @Override
    public String toString() {
        return "BloodSugarData [strBloodSugar=" + strBloodSugar + "]";
    }
}