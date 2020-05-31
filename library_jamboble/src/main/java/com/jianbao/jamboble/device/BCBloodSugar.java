package com.jianbao.jamboble.device;

import com.jianbao.jamboble.ValueCast;
import com.jianbao.jamboble.data.BloodSugarData;

/**
 * 百捷血糖仪
 * <p>
 * <p>
 * Ex:06 61 00 E3 07 04 04 09 28 1B 1D B1 11
 * flag(8bit):				06
 * Sequence Number(uint16):	61 00
 * Year(uint16):			E3 07
 * Month(uint8):			04
 * Day(uint8):				04
 * Hours(uint8):			09
 * Minutes(uint8):			28
 * Seconds(uint8):			1B
 * https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.date_time.xml
 * Minutes(uint8):			28
 * Seconds(uint8):			1B
 * Value(SFLOAT IEEE-11073 16-bit SFLOAT):1D B1
 * //幂次方数
 * data[11] = B1;
 * int powUnit = (0xFF & data[11]) >> 4;
 * //血糖, 单位mmol/L
 * data[10] = 1D;
 * float bloodSugar = (float) (((0x0F & data[11]) << 8 | 0xFF & data[10]) * 1000 / Math.pow(10, 16-powUnit)) ;
 *
 * @author 毛晓飞
 */
public class BCBloodSugar extends BeneCheckThreeInOne {

    public BCBloodSugar() {
        super("百捷三合一血糖仪");
    }

    @Override
    public BloodSugarData paserData(byte[] data) {

        //血糖数据解析
        //示列：
        //尿酸：06 51 00 E3 07 04 04 06 22 1B 89 A1 11
        //胆固醇：06 61 00 E3 07 04 04 09 28 1B 1D B1 11
        if (data != null && data.length == 13) {
            if (data[0] == 0x06) {
                if (data[1] == 0x41) { //血糖
                    BloodSugarData btData = new BloodSugarData();
                    btData.bloodSugar = ValueCast.makePrecision(getResultValue(data), 1);
                    return btData;
                }
            }
        }
        return null;
    }

}