package com.rbt.util.exceloperate.bean.expt.config;

import org.dom4j.Node;

import com.rbt.util.StringUtil;
import com.rbt.util.exceloperate.Constant;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableFont;
import jxl.write.WritableFont.FontName;

/**
 * 和 Style 有關的屬性設定資訊
 * @author Allen
 */
public abstract class AbstractStyleInfo {

	// =====================================================
	// 字體
	// =====================================================
	/**
	 * 字型
	 */
	protected FontName font;
	/**
	 * 字體大小
	 */
	protected String size;
	/**
	 * 是否加粗
	 */
	protected String bold;
	/**
	 * 是否斜體
	 */
	protected String italic;
	/**
	 * 底線
	 */
	protected UnderlineStyle underline;
	/**
	 * 字體顏色
	 */
	protected Colour color;

	// =====================================================
	// Cell 設定
	// =====================================================
	/**
	 * 欄寬
	 */
	protected String width;
	/**
	 * 水平位置
	 */
	protected Alignment align;
	/**
	 * 垂直位置
	 */
	protected VerticalAlignment valign;
	/**
	 * 自動換行
	 */
	protected String wrap;
	/**
	 * 背景顏色
	 */
	protected Colour background;
	/**
	 * 邊線位置
	 */
	protected Border borderSide;
	/**
	 * 邊線樣式
	 */
	protected BorderLineStyle borderStyle;

