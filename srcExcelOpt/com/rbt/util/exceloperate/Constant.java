package com.rbt.util.exceloperate;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableFont;
import jxl.write.WritableFont.FontName;

public class Constant {

	// =========================================================
	// ELEMENT
	// =========================================================
	public static final String ELEMENT_FUNCTION = "function";
	public static final String ELEMENT_FORMAT = "format";
	public static final String ELEMENT_EXCEL = "excel";
	public static final String ELEMENT_STYLE = "style";
	public static final String ELEMENT_SHEET = "sheet";
	public static final String ELEMENT_CONTEXT = "context";
	public static final String ELEMENT_TR = "tr";
	public static final String ELEMENT_TD = "td";
	public static final String ELEMENT_DETAIL = "detail";
	public static final String ELEMENT_COLUMN = "column";
	public static final String ELEMENT_ARRAY = "array";
	public static final String ELEMENT_SINGLE = "single";
	public static final String ELEMENT_READ = "read";
	public static final String ELEMENT_PARAMS = "params";
	public static final String ELEMENT_PARAM = "param";
	public static final String ELEMENT_DEFAULT_VALUE = "defaultValue";
	public static final String ELEMENT_FUNC_PARAM = "funcParam";

	// =========================================================
	// ATTRIBUTE
	// =========================================================
	public static final String ATTRIBUTE_FUNCID = "@funcId";
	public static final String ATTRIBUTE_CLASSNAME = "@className";
	public static final String ATTRIBUTE_METHOD = "@method";
	public static final String ATTRIBUTE_ID = "@id";
	public static final String ATTRIBUTE_FILENAME = "@fileName";
	public static final String ATTRIBUTE_SHEETNAME = "@sheetName";
	public static final String ATTRIBUTE_PAPERSIZE = "@paperSize";
	public static final String ATTRIBUTE_DATAID = "@dataId";
	public static final String ATTRIBUTE_KEY = "@key";
	public static final String ATTRIBUTE_FUNC_PARAM = "@funcParam";
	public static final String ATTRIBUTE_ROWSPAN = "@rowspan";
	public static final String ATTRIBUTE_COLSPAN = "@colspan";
	public static final String ATTRIBUTE_WIDTH = "@width";
	public static final String ATTRIBUTE_FONT = "@font";
	public static final String ATTRIBUTE_SIZE = "@size";
	public static final String ATTRIBUTE_BOLD = "@bold";
	public static final String ATTRIBUTE_ITALIC = "@italic";
	public static final String ATTRIBUTE_UNDERLINE = "@underline";
	public static final String ATTRIBUTE_COLOR = "@color";
	public static final String ATTRIBUTE_ALIGN = "@align";
	public static final String ATTRIBUTE_VALIGN = "@valign";
	public static final String ATTRIBUTE_WRAP = "@wrap";
	public static final String ATTRIBUTE_BACKGROUND = "@background";
	public static final String ATTRIBUTE_BORDERSIDE = "@borderSide";
	public static final String ATTRIBUTE_BORDERSTYLE = "@borderStyle";
	public static final String ATTRIBUTE_SHEETNUM = "@sheetNum";
	public static final String ATTRIBUTE_STARTROW = "@startRow";
	public static final String ATTRIBUTE_CHECK_EMPTY_ROW = "@checkEmptyRow";
	public static final String ATTRIBUTE_CHECK_DUPLICATE = "@checkDuplicate";
	public static final String ATTRIBUTE_DESC = "@desc";
	public static final String ATTRIBUTE_REGEX = "@regex";
	public static final String ATTRIBUTE_CHECK_NULL = "@checkNull";
	public static final String ATTRIBUTE_PASS = "@pass";
	public static final String ATTRIBUTE_INDEX = "@index";
	public static final String ATTRIBUTE_DEFAULT_VALUE = "@defaultValue";
	public static final String ATTRIBUTE_FORMATID = "@formatId";

	// ===========================================================================
	// EXPORT 參數預設值區
	// ===========================================================================
	/**
	 * 預設文字大小
	 */
	public static final int DEFAULT_FONT_SIZE = 13;
	/**
	 * 預設字體設定
	 */
	public static final FontName DEFAULT_FONT = WritableFont.createFont("標楷體");
	/**
	 * 預設粗體設定
	 */
	public static final String DEFAULT_BOLD = "false";
	/**
	 * 預設斜體設定
	 */
	public static final String DEFAULT_ITALIC = "false";
	/**
	 * 預設字底底線設定
	 */
	public static final UnderlineStyle DEFAULT_UNDERLINE_STYLE = UnderlineStyle.NO_UNDERLINE;
	/**
	 * 預設字體顏色設定
	 */
	public static final Colour DEFAULT_COLOR = Colour.BLACK;
	/**
	 * 預設欄寬
	 */
	public static final String DEFAULT_WIDTH = "16";
	/**
	 * 預設水平置中設定
	 */
	public static final Alignment DEFAULT_ALIGN = Alignment.CENTRE;
	/**
	 * 預設垂直置中設定
	 */
	public static final VerticalAlignment DEFAULT_VALIGN = VerticalAlignment.CENTRE;
	/**
	 * 預設文字換行設定
	 */
	public static final String DEFAULT_WRAP = "true";

	/**
	 * 預設邊線位置設定
	 */
	public static final Border DEFAULT_BORDER_SIDE = Border.ALL;
	/**
	 * 預設邊線樣式設定
	 */
	public static final BorderLineStyle DEFAULT_BORDER_STYLE = BorderLineStyle.THIN;
	/**
	 * 預設背景顏色設定
	 */
	public static final Colour DEFAULT_BACKGROUND = Colour.WHITE;
}
