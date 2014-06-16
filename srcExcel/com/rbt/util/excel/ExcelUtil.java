package com.rbt.util.excel;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import com.rbt.util.DateUtil;
import com.rbt.util.FmtUtil;
import com.rbt.util.StringUtil;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import jxl.*;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WritableFont;
import jxl.format.*;

/**
 */
public class ExcelUtil {

	public static int BLOCK_ALIGN_LEFT = 1;
	public static int BLOCK_ALIGN_CENTER = 2;
	public static int BLOCK_ALIGN_RIGHT = 3;

	public static PageSize PAGE_SIZE_A2 = (new ExcelUtil()).new PageSize(PaperSize.A2);
	public static PageSize PAGE_SIZE_A3 = (new ExcelUtil()).new PageSize(PaperSize.A3);
	public static PageSize PAGE_SIZE_A4 = (new ExcelUtil()).new PageSize(PaperSize.A4);
	public static PageSize PAGE_SIZE_B4 = (new ExcelUtil()).new PageSize(PaperSize.B4);
	public static PageSize PAGE_SIZE_B5 = (new ExcelUtil()).new PageSize(PaperSize.B5);

	public static Orientation ORIENTAION_LANDSCAPE = (new ExcelUtil()).new Orientation(PageOrientation.LANDSCAPE);
	public static Orientation ORIENTAION_PORTRAIT = (new ExcelUtil()).new Orientation(PageOrientation.PORTRAIT);

	/**
	 * 區塊資料結構定義, 用來定義 表頭(PageHeader) 及 表尾(PageFooter) 的單一區塊內容 <br/>
	 * 表頭(PageHeader) 及 表尾(PageFooter) 分別由多個區塊所組成
	 */
	public class BlockInfo {

		BlockInfo() {
		}

		/**
		 * 要顯示的字串
		 */
		public String TitleStr = "";

		/**
		 * 字型尺寸, 預設為13pt
		 */
		public int FontSize = 13;

		/**
		 * 字串的顯示位置(cell-X)
		 */
		public int pos_x = 0;

		/**
		 * 字串的顯示位置(cell-Y)
		 */
		public int pos_y = 0;

		/**
		 * 字串在區塊中顯示時的對齊方式, {BLOCK_ALIGN_LEFT, BLOCK_ALIGN_CENTER, BLOCK_ALIGN_RIGHT}
		 */
		public int align = BLOCK_ALIGN_LEFT;

		/**
		 * 合併儲存格數
		 */
		public int merge_x = 1;
		public int merge_y = 1;
	}

	public class PageSize {
		private PaperSize paperSize;

		PageSize(PaperSize size) {
			this.paperSize = size;
		}

		PaperSize get() {
			return this.paperSize;
		}
	}

	public class Orientation {
		private PageOrientation orientation;

		Orientation(PageOrientation orientation) {
			this.orientation = orientation;
		}

		PageOrientation get() {
			return this.orientation;
		}
	}

	/**
	 * 輸出 Excel 頁面/版面 的資訊結構
	 * @author EricHuang
	 *
	 */
	public class PageInfo {
		public int HeaderFontSize = 9;
		public int DataFontSize = 9;

		public PageSize PageSize = PAGE_SIZE_A4;
		public Orientation Orientation = ORIENTAION_LANDSCAPE;

		public List<BlockInfo> TitleList = new LinkedList<BlockInfo>();
		public List<BlockInfo> FootList = new LinkedList<BlockInfo>();

		PageInfo() {
		}
	}

	/**
	 * 資料表格 的定義
	 * @author EricHuang
	 *
	 */
	public class ColumnInfo {

		ColumnInfo() {
		}

		/**
		 * 要輸出的資料欄位名稱(field-name)列表
		 */
		public String[] fields = null;

		/**
		 * 對應的欄位標題(header)列表
		 */
		public String[] headers = null;

		/**
		 * 對應的欄位寬度(字數)設定
		 */
		public Integer[] widths = null;

		/**
		 * 對應的自定義資料類型列表, 主要用來支援 'TIME' 類型(文字型態的 date-time yyyyMMddHHmmss 格式), 輸出格式 yyyy-MM-dd HH:mm:ss <br>
		 * 其他類型可以不用定義, 自動判斷是否為 java.lang.Date 類型, 預設輸出格式 yyyy-MM-dd HH:mm:ss <br>
		 * 除上列外其他類型, 一律以字串的方式輸出 (toString)
		 */
		public String[] types = null;

