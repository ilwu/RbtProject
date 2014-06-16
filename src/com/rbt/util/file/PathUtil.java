package com.rbt.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * @author Allen Wu
 */
public class PathUtil {

	/**
	 * 建構子
	 */
	public PathUtil(){
	}


	//=========================================================================================
	//檢查路徑是否存在，若不存在則新增
	//=========================================================================================
	/**
	 * 檢查路徑是否存在，若不存在則新增(減少 I/O 負荷，由最後一層檢核回來)
	 * @param path 路徑字串 (字串中"\"請以"\\"代替)
	 * @throws Exception
	 */
	public void chkAndCreateDir(String path) throws Exception{

		if(path==null||path.equalsIgnoreCase("")){
			throw new Exception("傳入路徑字串為空");
		}
		this.recursionChk(this.splitPath(path));
	}

	/**
	 * 將路徑字串分割
	 * @param path 路徑字串
	 * @return
	 */
	private List<String> splitPath(String path){

		//使字串中目錄符號(File.separator)統一
		path = new File(path).getPath();

		//路徑字串的 LIST
		List<String> pathPartList = new ArrayList<String>();

		for(int i=0;i<path.length();i++){
			if(File.separator.equals(String.valueOf(path.charAt(i)))){
				pathPartList.add(path.substring(0,i));
			}
		}
		pathPartList.add(path);

		return pathPartList;
	}

	/**
	 * 檢查路徑是否存在，若不存在則新增(減少 I/O 負荷，由最後一層檢核回來)
	 * @param pathPartList 處理後之路徑字串
	 */
	private String recursionChk(List<String> pathPartList){

		if(pathPartList == null || pathPartList.size()<1) return "";

		File dir= new File(pathPartList.get(pathPartList.size()-1));

		//取得路徑字串
		String filePath = pathPartList.get(pathPartList.size()-1);

		try {
			//檢查路徑是否存在
			if (!dir.exists() || !dir.isDirectory()) {

				//檢查上層路徑是否存在
				if(pathPartList.size()>1){
					//將最底層（最後一筆去除，並傳入檢查）
					pathPartList.remove(pathPartList.size()-1);
					this.recursionChk(pathPartList);
				}

				//創建目錄，若失敗則傳回 ""
				if(dir.mkdir()){
					System.out.println("創建目錄:" + filePath);
				}else{
					System.out.println("創建目錄失敗:" + filePath);
					filePath = "";
				}
			}
		} catch (Exception e) {
			filePath = "";
			e.printStackTrace();
		} finally{
			dir = null;
		}
		return filePath;
	}


	//=========================================================================================
	//刪除目錄下所有檔案
	//=========================================================================================
	/**
	 * 刪除檔案、目錄下所有子目錄、檔案
	 * @param path 路徑
	 */
	public void  deleteAll(File path){

		if(!path.exists())   return;
		if(path.isFile()){
			path.delete();
			return;
		}
		File[] files = path.listFiles();
		for(int i=0;i<files.length;i++){
			deleteAll(files[i]);
		}
		path.delete();
	}

	//=========================================================================================
	//取得目錄下所有檔案名稱
	//=========================================================================================

	/**
	 * 取得目錄下所有檔案名稱(包含子目錄)
	 * @param path			路徑或檔案完整路徑
	 * @param fileSubName	要取得的副檔名
	 * @return List <String['子路徑','檔案名稱']>
	 */
	public List<String[]> readDir(String path,String fileSubName){
		File file = new File(path);
		List<String[]> fileList  = new LinkedList<String[]>();
		this.visitAllFiles(file,fileSubName,fileList,file.getPath());
		return fileList;
	}

	/**
	 * 遞迴取得所有路徑下檔案名稱與子路徑
	 * @param path			路徑或檔案完整路徑
	 * @param fileSubName	要取得的副檔名
	 * @param fileList		存放結果的 list
	 * @param rootPath		原始搜尋目錄
	 */
	private void visitAllFiles(File path,String fileSubName,List<String[]> fileList,String rootPath) {

		if (path.isDirectory()) {
			String[] children = path.list();
			for (int i=0; i<children.length; i++) {
				visitAllFiles(new File(path, children[i]),fileSubName,fileList,rootPath);
			}
		} else {
			if (path.getName().matches(".*\\."+fileSubName+"$")) {
				String fullPath = path.getPath();
				fileList.add(new String[] {fullPath.substring(rootPath.length(),fullPath.indexOf(path.getName())),path.getName()});
			}
		}
	}

	/**
	 * 取得目前路徑
	 * @return
	 */
	public String getCurrentDirectory(){
		File now_directory = new File(".");
		String current_path = now_directory.getAbsolutePath().replaceAll("\\.", "");
		return current_path;
	}

	public static void main(String[] args) {
		String path = "h:\\ test/1\\2\\3\\4";
		try {
			new PathUtil().chkAndCreateDir(path);
		} catch (Exception e) {
			//TOD Auto-generated catch block
			e.printStackTrace();
		}
	}
}
