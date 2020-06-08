package com.jianbao.jamboble.data;

/**
 * 来自蓝牙设备的体脂数据
 *
 * @author 毛晓飞
 */
public class FatScaleData extends BTData {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	//体重
	public float weight;
	//脂肪
	public float fat;
	//水分
	public float tbw;
	//BMI
	public float bmi;
	//皮下脂肪率
	public float subcutaneousfat;
	//内脏脂肪等级
	public float viscerallevel;
	//骨骼肌率
	public float skeletal;
	//骨量
	public float bonemass;
	//蛋白质含量
	public float proteins;
	//基础代谢
	public float metabolic;
	//体年龄
	public float bodyage;
	//分数
	public float score;
	//体型
	public String bodyshape;

	@Override
	public String toString() {
		return "FatScaleData [weight=" + weight + ", fat=" + fat + ", tbw="
				+ tbw + ", bmi=" + bmi + ", subcutaneousfat="
				+ subcutaneousfat + ", viscerallevel=" + viscerallevel
				+ ", skeletal=" + skeletal + ", bonemass=" + bonemass
				+ ", proteins=" + proteins + ", metabolic=" + metabolic /*+ "bodyage= " + bodyage + "score = " + score + "bodyshape= " + bodyshape*/ + "]";
	}

}