		/**
		 * 對應的 date-time format 格式列表, 以支援由外部定義 java.lang.Date 的輸出格式
		 */
		public String[] datetimeFormat = null;

	}

	protected static Logger LOG = Logger.getLogger(ExcelUtil.class);

	public static PageInfo createPageInfo() {
		return (new ExcelUtil()).new PageInfo();
	}

	public static BlockInfo createBlockInfo() {
		return (new ExcelUtil()).new BlockInfo();
	}

	public static ColumnInfo createColumnInfo() {
		return (new ExcelUtil()).new ColumnInfo();
	}

	/**
	 * 格式化類別:時間
	 */
	public static final String FORMAT_TYPE_TIME = "TIME";
	/**
	 * 格式化類別:數字 (請傳入 FORMAT_TYPE_NUMBER + 小數位數)
	 */
	public static final String FORMAT_TYPE_NUMBER = "NUMBER";

	/**
	 * 產生 Excel 檔案, 並經由OutputStream out 輸出
	 * @param data 要輸出的資料表格來源
	 * @param pageInfo PageInfo
	 * @param ColumnInfo ColumnInfo
	 * @param out OutputStream
	 */
	public static void writeExcel(List<Map<String, Object>> data, PageInfo pageInfo, ColumnInfo columnInfo, OutputStream out) {

		try {
			int headerFontSize = pageInfo != null ? pageInfo.HeaderFontSize : 10;
			int dataFontSize = pageInfo != null ? pageInfo.DataFontSize : 10;

			List<BlockInfo> titleList = pageInfo != null ? pageInfo.TitleList : null;
			List<BlockInfo> footList = pageInfo != null ? pageInfo.FootList : null;

			String[] fields = columnInfo.fields;
			String[] headers = columnInfo.headers;
			Integer[] widths = columnInfo.widths;
			String[] types = columnInfo.types;
			String[] datetimeFormat = columnInfo.datetimeFormat;

			WritableWorkbook writableWorkbook = Workbook.createWorkbook(out);
			WritableSheet writableSheet = writableWorkbook.createSheet("Sheet1", 0);
			writableSheet.setPageSetup(pageInfo.Orientation.get(), pageInfo.PageSize.get(), 0, 0);
			writableSheet.getSettings().setLeftMargin(0);
			writableSheet.getSettings().setRightMargin(0);

			// 標題列字型
			jxl.write.WritableFont writableFont_Header = new jxl.write.WritableFont(
					WritableFont.ARIAL,
					headerFontSize,
					WritableFont.BOLD,
					false,
					UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK);

			// 資料列字型
			jxl.write.WritableFont writableFont_Data = new jxl.write.WritableFont(
					WritableFont.ARIAL,
					dataFontSize,
					WritableFont.NO_BOLD,
					false,
					UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.BLACK);

			// 標題列cell格式
			jxl.write.WritableCellFormat writableCellFormat_Header = new jxl.write.WritableCellFormat(writableFont_Header);
			writableCellFormat_Header.setAlignment(jxl.format.Alignment.CENTRE); // 設定欄位資料置中
			writableCellFormat_Header.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); // 設定欄位邊框格式
			writableCellFormat_Header.setWrap(true);

			// 資料列cell格式
			jxl.write.WritableCellFormat writableCellFormat_Data = new jxl.write.WritableCellFormat(writableFont_Data);
			// wcfFCData.setAlignment(jxl.format.Alignment.CENTRE); //設定欄位資料置中
			writableCellFormat_Data.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); // 設定欄位邊框格式
			writableCellFormat_Data.setWrap(true); // 可換行
			writableCellFormat_Data.setVerticalAlignment(VerticalAlignment.CENTRE);

			int beginLine = 0;

