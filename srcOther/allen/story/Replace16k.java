/**
 *
 */
package allen.story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.rbt.util.StringUtil;
import com.rbt.util.file.FileUtil;
import com.rbt.util.readfile.PropertiesUtil;

/**
 * @author Allen
 *
 */
public class Replace16k {

	/**
	 * key 只含有單字元的設定 map
	 */
	private HashMap<String, String> singlePropertiesMap = new HashMap<String, String>();
	/**
	 * key 只含有單字元的設定 PropertyBean List
	 */
	private List<PropertyBean> singlePropertiesList = new ArrayList<PropertyBean>();
	/**
	 * key 含有雙字元的設定 PropertyBean List
	 */
	private List<PropertyBean> multiPropertiesList = new ArrayList<PropertyBean>();

	/**
	 * 需要用 Replace 方式處理的字串 Bean
	 *
	 * @author Allen
	 */
	public class PropertyBean implements Comparable<PropertyBean> {

		public PropertyBean(String key, String content) {
			this.key = key;
			this.content = content;
		}

		String key = "";
		String content = "";

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(PropertyBean o) {
			int thisLength = this.key.length();
			int anotherLength = o.key.length();

			// 先比長度
			if (thisLength != anotherLength) {
				return (anotherLength > thisLength) ? 1 : -1;
			}
			// 再比順序
			// (由小到大)
			// return this.key.compareTo(o.key); (由小到大)
			// (由大到小)
			return o.key.compareTo(this.key);
		}
	}

	/**
	 * 建構子
	 */
	public Replace16k() {
		// 讀取設定檔
		this.readProperties();
	}

	/**
	 * 讀取設定檔
	 */
	public void readProperties() {

		// =======================================================
		// 讀檔
		// =======================================================
		// 讀檔
		HashMap<String, String> propertiesMap = new PropertiesUtil().read(Replace16k.class);

		// =======================================================
		// 區分含有雙字元的設定
		// =======================================================

		for (Entry<String, String> entry : propertiesMap.entrySet()) {
			// 將含有雙字元部分的設定獨立出來 (空白算雙字元組)
			if (entry.getKey().length() != entry.getKey().getBytes().length || //(byte長度不一樣)
					checkSpcChar(entry.getKey())){
				// 含有雙字元部分
				this.multiPropertiesList.add(new PropertyBean(entry.getKey(), entry.getValue()));
			} else {
				// 單字元部分
				this.singlePropertiesMap.put(entry.getKey(), entry.getValue());
				this.singlePropertiesList.add(new PropertyBean(entry.getKey(), entry.getValue()));

			}
		}

		// 排序 (由長->短)
		Collections.sort(this.singlePropertiesList);
		Collections.sort(this.multiPropertiesList);
	}

	
	/**
	 * 有特殊字元亦視為雙字
	 * @param in
	 * @return
	 */
	private boolean checkSpcChar(String in) {
		String s[] = { " ", ",", ";" ,":"};
		for (String chr : s) {
			if(in.indexOf(chr)>-1){
				return true;
			}

		}
		return false;
	}

