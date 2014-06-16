package com.rbt.util.exceloperate.bean.expt.config;

import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.Constant;

/**
 * @author Allen
 */
public class StyleInfo extends AbstractStyleInfo {

	/**
	 * 未設定的元素, 自動帶入系統預設
	 */
	public void setEmptyAttrToSystemDefault() {

		// 字型
		if (this.font == null) {
			this.font = Constant.DEFAULT_FONT;
		}
		// 字體大小
		if (StringUtil.isEmpty(this.size)) {
			this.size = Constant.DEFAULT_FONT_SIZE + "";
		}
		//是否加粗
		if (StringUtil.isEmpty(this.bold)) {
			this.bold = Constant.DEFAULT_BOLD;
		}
		//是否斜體
		if (StringUtil.isEmpty(this.italic)) {
			this.italic = Constant.DEFAULT_ITALIC;
		}
		//底線
		if (StringUtil.isEmpty(this.underline)) {
			this.underline = Constant.DEFAULT_UNDERLINE_STYLE;
		}
		//字體顏色
		if (StringUtil.isEmpty(this.color)) {
			this.color = Constant.DEFAULT_COLOR;
		}

		//欄寬 因此欄位會影響整張表, 故輸出前才作判定
		//if (StringUtil.isEmpty(this.width) || "0".equals(this.width.trim())) {
		//	this.width = Constant.WIDTH;
		//}

		//水平位置
		if (StringUtil.isEmpty(this.align)) {
			this.align = Constant.DEFAULT_ALIGN;
		}
		//垂直位置
		if (StringUtil.isEmpty(this.valign)) {
			this.valign = Constant.DEFAULT_VALIGN;
		}
		//自動換行
		if (StringUtil.isEmpty(this.wrap)) {
			this.wrap = Constant.DEFAULT_WRAP;
		}
		//背景顏色
		if (StringUtil.isEmpty(this.background)) {
			this.background = Constant.DEFAULT_BACKGROUND;
		}
		//邊線位置
		if (StringUtil.isEmpty(this.borderSide)) {
			this.borderSide = Constant.DEFAULT_BORDER_SIDE;
		}
		//邊線樣式
		if (StringUtil.isEmpty(this.borderStyle)) {
			this.borderStyle = Constant.DEFAULT_BORDER_STYLE;
		}
	}
}