			// write Titles
			if (titleList != null) {
				for (BlockInfo title : titleList) {

					// Title字型
					jxl.write.WritableFont wfcTitle = new jxl.write.WritableFont(WritableFont.ARIAL, title.FontSize, WritableFont.BOLD, false,
							UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);

					// Title cell格式
					jxl.write.WritableCellFormat wcfFCTitle = new jxl.write.WritableCellFormat(wfcTitle);
					wcfFCTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.NONE); // 設定欄位邊框格式

					if (title.align == BLOCK_ALIGN_RIGHT) {
						wcfFCTitle.setAlignment(jxl.format.Alignment.RIGHT);
					} else if (title.align == BLOCK_ALIGN_CENTER) {
						wcfFCTitle.setAlignment(jxl.format.Alignment.CENTRE);
					} else {
						wcfFCTitle.setAlignment(jxl.format.Alignment.LEFT);
					}

					writableSheet.addCell(new Label(title.pos_x, title.pos_y, title.TitleStr, wcfFCTitle));

					// 合併儲存格
					if (title.merge_x > 1) {
						writableSheet.mergeCells(title.pos_x, title.pos_y, title.pos_x + title.merge_x - 1, title.pos_y);
					}

					// 合併儲存格
					if (title.merge_y > 1) {
						writableSheet.mergeCells(title.pos_x, title.pos_y, title.pos_x, title.pos_y + title.merge_y - 1);
					}

					if (beginLine < title.pos_y) {
						beginLine = title.pos_y;
					}

				}
			}
			beginLine++;
			// write Headers
			for (int x = 0; x < fields.length; x++) {
				String header = fields[x];
				if (headers != null && headers.length >= fields.length) {
					header = headers[x];
				}
				writableSheet.addCell(new Label(x, beginLine, header, writableCellFormat_Header));
			}

			// 設定欄寬
			if (widths != null) {
				for (int x = 0; x < fields.length; x++) {
					if (x < widths.length && widths[x] != null) {
						writableSheet.setColumnView(x, widths[x]);
					}
				}
			}

			// 資料列輸出
			beginLine++;
			for (int y = 0; y < data.size(); y++) {
				Map<String, Object> row = data.get(y);

				for (int x = 0; x < fields.length; x++) {
					Object val = row.get(fields[x]);
					String valStr = StringUtil.safeTrim(val);

					if (StringUtil.notEmpty(types[x])) {
						if (types[x].equals(FORMAT_TYPE_TIME)) {
							//============
							// 時間
							//============
							if (valStr != null) {
								valStr = DateUtil.formateDateTimeForUser(valStr);
							}
						} else if (types[x].startsWith(FORMAT_TYPE_NUMBER)) {
							//============
							// 小數位
							//============
							int decimalDigits = 0;
							if (types[x].length() > FORMAT_TYPE_NUMBER.length()) {
								decimalDigits = Integer.parseInt(types[x].substring(FORMAT_TYPE_NUMBER.length()));
							}
							valStr = FmtUtil.format(valStr, decimalDigits);
						}
					} else {
						if (val == null) {
							valStr = "";
						} else if (val instanceof java.util.Date) {
							String format = "yyyy-MM-dd HH:mm:ss";
							if (datetimeFormat != null && datetimeFormat.length > x && datetimeFormat[x] != null) {
								format = datetimeFormat[x];
							}
							valStr = format((Date) val, format);
						} else {
							valStr = val + "";
						}
					}
					writableSheet.addCell(new Label(x, beginLine + y, valStr, writableCellFormat_Data));
				}
			}

			// Page Foot 輸出
			beginLine += data.size();
			if (footList != null) {
				for (int y = 0; y < footList.size(); y++) {
					BlockInfo foot = footList.get(y);

					// Title字型
					jxl.write.WritableFont wfcTitle = new jxl.write.WritableFont(WritableFont.ARIAL, foot.FontSize, WritableFont.BOLD, false,
							UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);

					// Title cell格式
					jxl.write.WritableCellFormat wcfFCTitle = new jxl.write.WritableCellFormat(wfcTitle);
					wcfFCTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.NONE); // 設定欄位邊框格式

					if (foot.align == BLOCK_ALIGN_RIGHT) {
						wcfFCTitle.setAlignment(jxl.format.Alignment.RIGHT);
					} else if (foot.align == BLOCK_ALIGN_CENTER) {
						wcfFCTitle.setAlignment(jxl.format.Alignment.CENTRE);
					} else {
						wcfFCTitle.setAlignment(jxl.format.Alignment.LEFT);
					}

					writableSheet.addCell(new Label(foot.pos_x, beginLine + foot.pos_y, foot.TitleStr, wcfFCTitle));

					// 合併儲存格
					if (foot.merge_x > 1) {
						writableSheet.mergeCells(foot.pos_x, beginLine + foot.pos_y, foot.pos_x + foot.merge_x - 1, beginLine + foot.pos_y);
					}

					// 合併儲存格
					if (foot.merge_y > 1) {
						writableSheet.mergeCells(foot.pos_x, beginLine + foot.pos_y, foot.pos_x, beginLine + foot.pos_y + foot.merge_y - 1);
					}

				}
			}
			writableWorkbook.write();
			writableWorkbook.close();
		} catch (Exception ex) {
			LOG.error(StringUtil.getExceptionStackTrace(ex));
			throw new RuntimeException("ExcelUtil.writeExcel(): " + ex.getMessage(), ex);
		}
	}

	/**
	 * get DataRow
	 * @param sheelIndex begin from 0
	 * @param RowIdx begin from 0
	 * @param ColumnCount MAX Columns will be read
	 * @param stream Excel File Source InputStream
	 * @return List<Object>
	 */
	public static List<Object> getDataRow(int sheelIndex, int RowIdx, int ColumnCount, InputStream stream) {
		LOG.debug("ExcelUtil.getDataRow begin");

		LinkedList<Object> Cols = new LinkedList<Object>();

		try {
			Workbook book = Workbook.getWorkbook(stream);
			Sheet sheel = book.getSheet(sheelIndex);
			if (RowIdx < sheel.getRows()) {
				Cell cell[] = sheel.getRow(RowIdx);

				for (int j = 0; j < ColumnCount; j++) {
					jxl.format.Format format = cell[j].getCellFormat() == null ? null : cell[j].getCellFormat().getFormat();

					if (format != null && format.getFormatString().equals("") == false) {
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(format.getFormatString());
							Cols.add(sdf.parse(cell[j].getContents()));
						} catch (Exception exception) {
							Cols.add(cell[j].getContents());
						}
					} else {
						Cols.add(cell[j].getContents());
					}
				}

			}
		} catch (Exception e) {
			LOG.error("ExcelUtil.getDataRow Exception: " + e.getMessage(), e);
		}

		return Cols;
	}

	/**
	 * get Data ByKey
	 * @return List<HashMap<String, Object>>
	 */
	public static List<HashMap<String, Object>> getDataByKey(int sheelIndex, int BeginRow, String key[], InputStream stream) {

		ArrayList<HashMap<String, Object>> listOfMap = new ArrayList<HashMap<String, Object>>();
		try {
			Workbook book = Workbook.getWorkbook(stream);
			Sheet sheel = book.getSheet(sheelIndex);
			for (int i = BeginRow; i < sheel.getRows(); i++) {
				Cell cell[] = sheel.getRow(i);
				// *********[row data check]
				if (cell[0].getContents() == null || cell[0].getContents().equals("") == true) {

					continue;
				}

				// *********[process row data]
				// Object data[] = new Object[cell.length];
				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				for (int j = 0; j < key.length; j++) {

					if (j >= cell.length) {
						dataMap.put(key[j], "");
						continue;
					}

					jxl.format.Format format = null;
					try {
						if (cell[j].getCellFormat() != null) {
							format = cell[j].getCellFormat().getFormat();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					if (format != null && !"".equals(format.getFormatString())) {
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(format.getFormatString());
							dataMap.put(key[j], sdf.parse(cell[j].getContents()));
						} catch (Exception exception) {
							dataMap.put(key[j], cell[j].getContents());
						}
					} else {
						dataMap.put(key[j], cell[j].getContents());
					}
				}
				LOG.debug("Row(" + i + "): " + dataMap);

				// *********[add row data to list]
				listOfMap.add(dataMap);

			}
		} catch (Exception e) {
			LOG.error("ExcelUtil.getDataByKey Exception: " + e.getMessage(), e);
		}

		return listOfMap;
	}

	public static String format(Date date, String pattern) {
		return format(date, pattern, false);
	}

	public static String format(Date date, String pattern, boolean nullable) {
		if (date == null || pattern == null) {
			if (nullable)
				return null;
			throw new NullPointerException();
		}

		return DateFormatUtils.format(date, pattern);
	}
}