	// =====================================================
	// 公用程式
	// =====================================================
	/**
	 * 讀取 Node 中,與 style 類型元素 相關的屬性
	 * @param node
	 */
	public void readStyleAttr(Node node) {
		if (node == null) {
			return;
		}
		// 字型
		this.setFont(this.getFont(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_FONT))));
		// 字體大小
		this.setSize(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_SIZE)));
		// 粗體
		this.setBold(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_BOLD)));
		// 斜體
		this.setItalic(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_ITALIC)));
		// 底線
		this.setUnderline(this.getUnderlineStyle(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_UNDERLINE))));
		// 欄寬
		this.setWidth(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_WIDTH)));
		// 文字顏色
		this.setColor(this.getColour(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_COLOR))));
		// 水平位置
		this.setAlign(this.getAlign(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_ALIGN))));
		// 垂直位置
		this.setValign(this.getValign(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_VALIGN))));
		// 自動換行
		this.setWrap(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_WRAP)));
		// 背景顏色
		this.setBackground(this.getColour(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_BACKGROUND))));
		// 邊線位置
		this.setBorderSide(this.getBorderSide(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_BORDERSIDE))));
		// 邊線樣式
		this.setBorderStyle(this.getBorderLineStyle(StringUtil.safeTrim(node.valueOf(Constant.ATTRIBUTE_BORDERSTYLE))));
	}

	// =====================================================
	// 私有 function
	// =====================================================
	/**
	 * 取得水平位置設定
	 * @param align
	 * @return
	 */
	private Alignment getAlign(String align) {
		if ("center".equalsIgnoreCase(align)) {
			return Alignment.CENTRE;
		} else if ("right".equalsIgnoreCase(align)) {
			return Alignment.RIGHT;
		} else if ("left".equalsIgnoreCase(align)) {
			return Alignment.LEFT;
		}
		return null;
	}

	/**
	 * 取得垂直位置設定
	 * @param valign
	 * @return
	 */
	private VerticalAlignment getValign(String valign) {
		if ("center".equalsIgnoreCase(valign)) {
			return VerticalAlignment.CENTRE;
		} else if ("top".equalsIgnoreCase(valign)) {
			return VerticalAlignment.TOP;
		} else if ("botton".equalsIgnoreCase(valign)) {
			return VerticalAlignment.BOTTOM;
		}
		return null;
	}

	/**
	 * 取得粗體設定
	 * @param fontName
	 * @return
	 */
	private FontName getFont(String fontName) {
		if (StringUtil.notEmpty(fontName)) {
			return WritableFont.createFont(fontName);
		}
		return null;
	}

	/**
	 * 取得底線設定
	 * @param underLine
	 * @return
	 */
	private UnderlineStyle getUnderlineStyle(String underLine) {
		if (StringUtil.notEmpty(underLine)) {
			if ("true".equalsIgnoreCase(underLine)) {
				return UnderlineStyle.SINGLE;
			}
			return UnderlineStyle.NO_UNDERLINE;
		}
		return null;
	}

	/**
	 * 取得字體顏色
	 * @param fontColor
	 * @return
	 */
	private Colour getColour(String fontColor) {

		if ("AQUA".equalsIgnoreCase(fontColor)) {
			return Colour.AQUA;
		} else if ("AUTOMATIC".equalsIgnoreCase(fontColor)) {
			return Colour.AUTOMATIC;
		} else if ("BLACK".equalsIgnoreCase(fontColor)) {
			return Colour.BLACK;
		} else if ("BLUE_GREY".equalsIgnoreCase(fontColor)) {
			return Colour.BLUE_GREY;
		} else if ("BLUE".equalsIgnoreCase(fontColor)) {
			return Colour.BLUE;
		} else if ("BLUE2".equalsIgnoreCase(fontColor)) {
			return Colour.BLUE2;
		} else if ("BRIGHT_GREEN".equalsIgnoreCase(fontColor)) {
			return Colour.BRIGHT_GREEN;
		} else if ("BROWN".equalsIgnoreCase(fontColor)) {
			return Colour.BROWN;
		} else if ("CORAL".equalsIgnoreCase(fontColor)) {
			return Colour.CORAL;
		} else if ("DARK_BLUE".equalsIgnoreCase(fontColor)) {
			return Colour.DARK_BLUE;
		} else if ("DARK_BLUE2".equalsIgnoreCase(fontColor)) {
			return Colour.DARK_BLUE2;
		} else if ("DARK_GREEN".equalsIgnoreCase(fontColor)) {
			return Colour.DARK_GREEN;
		} else if ("DARK_PURPLE".equalsIgnoreCase(fontColor)) {
			return Colour.DARK_PURPLE;
		} else if ("DARK_RED".equalsIgnoreCase(fontColor)) {
			return Colour.DARK_RED;
		} else if ("DARK_RED2".equalsIgnoreCase(fontColor)) {
			return Colour.DARK_RED2;
		} else if ("DARK_TEAL".equalsIgnoreCase(fontColor)) {
			return Colour.DARK_TEAL;
		} else if ("DARK_YELLOW".equalsIgnoreCase(fontColor)) {
			return Colour.DARK_YELLOW;
		} else if ("GOLD".equalsIgnoreCase(fontColor)) {
			return Colour.GOLD;
		} else if ("GRAY_25".equalsIgnoreCase(fontColor)) {
			return Colour.GRAY_25;
		} else if ("GRAY_50".equalsIgnoreCase(fontColor)) {
			return Colour.GRAY_50;
		} else if ("GRAY_80".equalsIgnoreCase(fontColor)) {
			return Colour.GRAY_80;
		} else if ("GREEN".equalsIgnoreCase(fontColor)) {
			return Colour.GREEN;
		} else if ("GREY_25_PERCENT".equalsIgnoreCase(fontColor)) {
			return Colour.GREY_25_PERCENT;
		} else if ("GREY_40_PERCENT".equalsIgnoreCase(fontColor)) {
			return Colour.GREY_40_PERCENT;
		} else if ("GREY_50_PERCENT".equalsIgnoreCase(fontColor)) {
			return Colour.GREY_50_PERCENT;
		} else if ("GREY_80_PERCENT".equalsIgnoreCase(fontColor)) {
			return Colour.GREY_80_PERCENT;
		} else if ("ICE_BLUE".equalsIgnoreCase(fontColor)) {
			return Colour.ICE_BLUE;
		} else if ("INDIGO".equalsIgnoreCase(fontColor)) {
			return Colour.INDIGO;
		} else if ("IVORY".equalsIgnoreCase(fontColor)) {
			return Colour.IVORY;
		} else if ("LAVENDER".equalsIgnoreCase(fontColor)) {
			return Colour.LAVENDER;
		} else if ("LIGHT_BLUE".equalsIgnoreCase(fontColor)) {
			return Colour.LIGHT_BLUE;
		} else if ("LIGHT_GREEN".equalsIgnoreCase(fontColor)) {
			return Colour.LIGHT_GREEN;
		} else if ("LIGHT_ORANGE".equalsIgnoreCase(fontColor)) {
			return Colour.LIGHT_ORANGE;
		} else if ("LIGHT_TURQUOISE".equalsIgnoreCase(fontColor)) {
			return Colour.LIGHT_TURQUOISE;
		} else if ("LIGHT_TURQUOISE2".equalsIgnoreCase(fontColor)) {
			return Colour.LIGHT_TURQUOISE2;
		} else if ("LIME".equalsIgnoreCase(fontColor)) {
			return Colour.LIME;
		} else if ("OCEAN_BLUE".equalsIgnoreCase(fontColor)) {
			return Colour.OCEAN_BLUE;
		} else if ("OLIVE_GREEN".equalsIgnoreCase(fontColor)) {
			return Colour.OLIVE_GREEN;
		} else if ("ORANGE".equalsIgnoreCase(fontColor)) {
			return Colour.ORANGE;
		} else if ("PALE_BLUE".equalsIgnoreCase(fontColor)) {
			return Colour.PALE_BLUE;
		} else if ("PERIWINKLE".equalsIgnoreCase(fontColor)) {
			return Colour.PERIWINKLE;
		} else if ("PINK".equalsIgnoreCase(fontColor)) {
			return Colour.PINK;
		} else if ("PINK2".equalsIgnoreCase(fontColor)) {
			return Colour.PINK2;
		} else if ("PLUM".equalsIgnoreCase(fontColor)) {
			return Colour.PLUM;
		} else if ("PLUM2".equalsIgnoreCase(fontColor)) {
			return Colour.PLUM2;
		} else if ("RED".equalsIgnoreCase(fontColor)) {
			return Colour.RED;
		} else if ("ROSE".equalsIgnoreCase(fontColor)) {
			return Colour.ROSE;
		} else if ("SEA_GREEN".equalsIgnoreCase(fontColor)) {
			return Colour.SEA_GREEN;
		} else if ("SKY_BLUE".equalsIgnoreCase(fontColor)) {
			return Colour.SKY_BLUE;
		} else if ("TAN".equalsIgnoreCase(fontColor)) {
			return Colour.TAN;
		} else if ("TEAL".equalsIgnoreCase(fontColor)) {
			return Colour.TEAL;
		} else if ("TEAL2".equalsIgnoreCase(fontColor)) {
			return Colour.TEAL2;
		} else if ("TURQOISE2".equalsIgnoreCase(fontColor)) {
			return Colour.TURQOISE2;
		} else if ("TURQUOISE".equalsIgnoreCase(fontColor)) {
			return Colour.TURQUOISE;
		} else if ("VERY_LIGHT_YELLOW".equalsIgnoreCase(fontColor)) {
			return Colour.VERY_LIGHT_YELLOW;
		} else if ("VIOLET".equalsIgnoreCase(fontColor)) {
			return Colour.VIOLET;
		} else if ("VIOLET2".equalsIgnoreCase(fontColor)) {
			return Colour.VIOLET2;
		} else if ("WHITE".equalsIgnoreCase(fontColor)) {
			return Colour.WHITE;
		} else if ("YELLOW".equalsIgnoreCase(fontColor)) {
			return Colour.YELLOW;
		} else if ("YELLOW2".equalsIgnoreCase(fontColor)) {
			return Colour.YELLOW2;
		}

		return null;
	}

	/**
	 * 取得邊線設定
	 * @param borderSide
	 * @return
	 */
	private Border getBorderSide(String borderSide) {
		// NONE|ALL|TOP|BOTTOM|LEFT|RIGHT

		if ("NONE".equalsIgnoreCase(borderSide)) {
			return Border.NONE;
		} else if ("ALL".equalsIgnoreCase(borderSide)) {
			return Border.ALL;
		} else if ("TOP".equalsIgnoreCase(borderSide)) {
			return Border.TOP;
		} else if ("BOTTOM".equalsIgnoreCase(borderSide)) {
			return Border.BOTTOM;
		} else if ("LEFT".equalsIgnoreCase(borderSide)) {
			return Border.LEFT;
		} else if ("RIGHT".equalsIgnoreCase(borderSide)) {
			return Border.RIGHT;
		}
		return null;
	}

	/**
	 * 取得邊線樣式設定 BorderLineStyle
	 * NONE 無 <br/>
	 * THIN 薄 <br/>
	 * MEDIUM 中等的 <br/>
	 * DASHED 虛線 <br/>
	 * DOTTED 點綴 <br/>
	 * THICK 厚<br/>
	 * DOUBLE 雙 <br/>
	 * HAIR 毛髮<br/>
	 * MEDIUM_DASHED 中等虛線<br/>
	 * DASH_DOT 點劃線<br/>
	 * MEDIUM_DASH_DOT 中等點劃線<br/>
	 * DASH_DOT_DOT 點點劃線<br/>
	 * MEDIUM_DASH_DOT_DOT 中等點點劃線<br/>
	 * SLANTED_DASH_DOT 斜沖點<br/>
	 * @param borderStyle
	 * @return
	 */
	private BorderLineStyle getBorderLineStyle(String borderStyle) {

		if ("NONE".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.NONE;
		} else if ("THIN".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.THIN;
		} else if ("MEDIUM".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.MEDIUM;
		} else if ("DASHED".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.DASHED;
		} else if ("THICK".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.THICK;
		} else if ("DOUBLE".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.DOUBLE;
		} else if ("HAIR".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.HAIR;
		} else if ("MEDIUM_DASHED".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.MEDIUM_DASHED;
		} else if ("DASH_DOT".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.DASH_DOT;
		} else if ("MEDIUM_DASH_DOT".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.MEDIUM_DASH_DOT;
		} else if ("DASH_DOT_DOT".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.DASH_DOT_DOT;
		} else if ("MEDIUM_DASH_DOT_DOT".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.MEDIUM_DASH_DOT_DOT;
		} else if ("DASH_DOT_DOT".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.DASH_DOT_DOT;
		} else if ("MEDIUM_DASH_DOT_DOT".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.MEDIUM_DASH_DOT_DOT;
		} else if ("SLANTED_DASH_DOT".equalsIgnoreCase(borderStyle)) {
			return BorderLineStyle.SLANTED_DASH_DOT;
		}
		return null;
	}

	// =====================================================
	// getter && setter
	// =====================================================

	/**
	 * @return the font
	 */
	public FontName getFont() {
		return this.font;
	}

	/**
	 * @param font the font to set
	 */
	public void setFont(FontName font) {
		this.font = font;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		return this.size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @return the bold
	 */
	public String getBold() {
		return this.bold;
	}

	/**
	 * @param bold the bold to set
	 */
	public void setBold(String bold) {
		this.bold = bold;
	}

	/**
	 * @return the italic
	 */
	public String getItalic() {
		return this.italic;
	}

	/**
	 * @param italic the italic to set
	 */
	public void setItalic(String italic) {
		this.italic = italic;
	}

	/**
	 * @return the underline
	 */
	public UnderlineStyle getUnderline() {
		return this.underline;
	}

	/**
	 * @param underline the underline to set
	 */
	public void setUnderline(UnderlineStyle underline) {
		this.underline = underline;
	}

	/**
	 * @return the color
	 */
	public Colour getColor() {
		return this.color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Colour color) {
		this.color = color;
	}

	/**
	 * @return the wrap
	 */
	public String getWrap() {
		return this.wrap;
	}

	/**
	 * @param wrapText the wrap to set
	 */
	public void setWrap(String wrap) {
		this.wrap = wrap;
	}

	/**
	 * @return the align
	 */
	public Alignment getAlign() {
		return this.align;
	}

	/**
	 * @param align the align to set
	 */
	public void setAlign(Alignment align) {
		this.align = align;
	}

	/**
	 * @return the valign
	 */
	public VerticalAlignment getValign() {
		return this.valign;
	}

	/**
	 * @param valign the valign to set
	 */
	public void setValign(VerticalAlignment valign) {
		this.valign = valign;
	}

	/**
	 * @return the background
	 */
	public Colour getBackground() {
		return this.background;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackground(Colour background) {
		this.background = background;
	}

	/**
	 * @return the borderSide
	 */
	public Border getBorderSide() {
		return this.borderSide;
	}

	/**
	 * @param borderSide the borderSide to set
	 */
	public void setBorderSide(Border borderSide) {
		this.borderSide = borderSide;
	}

	/**
	 * @return the borderStyle
	 */
	public BorderLineStyle getBorderStyle() {
		return this.borderStyle;
	}

	/**
	 * @param borderStyle the borderStyle to set
	 */
	public void setBorderStyle(BorderLineStyle borderStyle) {
		this.borderStyle = borderStyle;
	}

	/**
	 * @return the width
	 */
	public String getWidth() {
		return this.width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

}
