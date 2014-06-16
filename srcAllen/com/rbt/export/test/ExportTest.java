package com.rbt.export.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;

import com.google.gson.Gson;
import com.rbt.util.BeanUtil;
import com.rbt.util.db.RbtDbUtilImpl;
import com.rbt.util.exceloperate.ExcelExporter;
import com.rbt.util.exceloperate.bean.expt.ExportDataSet;
import com.rbt.util.exceloperate.bean.expt.config.ExportConfigInfo;
import com.rbt.util.exceloperate.config.ExportConfigReader;
import com.rbt.util.file.FileUtil;

public class ExportTest {

	public ExportTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String configFile = "C:/workspace/RbtProject/srcAllen/com/rbt/export/test/AllenExport.xml";
		String configID = "08-01-01-02";
		String exportFile = "H:/export_test/各校就業率_" + System.currentTimeMillis() + ".xls";
		String dataFile = "H:/export_test/json.txt";

		ExcelExporter excelExporter = new ExcelExporter();
		FileOutputStream out = null;
		Connection conn = null;

		try {

			String jsonText = new FileUtil().readFileText(dataFile, "UTF-8");
			System.out.println(jsonText);
			ExportDataSet exportDataSet = new Gson().fromJson(jsonText, ExportDataSet.class);

//			// //查詢
//			conn = new RbtDbUtilImpl().getConnection();
//			StudMaintainForm studMaintainForm = new StudMaintainForm();
//			studMaintainForm.setQrySchoolChnName("永達");
//
//			ExportDataSet exportDataSet = new SchStudMaintainService().query4Eexport2(conn, studMaintainForm);

			//
			ExportConfigInfo exportConfigInfo = new ExportConfigReader().read(configFile, configID);

			System.out.println(new BeanUtil().showContent(exportDataSet));

			out = new FileOutputStream(new File(exportFile));

			excelExporter.export(exportConfigInfo, exportDataSet, out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