	/**
	 * 取代檔案內容
	 *
	 * @throws Exceptioe
	 */
	public void replaceContent(String filePath, String fileEncodeing) throws Exception {

		// =======================================================
		// 取得檔案內容
		// =======================================================

		// 讀檔
		String contentLines[] = new FileUtil().readFileContent(filePath, fileEncodeing);

		// =======================================================
		// replace 雙字元第一次
		// =======================================================
		for (int i = 0; i < contentLines.length; i++) {
			for (PropertyBean PropertyBean : this.multiPropertiesList) {
				contentLines[i] = contentLines[i].replaceAll(PropertyBean.key, PropertyBean.content);
			}
		}

		// =======================================================
		// 取代單字元部分(純英文)
		// =======================================================
		LinkedHashMap<String, Integer> undefinedMap = new LinkedHashMap<String, Integer>();
		FileUtil fu = new FileUtil();

		for (String content : contentLines) {

			String charBuffer = "";

			for (int i = 0; i < content.length(); i++) {

				// 由行中取出一字
				String chat = content.substring(i, i + 1);

				// 判斷單雙字元 (空白視為雙字元,) (直接進入判斷)
				if (chat.getBytes().length > 1 || 
						" ".equals(chat) || 
						i == content.length() - 1) {
					// ==============================
					// 雙字元
					// ==============================

					// 若已開始額取 keyWord, 則在此轉換並終止(直接將暫存之整串字存入)
					if (charBuffer.length() > 0) {
						String keyContent = this.singlePropertiesMap.get(charBuffer);
						if (!this.singlePropertiesMap.containsKey(charBuffer)) {
							fu.addStr(charBuffer);
							//排除單字
							if (charBuffer.length() > 1) {
								//判斷非數字
								if (!StringUtil.isNumber(charBuffer)) {

									if (undefinedMap.containsKey(charBuffer)) {
										undefinedMap.put(charBuffer, undefinedMap.get(charBuffer) + 1);
									} else {
										undefinedMap.put(charBuffer, 1);
									}
								}
							}
						} else {
							fu.addStr(keyContent);
						}
						charBuffer = "";
					}

					fu.addStr(chat);
				} else {
					// ==============================
					// 單字元
					// ==============================
					// 將本字先放入暫存
					charBuffer += chat;
				}
			}
			fu.addLine();
		}

		// =======================================================
		// replace 雙字元第二次 (連字校正)
		// =======================================================
		for (int i = 0; i < contentLines.length; i++) {
			for (PropertyBean PropertyBean : this.multiPropertiesList) {
				contentLines[i] = contentLines[i].replaceAll(PropertyBean.key, PropertyBean.content);
			}
		}

		// =======================================================
		// 檔案輸出
		// =======================================================
		fu.writeToFile(filePath + ".trns");

		// =======================================================
		// 未定義字串訊息
		// =======================================================
		List<Map.Entry<String, Integer>> list_Data = new ArrayList<Map.Entry<String, Integer>>(undefinedMap.entrySet());

		// 依次數排序
		Collections.sort(list_Data, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
				return (entry1.getValue().compareTo(entry2.getValue()));
			}
		});

		for (Map.Entry<String, Integer> entry : list_Data) {
			System.out.println("[" + entry.getKey() + "] " + entry.getValue());
		}

		// for (Entry<String, Integer> entry : undefinedMap.entrySet()) {
		// System.out.println("[" + entry.getKey() + "] " + entry.getValue());
		// }
	}

	/**
	 * 重新整理設定檔後輸出
	 *
	 * @param filePath
	 * @param fileEncodeing
	 * @throws Exception
	 */
	public void outputPropertyFile(String filePath, String fileEncodeing) throws Exception {

		FileUtil fu = new FileUtil();
		fu.setBROKEN_LINE_SYMBOL("\r\n");
		fu.addLine("###################################################");
		fu.addLine("# 單字元");
		fu.addLine("###################################################");

		for (int i = this.singlePropertiesList.size() - 1; i >= 0; i--) {
			PropertyBean bean = this.singlePropertiesList.get(i);
			fu.addLine(bean.key.replaceAll(" ", "　") + "=" + bean.content);
			//fu.addLine(bean.key + "=" + bean.content);
		}

		fu.addLine("###################################################");
		fu.addLine("# 雙字元");
		fu.addLine("###################################################");

		for (int i = this.multiPropertiesList.size() - 1; i >= 0; i--) {
			PropertyBean bean = this.multiPropertiesList.get(i);
			fu.addLine(bean.key.replaceAll(" ", "　") + "=" + bean.content);
			//fu.addLine(bean.key + "=" + bean.content);
		}
		fu.writeToFile(filePath);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String filePath = "d:/Dropbox/小說/神話版三國.txt";
		String outputPropertyPath = "d:/Dropbox/小說/Replace16k.properties";
		Replace16k replace16k = new Replace16k();
		replace16k.replaceContent(filePath, "utf8");
		replace16k.outputPropertyFile(outputPropertyPath, "utf8");
	}
}
